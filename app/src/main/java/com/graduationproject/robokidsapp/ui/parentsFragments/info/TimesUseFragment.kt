package com.graduationproject.robokidsapp.ui.parentsFragments.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.NotificationAdapter
import com.graduationproject.robokidsapp.databinding.FragmentKidsReportsBinding
import com.graduationproject.robokidsapp.databinding.FragmentTimesUseBinding
import java.text.SimpleDateFormat
import java.util.*

class TimesUseFragment : Fragment() {
    private lateinit var mNavController: NavController
    private var _binding: FragmentTimesUseBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<TimesUseFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimesUseBinding.inflate(inflater, container, false)


        val report = args.dayReport

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR

        var dayDate = ""
        if (isLeftToRight) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            dayDate = simpleDateFormat.format(report.dayDate)
        } else {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
            dayDate = simpleDateFormat.format(report.dayDate)
        }

        val adapter = NotificationAdapter(requireContext(), report.notifications)

        binding.apply {
            tvDayDate.text = dayDate
            tvTimeEducational.text = report.educationalTime
            tvTimeEntertainment.text = report.entertainmentTime
            rvNotification.adapter = adapter
            rvNotification.setHasFixedSize(true)
        }


        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}