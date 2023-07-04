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
import com.graduationproject.robokidsapp.databinding.FragmentSettingBinding
import com.graduationproject.robokidsapp.ui.parentsFragments.info.ParentsHomeFragment
import java.util.*

class SettingFragment : Fragment() {
    private lateinit var mNavController: NavController

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR

        if(!isLeftToRight){
            binding.ivAcountArrow.setImageResource(R.drawable.left_arrow)
            binding.ivPasswordArrow.setImageResource(R.drawable.left_arrow)
        }

        binding.goToUpdateAccount.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToUpdateAccountFragment()
            mNavController.navigate(action)
        }

        binding.goToChangePassword.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToChangePasswordFragment()
            mNavController.navigate(action)
        }

        binding.ivBack.setOnClickListener {
            ParentsHomeFragment.mNavController.currentBackStackEntry?.let { backEntry ->
                ParentsHomeFragment.mNavController.popBackStack(backEntry.destination.id, true)
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}