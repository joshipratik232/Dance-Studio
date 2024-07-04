package com.example.apptest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptest.adapter.VideoAdapter
import com.example.apptest.databinding.FragmentHomeBinding
import com.example.apptest.viewModel.image.ImageViewModel
import timber.log.Timber


class HomeFragment : Fragment() {
    private val viewModel: ImageViewModel by lazy {
        ViewModelProvider(this).get(ImageViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        Timber.plant(Timber.DebugTree())
        binding.gridVdo.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.gridVdo.adapter = VideoAdapter(VideoAdapter.OnclickListener {
            viewModel.openVideo(it)
        })
        viewModel.navigateToSelected.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                try {
                    this.requireActivity().findNavController(this.id).navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment(it))
                    viewModel.navigateToComplete()

                } catch (e: IllegalArgumentException) {
                    Timber.d("can't navigate")
                }
            }
        })
        return binding.root
    }
}
