package com.example.apptest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dailymotion.android.player.sdk.PlayerWebView
import com.example.apptest.databinding.FragmentVideoBinding
import com.example.apptest.viewModel.video.VideoViewModel
import com.example.apptest.viewModel.video.VideoViewModelFactory
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : Fragment() {
    private lateinit var playerWebView: PlayerWebView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentVideoBinding.inflate(inflater)
        playerWebView = binding.videoPlayer
        binding.lifecycleOwner = this
        val application = requireNotNull(activity).application
        val data = VideoFragmentArgs.fromBundle(requireArguments()).data
        val videoViewModelFactory = VideoViewModelFactory(data,application)
        val viewModel = ViewModelProvider(this, videoViewModelFactory).get(VideoViewModel::class.java)
        binding.video = viewModel
        viewModel.videoDetail.observe(viewLifecycleOwner, Observer {
            likeCount.text = it.toString()
        })
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        playerWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerWebView.onResume()
    }
}
