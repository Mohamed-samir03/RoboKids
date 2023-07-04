package com.graduationproject.robokidsapp.ui.parentsFragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.databinding.FragmentEmailSendingSuccessBinding

class EmailSendingSuccessFragment : Fragment() {
    private lateinit var mNavController: NavController
    private var _binding: FragmentEmailSendingSuccessBinding? = null
    private val binding get() = _binding!!

    private lateinit var sentEmail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSendingSuccessBinding.inflate(inflater, container, false)

        sentEmail = arguments?.getString("sent_email")!!

        binding.emailToSend.text = sentEmail

        binding.btnLoginEmailSent.setOnClickListener {
            val action = EmailSendingSuccessFragmentDirections.actionEmailSendingSuccessFragmentToLoginFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}