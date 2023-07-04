package com.graduationproject.robokidsapp.ui.parentsFragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.databinding.FragmentParentsORKidsBinding
import java.util.*

class ParentsORKidsFragment : Fragment() {
    private var _binding: FragmentParentsORKidsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentParentsORKidsBinding.inflate(inflater, container, false)

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR

        if(!isLeftToRight){
            binding.kidsEntry.setImageResource(R.drawable.kids_arabic)
            binding.parentsEntry.setImageResource(R.drawable.parents_arabic)
        }

        binding.kidsEntry.setOnClickListener {
            val action = ParentsORKidsFragmentDirections.actionParentsORKidsFragmentToContentEnterSplashFragment(Child())
            mNavController.navigate(action)
        }

        binding.parentsEntry.setOnClickListener {
            if(auth.currentUser != null){
                val action = ParentsORKidsFragmentDirections.actionParentsORKidsFragmentToParentsHomeFragment()
                mNavController.navigate(action)
            }else{
                val action = ParentsORKidsFragmentDirections.actionParentsORKidsFragmentToWelcomeFragment()
                mNavController.navigate(action)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}