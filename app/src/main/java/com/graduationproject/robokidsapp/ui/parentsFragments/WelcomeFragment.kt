package com.graduationproject.robokidsapp.ui.parentsFragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    // this action
    private lateinit var mNavController: NavController


    override fun onResume() {
        super.onResume()
        //show status bar
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.window!!.statusBarColor = this.resources.getColor(R.color.statusBar_color)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)


        binding.btnWelcomeLogin.setOnClickListener {
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
            mNavController.navigate(action)
        }

        binding.btnWelcomeNewAccount.setOnClickListener {
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToRegisterFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }


}