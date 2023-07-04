package com.graduationproject.robokidsapp.ui.parentsFragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.databinding.FragmentRegisterBinding
import com.graduationproject.robokidsapp.util.*
import com.hbb20.countrypicker.models.CPCountry
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment(), TextWatcher {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var countryCode = ""

    private lateinit var mNavController: NavController

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            etEmailRegister.addTextChangedListener(this@RegisterFragment)
            etPasswordRegister.addTextChangedListener(this@RegisterFragment)
            etConfirmPasswordRegister.addTextChangedListener(this@RegisterFragment)
        }


        binding.tvBackToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            mNavController.navigate(action)
        }


        binding.countryPicker.cpViewHelper.cpViewConfig.viewTextGenerator =
            { cpCountry: CPCountry ->
                countryCode = cpCountry.alpha2

                cpCountry.name
            }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this listen to live data in viewModel (register)
        observer()

        binding.btnRegister.setOnClickListener {
            if (inputsValidation()) {
                val email = binding.etEmailRegister.text.toString()
                val password = binding.etPasswordRegister.text.toString()
                val parent = getParentObj()

                // create parent on firebase
                authViewModel.registerParent(email, password, parent)
            }
        }
    }


    fun getParentObj(): Parent {
        return Parent(
            id = "",
            email = binding.etEmailRegister.text.toString(),
            name = "",
            gender = "",
            countryCode = countryCode,
            birth_date = ""
        )
    }


    private fun observer() {
        authViewModel.register.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarRegister.show()
                }
                is Resource.Failure -> {
                    binding.progressBarRegister.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarRegister.hide()
                    toast(resource.data)

                    val action = RegisterFragmentDirections.actionRegisterFragmentToParentsDataFragment(countryCode)
                    mNavController.navigate(action)
                }
            }
        }
    }


    private fun inputsValidation(): Boolean {
        binding.apply {
            if (btnRegister.isEnabled) {
                val email = etEmailRegister.text.toString().trim()
                val password = etPasswordRegister.text.toString().trim()
                val confirmPassword = etConfirmPasswordRegister.text.toString().trim()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmailRegister.error = getString(R.string.email_error)
                    etEmailRegister.requestFocus()
                    return false //function ديه معناها انه لو الشرط ده اتنفذ .. كده هو هيخرج من ال
                }

                if (password.length < 6) {
                    etPasswordRegister.error = getString(R.string.password_error)
                    etPasswordRegister.requestFocus()
                    return false
                }

                if (confirmPassword != password) {
                    etConfirmPasswordRegister.error = getString(R.string.confirmPass_error)
                    etConfirmPasswordRegister.requestFocus()
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
            btnRegister.isEnabled =
                etEmailRegister.text!!.trim().isNotEmpty() &&
                        etPasswordRegister.text!!.trim().isNotEmpty() &&
                        etConfirmPasswordRegister.text!!.trim().isNotEmpty()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}