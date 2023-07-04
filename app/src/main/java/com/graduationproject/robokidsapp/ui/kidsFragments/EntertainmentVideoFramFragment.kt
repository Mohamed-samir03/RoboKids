package com.graduationproject.robokidsapp.ui.kidsFragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.robokidsapp.databinding.FragmentEntertainmentVideoFramBinding


class EntertainmentVideoFramFragment : Fragment() {

    private var _binding: FragmentEntertainmentVideoFramBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private val args by navArgs<EntertainmentVideoFramFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEntertainmentVideoFramBinding.inflate(inflater, container, false)

        val videoUrl = args.video.videoUrl

        // Uri object to refer the
        // resource from the videoUrl
        val uri = Uri.parse(videoUrl)
        // sets the resource from the
        // videoUrl to the videoView
        binding.vvVideoContent.setVideoURI(uri)
        //binding.vvVideoContent.setVideoPath("android.resource://"+activity?.packageName+"/"+R.raw.v1)
        // creating object of
        // media controller class
        val mediaController = MediaController(requireContext())
        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(binding.vvVideoContent)
        // sets the media player to the videoView
        mediaController.setMediaPlayer(binding.vvVideoContent)
        // sets the media controller to the videoView
        binding.vvVideoContent.setMediaController(mediaController)
        // starts the video
        binding.vvVideoContent.start()


        binding.entertainmentVideoBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}