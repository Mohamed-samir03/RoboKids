package com.graduationproject.robokidsapp.ui.parentsFragments.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.databinding.FragmentParentsDataBinding
import com.graduationproject.robokidsapp.ui.parentsFragments.auth.AuthViewModel
import com.graduationproject.robokidsapp.ui.parentsFragments.auth.RegisterFragmentDirections
import com.graduationproject.robokidsapp.util.Constants.Companion.ADD_KIDS
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ParentsDataFragment : Fragment() {
    private var _binding: FragmentParentsDataBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by viewModels()

    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0
    private var gender = "father"
    private var flagBirth = false

    private lateinit var mNavController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentParentsDataBinding.inflate(inflater, container, false)

        binding.ivFather.setOnClickListener {
            gender = "father"
            binding.apply {
                ivFather.setBackgroundResource(R.drawable.bg_select_gender)
                ivMother.setBackgroundResource(R.drawable.bg_select_gender_default)
                tvNameFather.setTextColor(resources.getColor(R.color.nameParent_color))
                tvNameMother.setTextColor(resources.getColor(R.color.black))
            }
        }

        binding.ivMother.setOnClickListener {
            gender = "mother"
            binding.apply {
                ivFather.setBackgroundResource(R.drawable.bg_select_gender_default)
                ivMother.setBackgroundResource(R.drawable.bg_select_gender)
                tvNameMother.setTextColor(resources.getColor(R.color.nameParent_color))
                tvNameFather.setTextColor(resources.getColor(R.color.black))
            }
        }

        binding.selectYearBirth.setOnClickListener {
            showDialogCountries()
        }


        binding.tvSkip.setOnClickListener {
            val action = ParentsDataFragmentDirections.actionParentsDataFragmentToAddKidsFragment(ADD_KIDS, null)
            mNavController.navigate(action)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this listen to live data in viewModel (parentData)
        observer()


        binding.btnParentData.setOnClickListener {
            if (inputsValidation()) {
                val parent = getParentObj()

                infoViewModel.updateParentInfo(parent)
            }
        }

    }


    private fun observer() {
        infoViewModel.updateParent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarParentData.show()
                }
                is Resource.Failure -> {
                    binding.progressBarParentData.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarParentData.hide()
                    toast(resource.data)

                    val action = ParentsDataFragmentDirections.actionParentsDataFragmentToAddKidsFragment(ADD_KIDS, null)
                    mNavController.navigate(action)
                }
            }
        }
    }


    fun getParentObj(): Parent {
        return Parent(
            name = binding.etParentsName.text.toString(),
            gender = gender,
            countryCode = arguments?.getString("countryCode")!!,
            birth_date = binding.tvBirth.text.toString()
        )
    }


    private fun inputsValidation(): Boolean {
        binding.apply {
            val name = etParentsName.text.toString().trim()
            if (name.isEmpty()) {
                etParentsName.error = getString(R.string.select_name)
                etParentsName.requestFocus()
                return false
            }

            if (mYear == 0) {
                toast(getString(R.string.select_birthDate))
                return false
            }
            return true
        }
    }


    fun showDialogCountries() {
        val customView = LayoutInflater.from(activity)
            .inflate(R.layout.custom_layout_select_year_birth, null, false)

        val dialog = AlertDialog.Builder(requireContext())


        val calenderBirth = customView.findViewById<CalendarView>(R.id.calenderBirth)

        calenderBirth.setOnDateChangeListener { p0, year, month, dayOfMonth ->
            mDay = dayOfMonth
            mMonth = month + 1
            mYear = year
            flagBirth = true
            val birth = "$mDay/$mMonth/$mYear"
            binding.tvBirth.text = birth
        }


        if (flagBirth) {
            var calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, mDay)
            calendar.set(Calendar.YEAR, mYear)
            calendar.set(Calendar.MONTH, mMonth - 1)
            calenderBirth.setDate(calendar.timeInMillis, true, true)
        }


        dialog.setView(customView)

        val alert = dialog.create()
        alert.show()

        val close = customView.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener { alert.dismiss() } // this to close dialog

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}