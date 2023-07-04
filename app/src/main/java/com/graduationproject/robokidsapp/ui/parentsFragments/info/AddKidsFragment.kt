package com.graduationproject.robokidsapp.ui.parentsFragments.info

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.AdapterChildAvatar
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.databinding.FragmentAddKidsBinding
import com.graduationproject.robokidsapp.util.*
import com.graduationproject.robokidsapp.util.Constants.Companion.ADD_KIDS
import com.graduationproject.robokidsapp.util.Constants.Companion.ADD_KIDS_FROM_HP
import com.graduationproject.robokidsapp.util.Constants.Companion.EDIT_KIDS
import dagger.hilt.android.AndroidEntryPoint
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddKidsFragment : Fragment(), AdapterChildAvatar.OnAvatarClickListener {
    private lateinit var mNavController: NavController
    private var _binding: FragmentAddKidsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterChildAvatar: AdapterChildAvatar
    private lateinit var listAvatar: ArrayList<Drawable>

    private var avatarNumber = -1
    private var gender = "boy"
    private var isFourDigits = false
    private var isCheckedSwitch = false
    private var isClickedAdvance = false

    private val args by navArgs<AddKidsFragmentArgs>()

    private val infoViewModel: InfoViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddKidsBinding.inflate(inflater, container, false)

        if (args.operation == ADD_KIDS || args.operation == ADD_KIDS_FROM_HP) {
            setAvatarsInRecyclerView(-1)
        }


        // this check operation that is editKids or not
        checkOperation()


        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            gender = if (R.id.radioBoy == checkedId) "boy" else "girl"
        }

        binding.ivSubKidsAge.setOnClickListener {
            var age: Int = binding.tvAgeKids.text.toString().toInt()
            if (age > 1) {
                age--
            }
            binding.tvAgeKids.text = age.toString()
        }

        binding.ivPlusKidsAge.setOnClickListener {
            var age: Int = binding.tvAgeKids.text.toString().toInt()
            if (age in 1..6) {
                age++
            }
            binding.tvAgeKids.text = age.toString()
        }

        binding.advanceOptionsTitle.setOnClickListener {
            if (isClickedAdvance) {
                binding.ivArrow.setImageResource(R.drawable.arrow_down)
                binding.constraintLayout.transitionToStart()
                isClickedAdvance = false
            } else {
                binding.ivArrow.setImageResource(R.drawable.arrow_up)
                binding.constraintLayout.transitionToEnd()
                isClickedAdvance = true
            }
        }

        binding.etChildPassword.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 4) {
                binding.childPassword.boxStrokeColor = resources.getColor(R.color.dark_gray)
                binding.childPassword.helperText = resources.getString(R.string.four_digits)
                binding.childPassword.isCounterEnabled = true
                isFourDigits = false
            } else {
                binding.childPassword.boxStrokeColor = resources.getColor(R.color.main_color)
                binding.childPassword.helperText = null
                binding.childPassword.isCounterEnabled = false
                isFourDigits = true
            }
        }

        binding.swAddChildPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            isCheckedSwitch = isChecked
            binding.etChildPassword.isEnabled = isChecked
        }

        binding.tvSkip.setOnClickListener {
            val action = AddKidsFragmentDirections.actionAddKidsFragmentToParentsHomeFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this listen to live data in viewModel (insertChild)
        observerInsertChild()

        // this listen to live data in viewModel (updateChild)
        observerUpdateChild()

        binding.btnAddKids.setOnClickListener {
            if (inputsValidation()) {
                if (args.operation == ADD_KIDS || args.operation == ADD_KIDS_FROM_HP) {
                    val child = getChildObj()
                    val listReports = getListReports()
                    if (isCheckedSwitch) {
                        child.childPassword = binding.etChildPassword.text.toString()
                    }

                    infoViewModel.insertChild(child, listReports)
                } else {
                    val childUpdated = updateChildObj()
                    infoViewModel.updateChild(childUpdated)
                }
            }
        }
    }


    private fun setAvatarsInRecyclerView(selectedAvatar: Int) {
        adapterChildAvatar = AdapterChildAvatar(this, selectedAvatar)
        listAvatar = ArrayList()
        for (i in 1..14) {
            val drawable = getChildAvatarFormAssets(i, requireContext())
            listAvatar.add(drawable!!)
        }

        adapterChildAvatar.updateList(listAvatar)
        binding.rvChildAvatar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterChildAvatar
            setHasFixedSize(true)
        }
    }

    private fun getListReports(): ArrayList<Report> {
        val list: ArrayList<Report> = ArrayList()
        var report: Report
        for (i in 0..6) {
            val myDate = Date(System.currentTimeMillis() - (i * 1000 * 60 * 60 * 24))
            val f: Format = SimpleDateFormat("EEEE")
            val dayName: String = f.format(myDate)

            report = Report(dayDate = myDate, dayName = dayName)
            list.add(report)
        }
        return list
    }


    private fun observerInsertChild() {
        infoViewModel.insertChild.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarAddKids.show()
                }
                is Resource.Failure -> {
                    binding.progressBarAddKids.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarAddKids.hide()
                    toast(resource.data)

                    val action =
                        AddKidsFragmentDirections.actionAddKidsFragmentToParentsHomeFragment()
                    mNavController.navigate(action)
                }
            }
        }
    }

    private fun observerUpdateChild() {
        infoViewModel.updateChild.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarAddKids.show()
                }
                is Resource.Failure -> {
                    binding.progressBarAddKids.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarAddKids.hide()
                    toast(resource.data)

                    val action =
                        AddKidsFragmentDirections.actionAddKidsFragmentToParentsHomeFragment()
                    mNavController.navigate(action)
                }
            }
        }
    }


    fun getChildObj(): Child {
        return Child(
            childName = binding.etChildName.text.toString(),
            childAvatar = avatarNumber,
            gender = gender,
            age = binding.tvAgeKids.text.toString().toInt()
        )
    }

    fun updateChildObj(): Child {
        return Child(
            id = args.currentChild?.id!!,
            childName = binding.etChildName.text.toString(),
            childAvatar = avatarNumber,
            gender = gender,
            age = binding.tvAgeKids.text.toString().toInt(),
            childPassword = binding.etChildPassword.text.toString(),
            createDate = args.currentChild?.createDate!!
        )
    }

    private fun inputsValidation(): Boolean {
        binding.apply {
            val name = etChildName.text.toString().trim()
            if (avatarNumber == -1) {
                toast(getString(R.string.choose_avatar))
                return false
            } else if (name.isEmpty()) {
                etChildName.error = getString(R.string.select_name)
                etChildName.requestFocus()
                return false
            } else if (isCheckedSwitch) {
                if (!isFourDigits) {
                    etChildPassword.error = getString(R.string.enter_four_digits)
                    etChildPassword.requestFocus()
                    return false
                }
            }

            return true
        }
    }

    private fun checkOperation() {
        if (args.operation == EDIT_KIDS) {
            val child = args.currentChild

            binding.apply {
                tvSkip.hide()
                tvAddKidsTitle.text = getString(R.string.edit_kids)

                avatarNumber = child!!.childAvatar
                setAvatarsInRecyclerView(child.childAvatar - 1)

                if (child.gender == "boy") {
                    radioBoy.isChecked = true
                    gender = "boy"
                } else {
                    radioGirl.isChecked = true
                    gender = "girl"
                }

                etChildName.setText(child.childName)
                tvAgeKids.text = child.age.toString()

                if (child.childPassword != "") {
                    binding.swAddChildPassword.isChecked = true
                    binding.etChildPassword.setText(child.childPassword)
                }

                btnAddKids.text = getString(R.string.update_kids)
            }
        }

        if (args.operation == ADD_KIDS_FROM_HP) {
            binding.tvSkip.hide()
        }
    }

    // when click on child avatar
    override fun onItemClick(avatarNum: Int) {
        avatarNumber = avatarNum + 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}