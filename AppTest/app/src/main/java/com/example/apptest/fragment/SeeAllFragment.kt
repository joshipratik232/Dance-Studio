package com.example.apptest.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.apptest.R
import com.example.apptest.adapter.CatWiseSeeAllAdapter
import com.example.apptest.databinding.FragmentSeeAllBinding
import com.example.apptest.viewModel.seeAll.SeeAllViewModel
import com.example.apptest.viewModel.seeAll.SeeAllViewModelFactory
import com.example.apptest.viewModel.seeAll.Status
import com.google.android.material.appbar.AppBarLayout

class SeeAllFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentSeeAllBinding.inflate(inflater)
        binding.lifecycleOwner = this
        requireActivity().findViewById<AppBarLayout>(R.id.appbar).visibility = View.VISIBLE
        val application = requireNotNull(activity).application
        val id = SeeAllFragmentArgs.fromBundle(requireArguments()).id
        val seeAllViewModelFactory = SeeAllViewModelFactory(id, application)
        val seeAllViewModel = ViewModelProvider(this, seeAllViewModelFactory).get(SeeAllViewModel::class.java)
        binding.viewmodel = seeAllViewModel
        binding.gridVdo.adapter = CatWiseSeeAllAdapter(CatWiseSeeAllAdapter.OnclickListener {
            seeAllViewModel.openSeeVideo(it)
        })
        seeAllViewModel.navToSelected.observe(viewLifecycleOwner, {
            if (it != null) {
                this.requireActivity().findNavController(this.id).navigate(SeeAllFragmentDirections.actionSeeAllFragmentToVideoFragment(null, null, it))
                seeAllViewModel.navComplete()
            }
        })

        seeAllViewModel.status.observe(viewLifecycleOwner, {
            when (it!!) {
                Status.LOADING -> {
                    binding.status.visibility = View.VISIBLE
                    binding.status.setImageResource(R.drawable.loading_see_all_animation)
                }
                Status.DONE -> {
                    binding.status.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.status.visibility = View.GONE
                    binding.offline.root.visibility = View.VISIBLE
                }
            }
        })

        seeAllViewModel.isOffline.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.offline.root.visibility = View.VISIBLE
                binding.videoFragment.visibility = View.GONE
            } else {
                binding.offline.root.visibility = View.GONE
                binding.videoFragment.visibility = View.VISIBLE
            }
        })

        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                seeAllViewModel.isOffline()
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                seeAllViewModel.isOnline()
            }
        })
        return binding.root
    }
}
