package com.graduationproject.robokidsapp.ui.kidsFragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.WhiteboardAdapter
import com.graduationproject.robokidsapp.databinding.FragmentWhiteboardBinding
import com.graduationproject.robokidsapp.data.model.CustomCanvas
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WhiteboardFragment : Fragment() {
    private lateinit var mNavController: NavController

    lateinit var listWhiteBoardContent: ArrayList<ImageContent>
    lateinit var typeLetter: String
    var isImage: Boolean = false
    private lateinit var startDate: Date
    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var adapter: WhiteboardAdapter

    companion object {
        var path: Path = Path()
        var paint_brush = Paint()

        private var _binding: FragmentWhiteboardBinding? = null
        val binding get() = _binding!!

        var seekBar_size = 10f
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        startDate = Date()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWhiteboardBinding.inflate(inflater, container, false)


        CustomCanvas.pathList.clear()
        CustomCanvas.colorList.clear()
        CustomCanvas.sizeList.clear()
        path.reset()

        binding.customToolbar.inflateMenu(R.menu.whiteboard_menu)

        binding.customToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pencil -> {
                    currentColor(paint_brush.color)
                    true
                }
                R.id.eraser -> {
                    CustomCanvas.pathList.clear()
                    CustomCanvas.colorList.clear()
                    CustomCanvas.sizeList.clear()
                    path.reset()
                    true
                }
                R.id.undo -> {
                    binding.mCanvas.undo()
                    true
                }
                R.id.redo -> {
                    binding.mCanvas.redo()
                    true
                }
                R.id.black -> {
                    paint_brush.color = Color.BLACK
                    currentColor(paint_brush.color)
                    true
                }
                R.id.red -> {
                    paint_brush.color = Color.RED
                    currentColor(paint_brush.color)
                    true
                }
                R.id.green -> {
                    paint_brush.color = Color.GREEN
                    currentColor(paint_brush.color)
                    true
                }
                R.id.purple -> {
                    paint_brush.color = Color.MAGENTA
                    currentColor(paint_brush.color)
                    true
                }
                R.id.yellow -> {
                    paint_brush.color = Color.YELLOW
                    currentColor(paint_brush.color)
                    true
                }
                R.id.blue -> {
                    paint_brush.color = Color.BLUE
                    currentColor(paint_brush.color)
                    true
                }
                else -> false
            }
        }


        typeLetter = arguments?.getString("learnSection")!!
        // this fill arrayList by type letters
        setDataInArrayList(typeLetter)


        adapter = WhiteboardAdapter(requireContext(), isImage, contentViewModel.storageInstance)
        binding.rvWhiteboard.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWhiteboard.adapter = adapter
        binding.rvWhiteboard.setHasFixedSize(true)
        adapter.updateList(listWhiteBoardContent)


        binding.seekBarSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                seekBar_size = p1.toFloat()
                currentSize(seekBar_size)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })


        binding.whiteboardBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }

        return binding.root
    }

    // get image of whiteBoard and save in local device
    public fun savePhoto() {
        val bmp = binding.mCanvas.getBitmap()
        var imageOutStream: OutputStream? = null

        val cv = ContentValues()

        // name of the file
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png")

        // type of the file
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        // location of the file to be saved
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        // get the Uri of the file which is to be created in the storage
        val uri: Uri = activity?.getContentResolver()
            ?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)!!
        try {
            // open the output stream with the above uri
            imageOutStream = activity?.getContentResolver()!!.openOutputStream(uri)

            // this method writes the files in storage
            bmp?.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream)
            Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
            // close the output stream after use
            imageOutStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun currentColor(c: Int) {
        CustomCanvas.currentBrush = c
        path = Path()
    }

    fun currentSize(size: Float) {
        CustomCanvas.currentSize = size
        path = Path()
    }


    fun setDataInArrayList(type: String) {
        listWhiteBoardContent = ArrayList()
        when (type) {
            "Arabic" -> {
                arduinoBluetooth.sendMessage("wbArabic-")  // send message to arduino bluetooth
                for (i in 1569..1594) {
                    listWhiteBoardContent.add(ImageContent("", "" + i.toChar(), ""))
                }
                for (i in 1601..1610) {
                    listWhiteBoardContent.add(ImageContent("", "" + i.toChar(), ""))
                }

                isImage = false
                binding.tvText.visibility = View.VISIBLE
                binding.rvWhiteboard.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            "English" -> {
                arduinoBluetooth.sendMessage("wbEnglish-")
                for (i in 'A'..'Z') {
                    listWhiteBoardContent.add(ImageContent("", "" + i, ""))
                }
                isImage = false
                binding.tvText.visibility = View.VISIBLE
                binding.rvWhiteboard.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            "Math" -> {
                arduinoBluetooth.sendMessage("wbNumbers-")
                for (i in 0..100) {
                    listWhiteBoardContent.add(ImageContent("", "" + i, ""))
                }
                isImage = false
                binding.tvText.visibility = View.VISIBLE
                binding.rvWhiteboard.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            "Photo" -> {
                arduinoBluetooth.sendMessage("wbImageNames-")
                binding.knowImageLayout.visibility = View.VISIBLE
                contentViewModel.getWhiteboardContent()
                observerGetWhiteboardContent()
                isImage = true
                binding.rvWhiteboard.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            else -> println("invalid type")
        }
    }

    private fun observerGetWhiteboardContent() {
        contentViewModel.whiteboardContent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
//                    binding.progressBarEntertainmentSection.show()
                }
                is Resource.Failure -> {
//                    binding.progressBarEntertainmentSection.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
//                    binding.progressBarEntertainmentSection.hide()
                    adapter.updateList(resource.data)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val endTime = simpleDateFormat.format(Date())
        val startTime = simpleDateFormat.format(startDate)
        if (typeLetter == "Arabic") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.boardArabic) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else if (typeLetter == "English") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.boardEnglish) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else if (typeLetter == "Math") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.boardMath) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else {
            ContentFragment.listOfNotifications.add(
                getString(R.string.boardPhoto) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}