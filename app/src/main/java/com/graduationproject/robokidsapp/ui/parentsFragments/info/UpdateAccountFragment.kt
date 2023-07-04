package com.graduationproject.robokidsapp.ui.parentsFragments.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.databinding.FragmentUpdateAccountBinding
import com.graduationproject.robokidsapp.util.*
import com.hbb20.countrypicker.models.CPCountry
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UpdateAccountFragment : Fragment() {
    private lateinit var mNavController: NavController
    private var _binding: FragmentUpdateAccountBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by viewModels()


    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0
    private var countryCode: String = ""
    private var gender = "father"
    private var flagBirth = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateAccountBinding.inflate(inflater, container, false)

        binding.selectYearBirth.setOnClickListener { showDialogCalender(false) }


        binding.ivFather.setOnClickListener {
            selectFather()
        }

        binding.ivMother.setOnClickListener {
            selectMother()
        }


        binding.countryPicker.cpViewHelper.cpViewConfig.viewTextGenerator =
            { cpCountry: CPCountry ->
                countryCode = cpCountry.alpha2

                cpCountry.name
            }



        binding.ivLeftBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(backEntry.destination.id, true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this listen to live data in viewModel (getParent)
        observerGetParent()
        infoViewModel.getParentData()

        // this listen to live data in viewModel (updateParent)
        observerUpdateParent()

        binding.btnSaveParentData.setOnClickListener {
            if (inputsValidation()) {
                val parent = getParentObj()
                infoViewModel.updateParentInfo(parent)
            }
        }
    }


    private fun observerGetParent() {
        infoViewModel.getParent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarUpdateAccount.show()
                }
                is Resource.Failure -> {
                    binding.progressBarUpdateAccount.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarUpdateAccount.hide()
                    setParentData(resource.data)
                }
            }
        }
    }

    private fun observerUpdateParent() {
        infoViewModel.updateParent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarUpdateAccount.show()
                }
                is Resource.Failure -> {
                    binding.progressBarUpdateAccount.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarUpdateAccount.hide()

                    val action = UpdateAccountFragmentDirections.actionUpdateAccountFragmentToSettingFragment()
                    mNavController.navigate(action)
                }
            }
        }
    }


    fun getParentObj(): Parent {
        return Parent(
            name = binding.etParentsName.text.toString(),
            gender = gender,
            countryCode = countryCode,
            birth_date = binding.tvBirth.text.toString()
        )
    }


    private fun setParentData(parent: Parent) {
        if (parent.gender == "father") {
            selectFather()
        } else {
            selectMother()
        }

        binding.etParentsName.setText(parent.name)
        binding.countryPicker.cpViewHelper.setCountryForAlphaCode(parent.countryCode)

        val listBirthDate = parent.birth_date.split("/")
        mDay = listBirthDate[0].toInt()
        mMonth = listBirthDate[1].toInt()
        mYear = listBirthDate[2].toInt()
        binding.tvBirth.text = parent.birth_date
        countryCode = parent.countryCode


        showDialogCalender(true)

    }


    fun showDialogCalender(findData: Boolean) {
        val customView = LayoutInflater.from(activity)
            .inflate(R.layout.custom_layout_select_year_birth, null, false)
        val calenderBirth = customView.findViewById<CalendarView>(R.id.calenderBirth!!)

        if (findData) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, mDay)
            calendar.set(Calendar.YEAR, mYear)
            calendar.set(Calendar.MONTH, mMonth - 1)
            calenderBirth.setDate(calendar.timeInMillis, true, true)
            flagBirth = true
        } else {
            val dialog = AlertDialog.Builder(requireContext())
            calenderBirth.setOnDateChangeListener { p0, year, month, dayOfMonth ->
                mDay = dayOfMonth
                mMonth = month + 1
                mYear = year
                flagBirth = true
                val birth = "$mDay/$mMonth/$mYear"
                binding.tvBirth.text = birth
            }


            if (flagBirth) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, mDay)
                calendar.set(Calendar.YEAR, mYear)
                calendar.set(Calendar.MONTH, mMonth - 1)
                calenderBirth.setDate(calendar.timeInMillis, true, true)
                flagBirth = false
            }


            dialog.setView(customView)
            val alert = dialog.create()
            alert.show()

            val close = customView.findViewById<ImageView>(R.id.iv_close)

            close.setOnClickListener { alert.dismiss() } // this to close dialog
        }
    }


    private fun selectFather() {
        gender = "father"
        binding.apply {
            ivFather.setBackgroundResource(R.drawable.bg_select_gender)
            ivMother.setBackgroundResource(R.drawable.bg_select_gender_default)
            tvNameFather.setTextColor(resources.getColor(R.color.nameParent_color))
            tvNameMother.setTextColor(resources.getColor(R.color.black))
        }
    }

    private fun selectMother() {
        gender = "mother"
        binding.apply {
            ivMother.setBackgroundResource(R.drawable.bg_select_gender)
            ivFather.setBackgroundResource(R.drawable.bg_select_gender_default)
            tvNameMother.setTextColor(resources.getColor(R.color.nameParent_color))
            tvNameFather.setTextColor(resources.getColor(R.color.black))
        }
    }


    private fun inputsValidation(): Boolean {
        binding.apply {
            val name = etParentsName.text.toString().trim()
            if (name.isEmpty()) {
                etParentsName.error = getString(R.string.select_name)
                etParentsName.requestFocus()
                return false
            }

            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}