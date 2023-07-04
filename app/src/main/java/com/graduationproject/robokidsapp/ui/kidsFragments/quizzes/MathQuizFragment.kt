package com.graduationproject.robokidsapp.ui.kidsFragments.quizzes

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.databinding.FragmentMathQuizBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentFragment
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentViewModel
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MathQuizFragment : Fragment() {
    private var _binding: FragmentMathQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private lateinit var listImage: ArrayList<ImageContent>
    private lateinit var listOperator: ArrayList<String>
    private lateinit var mediaPlayer: MediaPlayer
    private val contentViewModel: ContentViewModel by viewModels()
    private var counter = 0
    private var numberTrue = 0
    private var numberFalse = 0


    companion object {
        var textNumber = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        listImage = ArrayList()
        listOperator = ArrayList()
        mediaPlayer = MediaPlayer()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("mathQuiz-")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMathQuizBinding.inflate(inflater, container, false)


        binding.btn0.setOnClickListener { changeNumber("0") }
        binding.btn1.setOnClickListener { changeNumber("1") }
        binding.btn2.setOnClickListener { changeNumber("2") }
        binding.btn3.setOnClickListener { changeNumber("3") }
        binding.btn4.setOnClickListener { changeNumber("4") }
        binding.btn5.setOnClickListener { changeNumber("5") }
        binding.btn6.setOnClickListener { changeNumber("6") }
        binding.btn7.setOnClickListener { changeNumber("7") }
        binding.btn8.setOnClickListener { changeNumber("8") }
        binding.btn9.setOnClickListener { changeNumber("9") }
        binding.btnMuns.setOnClickListener { changeNumber("-") }

        binding.delete.setOnClickListener {
            if (textNumber.isNotEmpty()) {
                textNumber = textNumber.substring(0, textNumber.length - 1)
                binding.tvMathQuiz1Result.text = textNumber
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    mediaPlayer.stop()
                    mediaPlayer.seekTo(0)
                }
                mediaPlayer = MediaPlayer.create(requireContext(), R.raw.click)
                mediaPlayer.start()
            }
        }

        contentViewModel.getQuizContent("Math")
        observerGetImages()

        binding.mathQuiz1CheckAnswer.setOnClickListener {
            val answerText = binding.tvMathQuiz1Result.text
            if (answerText.isNotEmpty()) {
                if (answerText.count { it == '-' } > 1 ||
                    (answerText.contains("-") && (answerText.length == 1 || answerText.first() != '-')) ) {
                    return@setOnClickListener
                }

                val answer:Int = answerText.trim().toString().toInt()
                val operator = binding.tvOperator.text.trim().toString()
                val result:Int
                if (operator == "+"){
                    result = listImage[counter-2].imageName.toInt() + listImage[counter-1].imageName.toInt()
                }else{
                    result = listImage[counter-2].imageName.toInt() - listImage[counter-1].imageName.toInt()
                }
                if (answer == result){
                    arduinoBluetooth.sendMessage("correct-")  // send result to arduino bluetooth
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.pause()
                        mediaPlayer.stop()
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer = MediaPlayer.create(requireContext() , R.raw.soundcorrect)
                    mediaPlayer.start()
                    numberTrue++
                    binding.numberTrue.text = numberTrue.toString()
                    if (counter < 12){
                        binding.tvMathQuiz1Result.text = ""
                        textNumber = ""
                        Glide.with(this).load(listImage[counter++].imageUrl).into(binding.mathQuizImage1)
                        Glide.with(this).load(listImage[counter++].imageUrl).into(binding.mathQuizImage2)
                        binding.tvOperator.text = listOperator[(counter/2)-1]
                    }
                }else{
                    arduinoBluetooth.sendMessage("inCorrect-")  // send result to arduino bluetooth
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.pause()
                        mediaPlayer.stop()
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer = MediaPlayer.create(requireContext() , R.raw.soundincorrect)
                    mediaPlayer.start()
                    numberFalse++
                    binding.numberFalse.text = numberFalse.toString()
                    if(numberFalse == 5){
                        arduinoBluetooth.sendMessage("badResult-")
                        showBadDialog()
                    }
                }

                if (counter == listImage.size){
                    arduinoBluetooth.sendMessage("goodResult-")
                    showGoodDialog()
                }
            }
        }


        binding.mathQuiz1Exit.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }

        return binding.root
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

    private fun disPlayData() {
        listOperator.add("+")
        listOperator.add("+")
        listOperator.add("-")
        listOperator.add("-")
        listOperator.add("+")
        listOperator.add("-")

        listImage.shuffle()
        listOperator.shuffle()
        Glide.with(this).load(listImage[counter++].imageUrl).into(binding.mathQuizImage1)
        Glide.with(this).load(listImage[counter++].imageUrl).into(binding.mathQuizImage2)
        binding.tvOperator.text = listOperator[(counter / 2) - 1]
    }

    private fun changeNumber(num: String) {
        if (textNumber.length < 3) {
            textNumber += num
            binding.tvMathQuiz1Result.text = textNumber
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.stop()
                mediaPlayer.seekTo(0)
            }
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.click)
            mediaPlayer.start()
        }
    }

    private fun showGoodDialog() {
        var flag = false
        val view = layoutInflater.inflate(R.layout.custom_layout_good_result, null, false)

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setView(view)
        val alert = dialog.create()

        alert.setOnDismissListener {
            if (!flag) showGoodDialog()
        }

        alert.show()

        val button = view.findViewById<Button>(R.id.btn_moreQuestions)
        var tvTrue = view.findViewById<TextView>(R.id.number_true)
        var tvFalse = view.findViewById<TextView>(R.id.number_false)

        tvTrue.text = numberTrue.toString()
        tvFalse.text = numberFalse.toString()

        ContentFragment.listOfNotifications.add(
            getString(R.string.calcMath) + " $numberTrue ${
                getString(
                    R.string.isTrue
                )
            }, $numberFalse ${getString(R.string.isFalse)} (${getString(R.string.very_good)})"
        )

        button.setOnClickListener {
            binding.tvMathQuiz1Result.text = ""
            textNumber = ""
            val action = MathQuizFragmentDirections.actionMathQuiz1FragmentSelf()
            mNavController.navigate(action)
            alert.dismiss()
            flag = true
        }
    }

    private fun showBadDialog() {
        var flag = false
        val view = layoutInflater.inflate(R.layout.custom_layout_bad_result, null, false)

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setView(view)
        val alert = dialog.create()

        alert.setOnDismissListener {
            if (!flag) showBadDialog()
        }

        ContentFragment.listOfNotifications.add(
            getString(R.string.calcMath) + " $numberTrue ${
                getString(
                    R.string.isTrue
                )
            }, $numberFalse ${getString(R.string.isFalse)} (${getString(R.string.very_bad)})"
        )
        alert.show()

        val button = view.findViewById<Button>(R.id.btn_tryAgain)

        button.setOnClickListener {
            binding.tvMathQuiz1Result.text = ""
            textNumber = ""
            val action = MathQuizFragmentDirections.actionMathQuiz1FragmentSelf()
            mNavController.navigate(action)
            alert.dismiss()
            flag = true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        textNumber = ""
    }

}