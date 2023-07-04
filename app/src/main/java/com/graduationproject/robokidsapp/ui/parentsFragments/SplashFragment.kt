package com.graduationproject.robokidsapp.ui.parentsFragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.ui.parentsFragments.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var mNavController: NavController

    private val authViewModel: AuthViewModel by viewModels()

    private var isLoggedIn = false

    override fun onResume() {
        super.onResume()
        //Hide status bar
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // check is logged in or not
        if (authViewModel.currentUser != null)
            isLoggedIn = true

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)

            withContext(Dispatchers.Main) {
                if (isLoggedIn) {
                    val action = SplashFragmentDirections.actionSplashFragmentToHomeKidsFragment()
                    mNavController.navigate(action)
                } else {
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToParentsORKidsFragment()
                    mNavController.navigate(action)
                }

            }
        }
    }


}