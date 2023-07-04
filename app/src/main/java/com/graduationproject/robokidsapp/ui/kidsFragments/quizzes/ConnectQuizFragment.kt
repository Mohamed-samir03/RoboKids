package com.graduationproject.robokidsapp.ui.kidsFragments.quizzes

import android.content.ClipData
import android.content.ClipDescription
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.databinding.FragmentConnectQuizBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentFragment
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentViewModel
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectQuizFragment : Fragment() {

    private var _binding: FragmentConnectQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private lateinit var listImage:ArrayList<ImageContent>
    private lateinit var contentType:String
    private val contentViewModel: ContentViewModel by viewModels()
    companion object{
        lateinit var idCorrect:View
        lateinit var idLayout:View
        var correctCount = 0
    }


    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listImage = ArrayList()
        mNavController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("connectQuiz-")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConnectQuizBinding.inflate(inflater, container, false)

        contentType = arguments?.getString("content_type")!!


        if (contentType == "Arabic"){
            contentViewModel.getQuizContent(contentType)
            observerGetImages()

        }else if (contentType == "English"){
            contentViewModel.getQuizContent(contentType)
            observerGetImages()
        }else{
            contentViewModel.getQuizContent(contentType)
            observerGetImages()
            binding.tvWord1.textSize = 40f
            binding.tvWord2.textSize = 40f
            binding.tvWord3.textSize = 40f
            binding.tvWord4.textSize = 40f
        }

        binding.tvWord1.setOnLongClickListener {
            idCorrect = binding.correct1
            onLongClick(it)
        }

        binding.tvWord2.setOnLongClickListener {
            idCorrect = binding.correct2
            onLongClick(it)
        }
        binding.tvWord3.setOnLongClickListener {
            idCorrect = binding.correct3
            onLongClick(it)
        }
        binding.tvWord4.setOnLongClickListener {
            idCorrect = binding.correct4
            onLongClick(it)
        }

        binding.layout1.setOnDragListener { view, dragEvent ->
            idLayout = binding.layout1
            onDrag(view,dragEvent)
        }
        binding.layout2.setOnDragListener { view, dragEvent ->
            idLayout = binding.layout2
            onDrag(view,dragEvent)
        }
        binding.layout3.setOnDragListener { view, dragEvent ->
            idLayout = binding.layout3
            onDrag(view,dragEvent)
        }
        binding.layout4.setOnDragListener { view, dragEvent ->
            idLayout = binding.layout4
            onDrag(view,dragEvent)
        }

        binding.connectQuizBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        return binding.root
    }

    private fun disPlayData() {
        listImage.shuffle()
        val list:MutableList<ImageContent> = mutableListOf()
        val listName:MutableList<String> = mutableListOf()

        for (i in 0..3){
            list.add(listImage[i])
            listName.add(listImage[i].imageName)
        }
        listName.shuffle()
        listName.shuffle()
        Glide.with(this).load(list[0].imageUrl).into(binding.img1)
        Glide.with(this).load(list[1].imageUrl).into(binding.img2)
        Glide.with(this).load(list[2].imageUrl).into(binding.img3)
        Glide.with(this).load(list[3].imageUrl).into(binding.img4)

        binding.tvWord1.text = listName[0]
        binding.tvWord2.text = listName[1]
        binding.tvWord3.text = listName[2]
        binding.tvWord4.text = listName[3]

        binding.layout1.tag = ""+list[0].imageName
        binding.layout2.tag = ""+list[1].imageName
        binding.layout3.tag = ""+list[2].imageName
        binding.layout4.tag = ""+list[3].imageName

        binding.tvWord1.tag = ""+binding.tvWord1.text
        binding.tvWord2.tag = ""+binding.tvWord2.text
        binding.tvWord3.tag = ""+binding.tvWord3.text
        binding.tvWord4.tag = ""+binding.tvWord4.text
    }

    private fun observerGetImages() {
        contentViewModel.quizContent.observe(viewLifecycleOwner) { resource ->
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
                    listImage = resource.data
                    disPlayData()
                }
            }
        }
    }

    fun onLongClick(v: View): Boolean {
        val item = ClipData.Item(""+v.tag)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(""+v.tag, mimeTypes, item)
        val dragShadow = View.DragShadowBuilder(v)
        v.startDrag(data, dragShadow, v, 0)
        return true
    }


    fun onDrag(v: View, event: DragEvent): Boolean {
        val action = event.action

        when (action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                return event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                if(v.background == null){
                    v.background = resources.getDrawable(R.drawable.bg_layout_quiz_connect)
                }

                v.invalidate()
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION ->
                return true
            DragEvent.ACTION_DRAG_EXITED -> {
                if(v.allViews.count() < 3){
                    v.setBackgroundResource(0)
                }
                v.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text.toString()
                if(dragData==v.tag.toString()){
                    v.invalidate()
                    val vw: View = event.localState as View
                    val owner = vw.parent as ViewGroup
                    owner.removeView(vw)
                    val container = v as LinearLayout
                    container.addView(vw)
                    container.background = resources.getDrawable(R.drawable.bg_layout_quiz_connect)
//                    container.setBackgroundResource(R.drawable.bg_layout_quiz_connect)
                    vw.visibility = View.VISIBLE
                    val vwv: TextView = event.localState as TextView
                    vwv.textSize = 20F

                    arduinoBluetooth.sendMessage("correct-")

                    correctCount++
                    if(correctCount==4){
                        arduinoBluetooth.sendMessage("goodResult-") // send message to arduino bluetooth

                        showWinDialog()

                        if(mediaPlayer.isPlaying){
                            mediaPlayer.pause()
                            mediaPlayer.stop()
                            mediaPlayer.seekTo(0)
                        }
                        mediaPlayer = MediaPlayer.create(requireContext() , R.raw.sound_kids)
                        mediaPlayer.start()
                    }

                    mediaPlayer = MediaPlayer.create(requireContext() , R.raw.correct_sound)
                    mediaPlayer.start()

                    return true
                }else{
                    if(v.allViews.count() < 3){
                        arduinoBluetooth.sendMessage("inCorrect-") // send message to arduino bluetooth

                        mediaPlayer = MediaPlayer.create(requireContext() , R.raw.soundincorrect)
                        mediaPlayer.start()
                        v.setBackgroundResource(0)
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                v.invalidate()
                // Does a getResult(), and displays what happened.
                if (event.result){
                    idCorrect.visibility = View.VISIBLE
                }
                return true
            }
            else -> Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
        }

        return false
    }


    private fun showWinDialog() {
        var flag = false
        val view = layoutInflater.inflate(R.layout.custom_layout_correct_quiz, null, false)

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setView(view)
        val alert = dialog.create()

        alert.setOnDismissListener {
            if(!flag) showWinDialog()
        }

        alert.show()

        val button = view.findViewById<Button>(R.id.btn_moreQuestions)

        button.setOnClickListener {
            val action = ConnectQuizFragmentDirections.actionEnglishQuiz1FragmentSelf(contentType)
            mNavController.navigate(action)


            alert.dismiss()
            correctCount = 0
            flag = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if(contentType == "Arabic"){
            ContentFragment.listOfNotifications.add(getString(R.string.connectArabic))
        }else if (contentType == "English"){
            ContentFragment.listOfNotifications.add(getString(R.string.connectEnglish))
        }else{
            ContentFragment.listOfNotifications.add(getString(R.string.connectMath))
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        correctCount = 0
    }

}