package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.CustomCanvas
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.ui.kidsFragments.WhiteboardFragment
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import java.io.ByteArrayOutputStream
import java.lang.String.format
import java.util.*


class WhiteboardAdapter(
    val context: Context,
    val isImage: Boolean,
    val storageInstance: FirebaseStorage
) : Adapter<WhiteboardAdapter.WhiteboardViewHolder>() {

    var listData: MutableList<ImageContent> = arrayListOf()

    private val START_TIME_IN_MILLIS: Long = 30000
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    val mTextViewCountDown = WhiteboardFragment.binding.timer
    var isRunning: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhiteboardViewHolder {
        if (isImage) {
            return WhiteboardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.custom_layout_image_know, parent, false)
            )
        } else {
            return WhiteboardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.custom_layout_letters, parent, false)
            )
        }

    }

    fun updateList(list: MutableList<ImageContent>) {
        this.listData = list
        notifyDataSetChanged()
    }


    var imageContent: ImageContent = ImageContent()
    override fun onBindViewHolder(holder: WhiteboardViewHolder, position: Int) {
        val data = listData[position]

        holder.bind(data)


        holder.itemView.setOnClickListener {
            if (isImage) {
                Glide.with(context).load(data.imageUrl)
                    .into(WhiteboardFragment.binding.whiteboardImageContent)
                WhiteboardFragment.binding.tvNameImage.text = data.imageName
            } else {
                WhiteboardFragment.binding.tvText.text = data.imageName
            }

            imageContent = data

            WhiteboardFragment.binding.animationCorrect.visibility = View.GONE
            WhiteboardFragment.binding.animationIncorrect.visibility = View.GONE
            CustomCanvas.pathList.clear()
            CustomCanvas.colorList.clear()
            CustomCanvas.sizeList.clear()
            WhiteboardFragment.path.reset()

            mTextViewCountDown.setTextColor(Color.parseColor("#009688"))
            mCountDownTimer?.cancel()
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText()

            itemTimer(imageContent)
        }


        // machine learning
        WhiteboardFragment.binding.whiteboardCheckAnswer.setOnClickListener {
            WhiteboardFragment.binding.animationCorrect.hide()
            WhiteboardFragment.binding.animationIncorrect.hide()
            mCountDownTimer?.cancel()
            mTimeLeftInMillis = START_TIME_IN_MILLIS
            updateCountDownText()
            val bmp = WhiteboardFragment.binding.mCanvas.getBitmap()

            uploadImageAndGetText(bmp!!, imageContent)

        }
    }

    private fun uploadImageAndGetText(bitmapImage: Bitmap, imageContent: ImageContent) {
        val resizedBitmap = increasePadding(bitmapImage, 1000, 1000)

        val stream = ByteArrayOutputStream()
        resizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        val byteImage = stream.toByteArray()

        WhiteboardFragment.binding.animationWait.show()

        val reference = storageInstance.reference.child("whiteboardImage")
        val uploadTask: UploadTask = reference.putBytes(byteImage)
        uploadTask.addOnFailureListener {
            Toast.makeText(context, "failure upload!", Toast.LENGTH_SHORT).show()
            return@addOnFailureListener
        }.addOnSuccessListener { taskSnapshot -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                reference.downloadUrl.addOnSuccessListener { url ->

                    if (!Python.isStarted()) {
                        Python.start(AndroidPlatform(context))
                    }

                    try {
                        val py = Python.getInstance()
                        val module = py.getModule("Whiteboard")
                        val imageText = module.callAttr("convertImageToText", "$url")

                        val text: String = imageText.toString().trim()

                        if (imageContent.imageUrl == "") {
                            if (text == imageContent.imageName) {
                                Toast.makeText(
                                    context,
                                    "correct: select: ${imageContent.imageName} || AI: ${text}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                WhiteboardFragment.binding.animationWait.hide()
                                WhiteboardFragment.binding.animationCorrect.show()
                                WhiteboardFragment.binding.animationIncorrect.hide()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Incorrect: select: ${imageContent.imageName} || AI: ${text}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                WhiteboardFragment.binding.animationWait.hide()
                                WhiteboardFragment.binding.animationCorrect.hide()
                                WhiteboardFragment.binding.animationIncorrect.show()
                            }
                        } else {
                            if (text.lowercase().trim() == imageContent.imageName.lowercase().trim()) {
                                WhiteboardFragment.binding.animationWait.hide()
                                WhiteboardFragment.binding.animationCorrect.show()
                                WhiteboardFragment.binding.animationIncorrect.hide()
                            } else {
                                WhiteboardFragment.binding.animationWait.hide()
                                WhiteboardFragment.binding.animationCorrect.hide()
                                WhiteboardFragment.binding.animationIncorrect.show()
                            }
                        }
                    }catch (e:Exception){
                        Toast.makeText(context, ""+e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    fun increasePadding(Src: Bitmap, padding_x: Int, padding_y: Int): Bitmap? {
        val outputImage = Bitmap.createBitmap(
            Src.width + padding_x,
            Src.height + padding_y,
            Bitmap.Config.ARGB_8888
        )
        val can = Canvas(outputImage)
        can.drawColor(Color.WHITE) //This represents White color
        can.drawBitmap(Src, padding_x.toFloat(), padding_y.toFloat(), null)


        val output = Bitmap.createBitmap(
            outputImage.width + padding_x,
            outputImage.height + padding_y,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        canvas.drawColor(Color.WHITE) //This represents White color
        canvas.drawBitmap(outputImage, 0f, 0f, null)

        return output
    }


    private fun itemTimer(imageContent: ImageContent) {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                val num = (mTimeLeftInMillis / 1000).toInt() % 60
                if (num == 10) {
                    mTextViewCountDown.setTextColor(Color.RED)
                }

                if (num == 0) {
                    val bmp = WhiteboardFragment.binding.mCanvas.getBitmap()
//                    uploadImageAndGetText(bmp!!, imageContent)
                }

                updateCountDownText()
            }

            override fun onFinish() {
//                WhiteboardFragment().savePhoto()
            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted: String = format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        mTextViewCountDown.text = timeLeftFormatted
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    inner class WhiteboardViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(content: ImageContent) {
            if (isImage) {
                val image = itemView.findViewById<ImageView>(R.id.iv_imgKnow)
                Glide.with(context).load(content.imageUrl).into(image)
            } else {
                val textView = itemView.findViewById<TextView>(R.id.tv_letter)
                textView.text = content.imageName
            }

        }
    }
}