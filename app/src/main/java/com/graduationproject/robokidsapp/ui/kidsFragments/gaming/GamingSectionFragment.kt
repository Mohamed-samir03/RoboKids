package com.graduationproject.robokidsapp.ui.kidsFragments.gaming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentGamingSectionBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentFragment
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets
import com.graduationproject.robokidsapp.util.hide
import java.text.SimpleDateFormat
import java.util.*


class GamingSectionFragment : Fragment() {
    private var _binding: FragmentGamingSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController

    private lateinit var startDate: Date


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
        _binding = FragmentGamingSectionBinding.inflate(inflater, container, false)

        if (ContentFragment.currentChild.childName == "") { // is not register
            binding.gamingSectionImageChild.hide()
        } else {
            val imageDrawable = getChildAvatarFormAssets(ContentFragment.currentChild.childAvatar, requireContext())
            binding.gamingSectionImageChild.setImageDrawable(imageDrawable)
        }

        binding.XOGame.setOnClickListener {
            val action = GamingSectionFragmentDirections.actionGamingSectionFragmentToTicTacToeFragment()
            mNavController.navigate(action)
        }

        binding.memoryGame.setOnClickListener {
            val action = GamingSectionFragmentDirections.actionGamingSectionFragmentToMemoryGameFragment()
            mNavController.navigate(action)
        }

        binding.gamingSectionBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val endTime = simpleDateFormat.format(Date())
        val startTime = simpleDateFormat.format(startDate)
        ContentFragment.listOfNotifications.add(getString(R.string.secGames)+ " $startTime ${getString(R.string.out)} $endTime")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}