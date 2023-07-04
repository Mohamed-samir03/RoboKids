package com.graduationproject.robokidsapp.ui.parentsFragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentChangePasswordBinding
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(), TextWatcher {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.apply {
            etOldPassword.addTextChangedListener(this@ChangePasswordFragment)
            etNewPassword.addTextChangedListener(this@ChangePasswordFragment)
            etConfirmNewPassword.addTextChangedListener(this@ChangePasswordFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this listen to live data in viewModel (changePassword)
        observer()

        binding.btnChangePassword.setOnClickListener {
            if (inputsValidation()) {
                val oldPassword = binding.etOldPassword.text.toString().trim()
                val newPassword = binding.etNewPassword.text.toString().trim()
                authViewModel.changePassword(oldPassword, newPassword, requireContext())
            }
        }

    }

    private fun observer() {
        authViewModel.changePassword.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarChangePass.show()
                }
                is Resource.Failure -> {
                    binding.progressBarChangePass.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarChangePass.hide()
                    toast(resource.data)

                    val action = ChangePasswordFragmentDirections.actionChangePasswordFragmentToLoginFragment()
                    mNavController.navigate(action)
                }
            }
        }
    }


    private fun inputsValidation(): Boolean {
        binding.apply {
            if (btnChangePassword.isEnabled) {
                val newPassword = etNewPassword.text.toString().trim()
                val confirmNewPassword = etConfirmNewPassword.text.toString().trim()

                if (newPassword.length < 6) {
                    etNewPassword.error = getString(R.string.password_error)
                    etNewPassword.requestFocus()
                    return false
                }

                if (confirmNewPassword != newPassword) {
                    etConfirmNewPassword.error = getString(R.string.confirmPass_error)
                    etConfirmNewPassword.requestFocus()
                    return false
                }
                return true
            }
        }
        return false
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {
        binding.apply {
            btnChangePassword.isEnabled =
                etOldPassword.text!!.trim().isNotEmpty() &&
                        etNewPassword.text!!.trim().isNotEmpty() &&
                        etConfirmNewPassword.text!!.trim().isNotEmpty()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}