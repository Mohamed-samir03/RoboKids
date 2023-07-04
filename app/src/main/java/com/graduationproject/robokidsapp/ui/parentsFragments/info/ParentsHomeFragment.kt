package com.graduationproject.robokidsapp.ui.parentsFragments.info

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.ReportsKidsAdapter
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.databinding.FragmentParentsHomeBinding
import com.graduationproject.robokidsapp.ui.MainActivity
import com.graduationproject.robokidsapp.util.Constants.Companion.ADD_KIDS
import com.graduationproject.robokidsapp.util.Constants.Companion.ADD_KIDS_FROM_HP
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.hide
import com.graduationproject.robokidsapp.util.show
import com.graduationproject.robokidsapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentsHomeFragment : Fragment(), ReportsKidsAdapter.OnItemClickListener {
    companion object {
        lateinit var mNavController: NavController
    }

    private var _binding: FragmentParentsHomeBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by viewModels()

    private lateinit var listReports: ArrayList<Report>

    private val adapter by lazy {
        ReportsKidsAdapter(requireContext(), this,
            onDeleteClicked = { position, child ->
                infoViewModel.getReports(child.id)
                observerGetReports()

                infoViewModel.deleteChild(child)
                observerDeleteChild()
            }
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()

        listReports = ArrayList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentParentsHomeBinding.inflate(inflater, container, false)

        binding.addNewKids.setOnClickListener {
            val action = ParentsHomeFragmentDirections.actionParentsHomeFragmentToAddKidsFragment(ADD_KIDS_FROM_HP, null)
            mNavController.navigate(action)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvReportsKids.adapter = adapter
            rvReportsKids.setHasFixedSize(true)
        }

        observerGetChild()
        infoViewModel.getChildren()

    }


    private fun observerGetChild() {
        infoViewModel.getChildren.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarParentHome.show()
                }
                is Resource.Failure -> {
                    binding.progressBarParentHome.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarParentHome.hide()
                    adapter.updateList(resource.data.toMutableList())
                }
            }
        }
    }

    private fun observerDeleteChild() {
        infoViewModel.deleteChild.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarParentHome.show()
                }
                is Resource.Failure -> {
                    binding.progressBarParentHome.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarParentHome.hide()
                    Snackbar.make(binding.root, getString(R.string.child_deleted), Snackbar.LENGTH_LONG).apply {
                        setAction(getString(R.string.undo_snackbar)) {
                            // this return the child that is deleted
                            infoViewModel.insertChild(resource.data, listReports)
                            it.isEnabled = false
                        }
                        show()
                    }
                }
            }
        }
    }


    private fun observerGetReports() {
        infoViewModel.getReports.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarParentHome.show()
                }
                is Resource.Failure -> {
                    binding.progressBarParentHome.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarParentHome.hide()
                    listReports = resource.data
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        MainActivity.binding.customToolbarMainActivity.visibility = View.VISIBLE
        MainActivity.binding.customToolbarMainActivity.title = ""

        //show status bar
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.window!!.statusBarColor = this.resources.getColor(R.color.statusBar_color)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // open navigation drawer
        MainActivity.binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)


        if (infoViewModel.currentParent == null) {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(backEntry.destination.id, true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.binding.customToolbarMainActivity.visibility = View.GONE
        _binding = null
    }


    override fun onItemClick(position: Int) {

    }

}