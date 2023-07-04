package com.graduationproject.robokidsapp.ui.parentsFragments.info


import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.databinding.FragmentKidsReportsBinding
import com.graduationproject.robokidsapp.ui.MainActivity
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class KidsReportsFragment : Fragment() {
    private lateinit var mNavController: NavController
    private var _binding: FragmentKidsReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var childId: String

    private lateinit var listReports: ArrayList<Report>

    private lateinit var listTvDays: ArrayList<TextView>
    private lateinit var listTvShapeDays: ArrayList<TextView>


    private val infoViewModel: InfoViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()

        // close Navigation Drawable
        MainActivity.binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentKidsReportsBinding.inflate(inflater, container, false)

        listTvDays = arrayListOf(
            binding.tvSat,
            binding.tvSun,
            binding.tvMon,
            binding.tvTues,
            binding.tvWed,
            binding.tvThurs,
            binding.tvFri
        )

        listTvShapeDays = arrayListOf(
            binding.shapeSat,
            binding.shapeSun,
            binding.shapeMom,
            binding.shapeTues,
            binding.shapeWed,
            binding.shapeThurs,
            binding.shapeFri
        )


        childId = arguments?.getString("childId")!!
        listReports = ArrayList()




        binding.cardSat.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[0]
            )
            mNavController.navigate(action)
        }
        binding.cardSun.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[1]
            )
            mNavController.navigate(action)
        }
        binding.cardMon.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[2]
            )
            mNavController.navigate(action)
        }
        binding.cardTues.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[3]
            )
            mNavController.navigate(action)
        }
        binding.cardWed.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[4]
            )
            mNavController.navigate(action)
        }
        binding.cardThurs.setOnClickListener {
            val action = KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                listReports[5]
            )
            mNavController.navigate(action)
        }
        binding.cardFri.setOnClickListener {
            val action =
                KidsReportsFragmentDirections.actionKidsReportsFragmentToTimesUseFragment(
                    listReports[6]
                )
            mNavController.navigate(action)
        }


        binding.ivBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(backEntry.destination.id, true)
            }
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get all reports from firebase
        infoViewModel.getReports(childId)
        observerGetReports()
    }


    private fun observerGetReports() {
        infoViewModel.getReports.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarKidsReport.show()
                }
                is Resource.Failure -> {
                    binding.progressBarKidsReport.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarKidsReport.hide()
                    listReports = resource.data

                    updateUi(listReports)
                }
            }
        }
    }


    private fun updateUi(listReports: ArrayList<Report>) {
        val simpleDateFormat = SimpleDateFormat("dd MMMM")
        val lastDate: String = simpleDateFormat.format(listReports[listReports.size - 1].dayDate)
        val firstDate: String = simpleDateFormat.format(listReports[0].dayDate)

        binding.tvLastDate.text = lastDate
        binding.tvTodayDate.text = firstDate


        for (i in 0 until listReports.size) {
            updateDayName(listTvDays[i], listReports[i])
        }

    }


    private fun updateDayName(tvDay: TextView, report: Report) {
        when (report.dayName) {
            "السبت", "Saturday" -> {
                tvDay.text = getString(R.string.sat)
                updateShapeDays(binding.shapeSat, report.totalTime)
            }
            "الأحد", "Sunday" -> {
                tvDay.text = getString(R.string.sun)
                updateShapeDays(binding.shapeSun, report.totalTime)
            }
            "الاثنين", "Monday" -> {
                tvDay.text = getString(R.string.mon)
                updateShapeDays(binding.shapeMom, report.totalTime)
            }
            "الثلاثاء", "Tuesday" -> {
                tvDay.text = getString(R.string.tues)
                updateShapeDays(binding.shapeTues, report.totalTime)
            }
            "الأربعاء", "Wednesday" -> {
                tvDay.text = getString(R.string.wed)
                updateShapeDays(binding.shapeWed, report.totalTime)
            }
            "الخميس", "Thursday" -> {
                tvDay.text = getString(R.string.thurs)
                updateShapeDays(binding.shapeThurs, report.totalTime)
            }
            "الجمعة", "Friday" -> {
                tvDay.text = getString(R.string.fri)
                updateShapeDays(binding.shapeFri, report.totalTime)
            }
        }

    }


    private fun updateShapeDays(tvShape: TextView, totalTime: String) {
        var time = totalTime.toInt()
        if (time == 0)
            time = 1

        if (time >= 50)
            time = 50

        val params = tvShape.layoutParams
        params.height = time*6
        tvShape.layoutParams = params
    }


}