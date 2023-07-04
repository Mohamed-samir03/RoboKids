package com.graduationproject.robokidsapp.ui.parentsFragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentForgotPasswordBinding
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(), TextWatcher {
    private lateinit var mNavController: NavController
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!


    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        binding.etEmailForgotPassword.addTextChangedListener(this@ForgotPasswordFragment)


        binding.ivBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id, true) }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // listen to liveData in viewModel (forgotPassword)
        observer()

        binding.btnSendEmail.setOnClickListener {
            if (inputsValidation()) {
                val email = binding.etEmailForgotPassword.text.toString().trim()

                authViewModel.forgotPassword(email)
            }
        }
    }


    private fun observer() {
        authViewModel.forgotPassword.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarForgetPass.show()
                }
                is Resource.Failure -> {
                    binding.progressBarForgetPass.hide()
                    binding.tvEmialNotFound.show()
                }
                is Resource.Success -> {
                    binding.progressBarForgetPass.hide()

                    val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToEmailSendingSuccessFragment(resource.data)
                    mNavController.navigate(action)

                }
            }
        }
    }


    private fun inputsValidation(): Boolean {
        binding.apply {
            if (btnSendEmail.isEnabled) {
                val email = etEmailForgotPassword.text.toString().trim()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmailForgotPassword.error = getString(R.string.email_error)
                    etEmailForgotPassword.requestFocus()
                    return false //function ديه معناها انه لو الشرط ده اتنفذ .. كده هو هيخرج من ال
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
            tvEmialNotFound.hide()
            btnSendEmail.isEnabled = etEmailForgotPassword.text!!.trim().isNotEmpty()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}