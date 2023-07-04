package com.graduationproject.robokidsapp.ui.kidsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.adapters.VideoAdapter
import com.graduationproject.robokidsapp.databinding.FragmentEntertainmentSectionBinding
import com.graduationproject.robokidsapp.data.model.Videos
import com.graduationproject.robokidsapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EntertainmentSectionFragment : Fragment() , VideoAdapter.OnItemClickListener {
    private var _binding: FragmentEntertainmentSectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private lateinit var videoList:ArrayList<Videos>
    private lateinit var sectionName:String
    private lateinit var startDate: Date
    private lateinit var adapter:VideoAdapter

    private val contentViewModel: ContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        startDate = Date()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEntertainmentSectionBinding.inflate(inflater, container, false)

        if(ContentFragment.currentChild.childName==""){ // is not register
            binding.ivChild.hide()
        }else{
            val imageDrawable =
                getChildAvatarFormAssets(ContentFragment.currentChild.childAvatar, requireContext())
            binding.ivChild.setImageDrawable(imageDrawable)
        }

        sectionName = arguments?.getString("section_name")!!
        videoList = ArrayList()

        if(sectionName == "Films"){
            contentViewModel.getFilmsContent()
            observerGetFilms()
            binding.tvSectionName.text = getString(R.string.Films)
        }else {
            contentViewModel.getMusicContent()
            observerGetMusic()
            binding.tvSectionName.text = getString(R.string.Music)
        }

        adapter = VideoAdapter(requireContext(),contentViewModel.storageInstance,this)
        binding.rvEntertainmentSection.adapter = adapter
        binding.rvEntertainmentSection.setHasFixedSize(true)


        binding.rvEntertainmentSection.apply {
            set3DItem(true)
            setAlpha(true)
            setInfinite(true)
        }

        binding.entertainmentSectionBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val endTime = simpleDateFormat.format(Date())
        val startTime = simpleDateFormat.format(startDate)
        if(sectionName == "Films"){
            ContentFragment.listOfNotifications.add(getString(R.string.secStories)+ " $startTime ${getString(R.string.out)} $endTime")
        }else {
            ContentFragment.listOfNotifications.add(getString(R.string.secSongs)+ " $startTime ${getString(R.string.out)} $endTime")
        }

    }

    private fun observerGetFilms() {
        contentViewModel.filmsContent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEntertainmentSection.show()
                }
                is Resource.Failure -> {
                    binding.progressBarEntertainmentSection.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarEntertainmentSection.hide()
                    adapter.updateList(resource.data)
                }
            }
        }
    }
    private fun observerGetMusic() {
        contentViewModel.musicContent.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEntertainmentSection.show()
                }
                is Resource.Failure -> {
                    binding.progressBarEntertainmentSection.hide()
                    toast(resource.error)
                }
                is Resource.Success -> {
                    binding.progressBarEntertainmentSection.hide()
                    adapter.updateList(resource.data)
                }
            }
        }
    }

    override fun onItemClick(videos: Videos) {
        val action = EntertainmentSectionFragmentDirections.actionIntertainmentSectionFragmentToIntertainmentVidoFramFragment(videos)
        mNavController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}