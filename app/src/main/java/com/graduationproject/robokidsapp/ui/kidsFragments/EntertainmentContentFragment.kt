package com.graduationproject.robokidsapp.ui.kidsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.ContentAdapter
import com.graduationproject.robokidsapp.databinding.FragmentEntertainmentContentBinding
import com.graduationproject.robokidsapp.data.model.Content
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets
import com.graduationproject.robokidsapp.util.hide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EntertainmentContentFragment : Fragment(), ContentAdapter.OnItemClickListener {
    private var _binding: FragmentEntertainmentContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private lateinit var listContent: ArrayList<Content>

    lateinit var startDate: Date
    lateinit var endDate: Date

    companion object {
        var entertainmentTime = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()

        startDate = Date()
        endDate = Date()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("enterCont-") // send result to arduino bluetooth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEntertainmentContentBinding.inflate(inflater, container, false)

        if (ContentFragment.currentChild.childName == "") { // is not register
            binding.entertainmentContentImgChild.hide()
        } else {
            val imageDrawable =
                getChildAvatarFormAssets(ContentFragment.currentChild.childAvatar, requireContext())
            binding.entertainmentContentImgChild.setImageDrawable(imageDrawable)
        }

        listContent = ArrayList()
        listContent.add(Content("Films", R.drawable.stories))
        listContent.add(Content("Music", R.drawable.music))
        listContent.add(Content("Gaming", R.drawable.games_icon))

        val adapter = ContentAdapter(requireContext(), listContent, this)
        binding.rvEntertainmentContent.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEntertainmentContent.adapter = adapter
        binding.rvEntertainmentContent.setHasFixedSize(true)


        binding.entertainmentContentBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }


        return binding.root
    }


    // this when click on any card in recyclerView
    override fun onItemClick(position: Int) {
        val sectionName = listContent[position].contentName

        arduinoBluetooth.sendMessage("enter$sectionName-") // send result to arduino bluetooth

        if (sectionName == "Gaming") {
            val action = EntertainmentContentFragmentDirections.actionIntertainmentContentFragmentToGamingSectionFragment()
            mNavController.navigate(action)
        } else {
            val action = EntertainmentContentFragmentDirections.actionIntertainmentContentFragmentToIntertainmentSectionFragment(listContent[position].contentName)
            mNavController.navigate(action)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        endDate = Date()

        val hourFormat = SimpleDateFormat("HH")
        val munitFormat = SimpleDateFormat("mm")
        val secondFormat = SimpleDateFormat("ss")

        val startHour = hourFormat.format(startDate).toInt()
        val startMunit = munitFormat.format(startDate).toInt()
        val startSecond = secondFormat.format(startDate).toInt()

        val endHour = hourFormat.format(endDate).toInt()
        val endMunit = munitFormat.format(endDate).toInt()
        val endSecond = secondFormat.format(endDate).toInt()

        val totalHour = endHour - startHour
        val totalMunit = endMunit - startMunit
        val totalSecond = endSecond - startSecond

        entertainmentTime = (totalHour * 60) + totalMunit + (totalSecond / 60)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}