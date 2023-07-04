package com.graduationproject.robokidsapp.ui.parentsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.CommonQuestionsAdapter
import com.graduationproject.robokidsapp.databinding.FragmentCommonQuestionsBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CommonQuestionsFragment : Fragment() {
    private lateinit var mNavController: NavController

    private var _binding: FragmentCommonQuestionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var questionList:List<String>
    private lateinit var answerList:HashMap<String, List<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommonQuestionsBinding.inflate(inflater, container, false)

        showList()

        val adapter = CommonQuestionsAdapter(requireContext() , questionList , answerList)
        binding.elvCommonQuestions.setAdapter(adapter)

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR
        if(!isLeftToRight){
            binding.elvCommonQuestions.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }else{
            binding.elvCommonQuestions.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        binding.ivBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        return binding.root
    }

    private fun showList() {
        questionList = ArrayList()
        answerList = HashMap()

        (questionList as ArrayList).add(getString(R.string.question1))
        (questionList as ArrayList).add(getString(R.string.question2))
        (questionList as ArrayList).add(getString(R.string.question3))
        (questionList as ArrayList).add(getString(R.string.question4))
        (questionList as ArrayList).add(getString(R.string.question5))
        (questionList as ArrayList).add(getString(R.string.question6))
        (questionList as ArrayList).add(getString(R.string.question7))
        (questionList as ArrayList).add(getString(R.string.question8))


        val listAnswer1 = ArrayList<String>()
        listAnswer1.add(getString(R.string.answer1))

        val listAnswer2 = ArrayList<String>()
        listAnswer2.add(getString(R.string.answer2))

        val listAnswer3 = ArrayList<String>()
        listAnswer3.add(getString(R.string.answer3))

        val listAnswer4 = ArrayList<String>()
        listAnswer4.add(getString(R.string.answer4))

        val listAnswer5 = ArrayList<String>()
        listAnswer5.add(getString(R.string.answer5))

        val listAnswer6 = ArrayList<String>()
        listAnswer6.add(getString(R.string.answer6))

        val listAnswer7 = ArrayList<String>()
        listAnswer7.add(getString(R.string.answer7))

        val listAnswer8 = ArrayList<String>()
        listAnswer8.add(getString(R.string.answer8))

        answerList[questionList[0]] = listAnswer1
        answerList[questionList[1]] = listAnswer2
        answerList[questionList[2]] = listAnswer3
        answerList[questionList[3]] = listAnswer4
        answerList[questionList[4]] = listAnswer5
        answerList[questionList[5]] = listAnswer6
        answerList[questionList[6]] = listAnswer7
        answerList[questionList[7]] = listAnswer8



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}