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
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.ChildsAdapter
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.databinding.FragmentHomeKidsBinding
import com.graduationproject.robokidsapp.ui.MainActivity
import com.graduationproject.robokidsapp.ui.parentsFragments.info.InfoViewModel
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeKidsFragment : Fragment(),ChildsAdapter.OnItemClickListener {

    private var _binding: FragmentHomeKidsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    val adapter by lazy { ChildsAdapter(requireContext(), this) }

    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
//        MainActivity.connectBluetooth.bluetooth_connect_device()
    }


    override fun onResume() {
        super.onResume()
        //Hide status bar
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeKidsBinding.inflate(inflater, container, false)

        binding.imgParent.setOnClickListener {
            goToParentHome()
        }
        binding.tvParentName.setOnClickListener {
            goToParentHome()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvKids.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvKids.adapter = adapter
            rvKids.setHasFixedSize(true)
        }

        // this listen to live data in viewModel (getParent)
        observerGetParent()
        infoViewModel.getParentData()

        observerGetChild()
        infoViewModel.getChildren()

    }

    private fun observerGetParent() {
        infoViewModel.getParent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarHomeKids.show()
                }
                is Resource.Failure -> {
                    binding.progressBarHomeKids.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarHomeKids.hide()

                    if(resource.data.gender == "father"){
                        binding.imgParent.setImageResource(R.drawable.father)
                    }else{
                        binding.imgParent.setImageResource(R.drawable.mother)
                    }
                }
            }
        }
    }

    private fun observerGetChild() {
        infoViewModel.getChildren.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarHomeKids.show()
                }
                is Resource.Failure -> {
                    binding.progressBarHomeKids.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarHomeKids.hide()

                    adapter.updateList(resource.data.toMutableList())
                }
            }
        }
    }

    fun goToParentHome(){
        val action = HomeKidsFragmentDirections.actionHomeKidsFragmentToParentsHomeFragment()
        mNavController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(child: Child) {
        val action = HomeKidsFragmentDirections.actionHomeKidsFragmentToContentEnterSplashFragment(child)
        mNavController.navigate(action)
    }

}

