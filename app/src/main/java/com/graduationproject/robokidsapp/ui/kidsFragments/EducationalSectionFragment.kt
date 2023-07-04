package com.graduationproject.robokidsapp.ui.kidsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.EducationalSectionsAdapter
import com.graduationproject.robokidsapp.databinding.FragmentEducationalSectionBinding
import com.graduationproject.robokidsapp.data.model.EducationalSections
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets
import com.graduationproject.robokidsapp.util.hide


class EducationalSectionFragment : Fragment(), EducationalSectionsAdapter.OnItemClickListener {
    private var _binding: FragmentEducationalSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController
    private lateinit var listSection: ArrayList<EducationalSections>

    private lateinit var sectionData: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEducationalSectionBinding.inflate(inflater, container, false)

        if (ContentFragment.currentChild.childName == "") { // is not register
            binding.educationalSectionImgChild.hide()
        } else {
            val imageDrawable =
                getChildAvatarFormAssets(ContentFragment.currentChild.childAvatar, requireContext())
            binding.educationalSectionImgChild.setImageDrawable(imageDrawable)
        }

        // get section name from content screen
        sectionData = arguments?.getString("currentSection")!!

        listSection = ArrayList()

        if (sectionData == "Pronunciation") {
           arduinoBluetooth.sendMessage("educSecPro-")  // send message to arduino bluetooth
            listSection.add(EducationalSections("Arabic", R.drawable.speek_arapic))
            listSection.add(EducationalSections("English", R.drawable.speek_abc))
            listSection.add(EducationalSections("Math", R.drawable.speek_123))
            listSection.add(EducationalSections("ImageKnow", R.drawable.animals))
        } else if (sectionData == "Board") {
            arduinoBluetooth.sendMessage("educSecBoard-")
            listSection.add(EducationalSections("Arabic", R.drawable.board_arapic))
            listSection.add(EducationalSections("English", R.drawable.board_abc))
            listSection.add(EducationalSections("Math", R.drawable.board_123))
            listSection.add(EducationalSections("Photo", R.drawable.animals))
        } else if (sectionData == "Questions") {
            arduinoBluetooth.sendMessage("educSecQues-")
            listSection.add(EducationalSections("Arabic", R.drawable.ques_arapic))
            listSection.add(EducationalSections("English", R.drawable.ques_abc))
            listSection.add(EducationalSections("Math", R.drawable.math))
        }


        val adapter = EducationalSectionsAdapter(requireContext(), listSection, this)
        binding.rvEducationSection.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEducationSection.adapter = adapter
        binding.rvEducationSection.setHasFixedSize(true)

        if (sectionData == "Pronunciation") {
            binding.educationalSectionContentName.text = getString(R.string.Pronunciation)
        } else if (sectionData == "Board") {
            binding.educationalSectionContentName.text = getString(R.string.Board)
        } else {
            binding.educationalSectionContentName.text = getString(R.string.Questions)
        }

        binding.educationalSectionBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }

        return binding.root
    }

    var character = ArrayList<Char>()

    override fun onItemClick(position: Int) {
        val section = listSection[position]
        when (sectionData) {
            "Pronunciation" -> {
                val action =
                    EducationalSectionFragmentDirections.actionEducationalSectionFragmentToPronunciationLettersFragment(
                        section.sectionName
                    )
                mNavController.navigate(action)
            }
            "Board" -> {
                val action =
                    EducationalSectionFragmentDirections.actionEducationalSectionFragmentToWhiteboardFragment(
                        section.sectionName
                    )
                mNavController.navigate(action)
            }
            "Questions" -> {
                val action =
                    EducationalSectionFragmentDirections.actionEducationalSectionFragmentToMainQuizzesFragment2(
                        section.sectionName
                    )
                mNavController.navigate(action)
            }
            else -> println("invalid section Data")

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


