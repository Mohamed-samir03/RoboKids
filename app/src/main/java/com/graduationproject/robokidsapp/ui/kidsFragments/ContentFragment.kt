package com.graduationproject.robokidsapp.ui.kidsFragments

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
import androidx.navigation.fragment.navArgs
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.databinding.FragmentContentBinding
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.ui.parentsFragments.auth.AuthViewModel
import com.graduationproject.robokidsapp.ui.parentsFragments.info.InfoViewModel
import com.graduationproject.robokidsapp.util.Constants.Companion.EDUCATIONAL_FLAG
import com.graduationproject.robokidsapp.util.Constants.Companion.ENTERTAINMENT_FLAG
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private val args by navArgs<ContentFragmentArgs>()

    companion object {
        var currentChild = Child()
        var listOfNotifications = ArrayList<String>()
    }


    private var contentFlag = ""
    private var counterUpdated = 0

    private lateinit var listReports: ArrayList<Report>

    private val infoViewModel: InfoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onResume() {
        super.onResume()
        //Hide status bar
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        arduinoBluetooth.sendMessage("selectContent-") // send message to arduino bluetooth

        // check if parent is register in app or not
        if (authViewModel.currentUser != null) {
            // get all reports from firebase
            infoViewModel.getReports(args.currentChild.id)
            observerGetReports()

            currentChild = args.currentChild
            //get data from Home_Kids
            binding.tvContentChildName.text = currentChild.childName
            val drawable = getChildAvatarFormAssets(currentChild.childAvatar, requireContext())
            binding.ivChild.setImageDrawable(drawable)
        } else {
            binding.tvContentChildName.hide()
            binding.ivChild.hide()
        }
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
        _binding = FragmentContentBinding.inflate(inflater, container, false)

        listReports = ArrayList()

        binding.educationalContent.setOnClickListener {
            val action = ContentFragmentDirections.actionContentFragmentToEducationalContentFragment()
            mNavController.navigate(action)
            contentFlag = EDUCATIONAL_FLAG
            counterUpdated = 0
        }

        binding.entertainmentContent.setOnClickListener {
            val action = ContentFragmentDirections.actionContentFragmentToIntertainmentContentFragment()
            mNavController.navigate(action)
            contentFlag = ENTERTAINMENT_FLAG
            counterUpdated = 0
        }

        binding.back.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id, true) }
        }

        return binding.root
    }

    private fun checkContent(report: Report, isEqual: Boolean) {
        if (contentFlag == EDUCATIONAL_FLAG) {
            listOfNotifications.addAll(report.notifications)
            val reportUpdated = getObjReport(isEqual, report)
            infoViewModel.updateReports(args.currentChild.id, report.id, reportUpdated)
            observeUpdateReport()
        } else if (contentFlag == ENTERTAINMENT_FLAG) {
            listOfNotifications.addAll(report.notifications)
            val reportUpdated = getObjReport(isEqual, report)
            infoViewModel.updateReports(args.currentChild.id, report.id, reportUpdated)
            observeUpdateReport()
        }
        EducationalContentFragment.educationalTime = 0
        EntertainmentContentFragment.entertainmentTime = 0
        listOfNotifications.clear()
    }

    private fun updateReportsForContents() {
        val date = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dayDate = dateFormat.format(date)

        val dayNameFormat = SimpleDateFormat("EEEE")
        val dayName: String = dayNameFormat.format(date)

        for (report in listReports) {
            if (report.dayName == dayName) {
                val dayDateSorted = dateFormat.format(report.dayDate)
                if (dayDate == dayDateSorted) {
                    checkContent(report, true)
                } else {
                    checkContent(report, false)
                }
                break
            }
        }
    }

    private fun getObjReport(isEqual: Boolean, report: Report): Report {
        if (isEqual && contentFlag == EDUCATIONAL_FLAG) {
            return Report(
                id = report.id,
                dayName = report.dayName,
                dayDate = report.dayDate,
                educationalTime = (EducationalContentFragment.educationalTime + report.educationalTime.toInt()).toString(),
                entertainmentTime = report.entertainmentTime,
                totalTime = (EducationalContentFragment.educationalTime + report.totalTime.toInt()).toString(),
                notifications = listOfNotifications
            )
        } else if (!isEqual && contentFlag == EDUCATIONAL_FLAG) {
            return Report(
                id = report.id,
                dayName = report.dayName,
                dayDate = Date(),
                educationalTime = EducationalContentFragment.educationalTime.toString(),
                entertainmentTime = "0",
                totalTime = EducationalContentFragment.educationalTime.toString(),
                notifications = listOfNotifications
            )
        } else if (isEqual && contentFlag == ENTERTAINMENT_FLAG) {
            return Report(
                id = report.id,
                dayName = report.dayName,
                dayDate = report.dayDate,
                educationalTime = report.educationalTime,
                entertainmentTime = (EntertainmentContentFragment.entertainmentTime + report.entertainmentTime.toInt()).toString(),
                totalTime = (report.totalTime.toInt() + EntertainmentContentFragment.entertainmentTime).toString(),
                notifications = listOfNotifications
            )
        } else {
            return Report(
                id = report.id,
                dayName = report.dayName,
                dayDate = Date(),
                educationalTime = "0",
                entertainmentTime = EntertainmentContentFragment.entertainmentTime.toString(),
                totalTime = EntertainmentContentFragment.entertainmentTime.toString(),
                notifications = listOfNotifications
            )
        }
    }


    private fun observeUpdateReport() {
        infoViewModel.updateReports.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    toast(resource.error)
                }
                is Resource.Success -> {
                    //toast(resource.data)
                    counterUpdated = 1
                }
            }
        }
    }

    private fun observerGetReports() {
        infoViewModel.getReports.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    toast(resource.error)
                }
                is Resource.Success -> {
                    listReports = resource.data
                    if (counterUpdated == 0) {
                        updateReportsForContents()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        arduinoBluetooth.sendMessage("welcome-")   // send message to arduino bluetooth
    }

}