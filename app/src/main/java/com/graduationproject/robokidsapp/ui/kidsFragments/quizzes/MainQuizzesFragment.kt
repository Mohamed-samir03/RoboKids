package com.graduationproject.robokidsapp.ui.kidsFragments.quizzes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.databinding.FragmentMainQuizzesBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth

class MainQuizzesFragment : Fragment() {
    private var _binding: FragmentMainQuizzesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    private lateinit var typeQuiz:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("mainQuizzes-")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainQuizzesBinding.inflate(inflater, container, false)

        typeQuiz = arguments?.getString("sectionType")!!

        binding.mainQuizzesQuiz1.setOnClickListener {
            val action = MainQuizzesFragmentDirections.actionMainQuizzesFragment2ToEnglishQuiz1Fragment(typeQuiz)
            mNavController.navigate(action)
        }

        binding.mainQuizzesQuiz2.setOnClickListener {
            if(typeQuiz == "Arabic" || typeQuiz == "English"){
                val action = MainQuizzesFragmentDirections.actionMainQuizzesFragment2ToQuizSoundLittersFragment(typeQuiz)
                mNavController.navigate(action)
            }else{
                val action = MainQuizzesFragmentDirections.actionMainQuizzesFragment2ToMathQuiz1Fragment()
                mNavController.navigate(action)
            }
        }

        binding.mainQuizzesBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}