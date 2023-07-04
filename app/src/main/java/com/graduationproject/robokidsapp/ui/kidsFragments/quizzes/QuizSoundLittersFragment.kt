package com.graduationproject.robokidsapp.ui.kidsFragments.quizzes

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.databinding.FragmentQuizSoundLittersBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentFragment
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentViewModel
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizSoundLittersFragment : Fragment() {
    private var _binding: FragmentQuizSoundLittersBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController

    private lateinit var listImages: ArrayList<ImageContent>
    private lateinit var listSelectedImages:ArrayList<ImageContent>
    private lateinit var targetLetter: ImageContent
    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var language:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        mediaPlayer = MediaPlayer()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("imageSoundQuiz-")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizSoundLittersBinding.inflate(inflater, container, false)

        listImages = arrayListOf()

        language = arguments?.getString("language")!!

        contentViewModel.getPronunciationContent(language)
        observerGetImages()

        binding.letter1.setOnClickListener {
            if(listSelectedImages[0].imageName == targetLetter.imageName){
                arduinoBluetooth.sendMessage("correct-")  // send result to arduino bluetooth

                binding.letter1.setBackgroundResource(R.color.green)
                binding.animationCorrect.visibility = View.VISIBLE
                binding.animationIncorrect.visibility = View.GONE
                binding.letter1.isEnabled = false
                binding.letter2.isEnabled = false
                binding.letter3.isEnabled = false
                binding.animationNext.playAnimation()
                binding.tvNext.isEnabled = true
            }else{
                arduinoBluetooth.sendMessage("inCorrect-")  // send result to arduino bluetooth

                binding.letter1.setBackgroundResource(R.color.red)
                binding.animationCorrect.visibility = View.GONE
                binding.animationIncorrect.visibility = View.VISIBLE
                binding.letter1.isEnabled = false
            }
        }

        binding.letter2.setOnClickListener {
            if(listSelectedImages[1].imageName == targetLetter.imageName){
                arduinoBluetooth.sendMessage("correct-")  // send result to arduino bluetooth

                binding.letter2.setBackgroundResource(R.color.green)
                binding.animationCorrect.visibility = View.VISIBLE
                binding.animationIncorrect.visibility = View.GONE
                binding.letter1.isEnabled = false
                binding.letter2.isEnabled = false
                binding.letter3.isEnabled = false
                binding.animationNext.playAnimation()
                binding.tvNext.isEnabled = true
            }else{
                arduinoBluetooth.sendMessage("inCorrect-")  // send result to arduino bluetooth

                binding.letter2.setBackgroundResource(R.color.red)
                binding.animationCorrect.visibility = View.GONE
                binding.animationIncorrect.visibility = View.VISIBLE
                binding.letter2.isEnabled = false
            }
        }

        binding.letter3.setOnClickListener {
            if(listSelectedImages[2].imageName == targetLetter.imageName){
                arduinoBluetooth.sendMessage("correct-")  // send result to arduino bluetooth

                binding.letter3.setBackgroundResource(R.color.green)
                binding.animationCorrect.visibility = View.VISIBLE
                binding.animationIncorrect.visibility = View.GONE
                binding.letter1.isEnabled = false
                binding.letter2.isEnabled = false
                binding.letter3.isEnabled = false
                binding.animationNext.playAnimation()
                binding.tvNext.isEnabled = true
            }else{
                arduinoBluetooth.sendMessage("inCorrect-")  // send result to arduino bluetooth

                binding.letter3.setBackgroundResource(R.color.red)
                binding.animationCorrect.visibility = View.GONE
                binding.animationIncorrect.visibility = View.VISIBLE
                binding.letter3.isEnabled = false
            }
        }

        binding.ivSound.setOnClickListener {
            binding.animationWaves.playAnimation()

            mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.release()
                mediaPlayer.reset()
                mediaPlayer.seekTo(0)
            }
            try {
                mediaPlayer.setDataSource(targetLetter.imageVoice)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                toast(e.message)
            }

        }


        binding.tvNext.setOnClickListener {
            binding.letter1.isEnabled = true
            binding.letter2.isEnabled = true
            binding.letter3.isEnabled = true
            binding.animationCorrect.visibility = View.GONE
            binding.animationWaves.playAnimation()

            listImages.remove(targetLetter)
            if(listImages.size>=3){
                listSelectedImages.clear()
                getThreeRandomImage()
            }


            if(listImages.size <=2){
                val action =  QuizSoundLittersFragmentDirections.actionQuizSoundLittersFragmentSelf(language)
                mNavController.navigate(action)
            }
        }


        binding.quizSoundLittersExit.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id, true) }
        }

        return binding.root
    }

    private fun observerGetImages() {
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
                    getThreeRandomImage()
                }
            }
        }
    }


    fun getThreeRandomImage(){
        listImages.shuffle()
        listSelectedImages = arrayListOf()
        for (i in 0..2){
            listSelectedImages.add(listImages[i])
        }

        targetLetter = listSelectedImages.random()

        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(targetLetter.imageVoice)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Glide.with(this).load(listImages[0].imageUrl).into(binding.letter1)
        Glide.with(this).load(listImages[1].imageUrl).into(binding.letter2)
        Glide.with(this).load(listImages[2].imageUrl).into(binding.letter3)

        binding.letter1.setBackgroundResource(0)
        binding.letter2.setBackgroundResource(0)
        binding.letter3.setBackgroundResource(0)

        binding.tvNext.isEnabled = false
        binding.animationNext.pauseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (language == "Arabic"){
            ContentFragment.listOfNotifications.add(getString(R.string.selectArabic))
        }else{
            ContentFragment.listOfNotifications.add(getString(R.string.selectEnglish))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}