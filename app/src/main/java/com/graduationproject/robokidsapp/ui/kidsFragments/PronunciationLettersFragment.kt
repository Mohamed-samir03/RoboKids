package com.graduationproject.robokidsapp.ui.kidsFragments

import android.content.ContextWrapper
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.databinding.FragmentPronunciationLettersBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class PronunciationLettersFragment : Fragment() {
    private var _binding: FragmentPronunciationLettersBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    private var path: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording: Boolean = false
    private var isPlaying: Boolean = false
    private var second = 0
    private var dummySecond = 0
    private var playableSecond = 0
    lateinit var handler: Handler
    var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val contentViewModel: ContentViewModel by viewModels()

    private lateinit var startDate: Date

    private lateinit var contentType: String
    private lateinit var listImages: ArrayList<ImageContent>
    private var imageCounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()

        mediaPlayer = MediaPlayer()
        handler = Handler()
        startDate = Date()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPronunciationLettersBinding.inflate(inflater, container, false)

        contentType = arguments?.getString("content_type")!!
        listImages = ArrayList()

        contentViewModel.getPronunciationContent(contentType)
        observerGetPronunciation()

        binding.pronunciationLettersExit.setOnClickListener {
            val action =
                PronunciationLettersFragmentDirections.actionPronunciationLettersFragmentToEducationalSectionFragment(
                    "Pronunciation"
                )
            mNavController.navigate(action)
        }

        binding.pronunciationLettersEnableSpeaker.setOnClickListener {
            //playRecording()
            binding.pronunciationLettersEnableSpeaker.playAnimation()
            playSound()
        }

        binding.pronunciationLettersMic.setOnClickListener {
            startRecording()
        }
        binding.pronunciationLettersEnableMic.setOnClickListener {
            startRecording()
        }

        //  check sound
        binding.ivCheckSound.setOnClickListener {
            checkRecording()
        }

        binding.pronunciationLettersPrevious.setOnClickListener {
            binding.correctAnswer.hide()
            binding.incorrectAnswer.hide()
            binding.pronunciationLettersNext.visibility = View.VISIBLE
            binding.pronunciationLettersEnableSpeaker.playAnimation()
            if (imageCounter > 0) {
                imageCounter--
                playSound()
                Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            }
            if (imageCounter == 0) {
                binding.pronunciationLettersPrevious.visibility = View.GONE
            }
        }

        binding.pronunciationLettersNext.setOnClickListener {
            binding.correctAnswer.hide()
            binding.incorrectAnswer.hide()
            binding.pronunciationLettersPrevious.visibility = View.VISIBLE
            binding.pronunciationLettersEnableSpeaker.playAnimation()
            if (imageCounter < listImages.size - 1) {
                imageCounter++
                playSound()
                Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            }
            if (imageCounter == listImages.size - 1) {
                binding.pronunciationLettersNext.visibility = View.GONE
            }
        }

        binding.pronunciationLettersExit.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }

        return binding.root
    }

    fun checkContentType() {
        if (contentType == "Arabic") {
            arduinoBluetooth.sendMessage("proArabicLetter-")  // send message to arduino bluetooth
            Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            binding.pronunciationLettersTitle.text = getString(R.string.arabic_letters)
            playSound()
        } else if (contentType == "English") {
            arduinoBluetooth.sendMessage("proEnglishLetter-")
            Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            binding.pronunciationLettersTitle.text = getString(R.string.english_letters)
            playSound()
        } else if (contentType == "Math") {
            arduinoBluetooth.sendMessage("proNumbers-")
            Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            binding.pronunciationLettersTitle.text = getString(R.string.numbers)
            playSound()
        } else if (contentType == "ImageKnow") {
            arduinoBluetooth.sendMessage("proImageName-")
            Glide.with(this).load(listImages[imageCounter].imageUrl).into(binding.letterImage)
            binding.pronunciationLettersTitle.text = getString(R.string.photo)
            playSound()
        }
    }

    private fun observerGetPronunciation() {
        contentViewModel.pronunciationContent.observe(viewLifecycleOwner) { resource ->
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
                    listImages = resource.data
                    checkContentType()
                }
            }
        }
    }

    // this method to recording of kids
    fun startRecording() {
        if (!isRecording) {
            isRecording = true
            executor.execute {
                mediaRecorder = MediaRecorder()
                mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder!!.setOutputFile(getRecordingFilePath())
                path = getRecordingFilePath()
                mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                mediaRecorder!!.prepare()
                mediaRecorder!!.start()

                activity?.runOnUiThread {
                    playableSecond = 0
                    second = 0
                    dummySecond = 0
                    //binding.pronunciationLettersSpeaker.isEnabled = false
                    binding.pronunciationLettersEnableSpeaker.isEnabled = false
                    binding.pronunciationLettersEnableMic.visibility = View.VISIBLE
                    binding.pronunciationLettersMic.visibility = View.INVISIBLE
                    runTimer()
                }
            }
        } else {
            // this stop the record if is playing
            stopRecording()
        }
    } // end method startRecording


    private fun stopRecording() {
        executor.execute {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            playableSecond = second
            dummySecond = second
            second = 0
            isRecording = false

            activity?.runOnUiThread {
                handler.removeCallbacksAndMessages(null)
                //binding.pronunciationLettersSpeaker.isEnabled = true
                binding.pronunciationLettersEnableSpeaker.isEnabled = true
                binding.pronunciationLettersEnableMic.visibility = View.INVISIBLE
                binding.pronunciationLettersMic.visibility = View.VISIBLE
            }
        }
    }

    fun checkRecording() {
        if (isRecording) {
            stopRecording()
        }

        if (!isPlaying) {
            if (path != null) {
//                mediaPlayer!!.setDataSource(getRecordingFilePath())

                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(requireContext()))
                }
                val py = Python.getInstance()

                try {
                    if (contentType == "Arabic"){
                        val module = py.getModule("ArabicPronunciationLetters")
                        val soundText = module.callAttr("recognize_from_ar_audio", "${getRecordingFilePath()}")

                        if (soundText.toString() == listImages[imageCounter].imageName) {
                            toast(soundText.toString())
                            binding.correctAnswer.show()
                            binding.incorrectAnswer.hide()
                        } else {
                            toast(soundText.toString())
                            binding.correctAnswer.hide()
                            binding.incorrectAnswer.show()
                        }
                    }else{
                        val module = py.getModule("EnglishPronunciationLetters")
                        val soundText = module.callAttr("recognize_from_en_audio", "${getRecordingFilePath()}")

                        if (soundText.toString().trim().lowercase() == listImages[imageCounter].imageName.trim().lowercase()) {
                            toast(soundText.toString())
                            binding.correctAnswer.show()
                            binding.incorrectAnswer.hide()
                        } else {
                            toast(soundText.toString())
                            binding.correctAnswer.hide()
                            binding.incorrectAnswer.show()
                        }
                    }

                }catch (e:Exception){
                    toast(e.message)
                }

            } else {
                Toast.makeText(requireContext(), "No Recording", Toast.LENGTH_SHORT).show()
                return
            }

//            mediaPlayer!!.prepare()
//            mediaPlayer!!.start()
//            isPlaying = true
//            //binding.pronunciationLettersSpeaker.isEnabled = true
//            binding.pronunciationLettersEnableSpeaker.isEnabled = true
//            //binding.pronunciationLettersSpeaker.visibility = View.INVISIBLE
//            binding.pronunciationLettersEnableSpeaker.visibility = View.VISIBLE
//            runTimer()
        }
//        else {
//            mediaPlayer!!.stop()
//            mediaPlayer!!.release()
//            mediaPlayer = null
//            mediaPlayer = MediaPlayer()
//            isPlaying = false
//            second = 0
//            handler.removeCallbacksAndMessages(null)
//            //binding.pronunciationLettersSpeaker.isEnabled = true
//            binding.pronunciationLettersEnableSpeaker.isEnabled = true
//            binding.pronunciationLettersEnableSpeaker.visibility = View.INVISIBLE
//            //binding.pronunciationLettersSpeaker.visibility = View.VISIBLE
//        }
    }

    private fun getRecordingFilePath(): String {
        val contextWrapper = ContextWrapper(context)
        val music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(music, "testFile" + ".mp3")
        return file.path
    }

    private fun runTimer() {
        handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val minutes = (second % 3600) / 60
                val secs = second % 60
                var time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
//                textview_sound_recorder_heading.text = time

                if (isRecording || (isPlaying && playableSecond != -1)) {
                    second++
                    playableSecond--
                    if (playableSecond == -1 && isPlaying) {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        mediaPlayer == null
                        mediaPlayer = MediaPlayer()
                        playableSecond = dummySecond
                        second = 0
                        isPlaying = false
                        handler.removeCallbacksAndMessages(null)
                        binding.pronunciationLettersEnableSpeaker.visibility = View.INVISIBLE
                        //binding.pronunciationLettersSpeaker.visibility = View.VISIBLE
                        return
                    }
                }
                handler.postDelayed(this, 1000)
            }

        })
    }

    fun playSound() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer?.reset()
            mediaPlayer?.seekTo(0)
        }
        try {
            mediaPlayer?.setDataSource(listImages[imageCounter].imageVoice)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: Exception) {
            toast(e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val endTime = simpleDateFormat.format(Date())
        val startTime = simpleDateFormat.format(startDate)
        if (contentType == "Arabic") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.pronArabic) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else if (contentType == "English") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.pronEnglish) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else if (contentType == "Math") {
            ContentFragment.listOfNotifications.add(
                getString(R.string.pronMath) + " $startTime ${
                    getString(
                        R.string.out
                    )
                } $endTime"
            )
        } else {
            ContentFragment.listOfNotifications.add(
                getString(R.string.pronPhoto) + " $startTime ${
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