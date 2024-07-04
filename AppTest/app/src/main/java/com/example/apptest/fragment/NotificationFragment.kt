package com.example.apptest.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptest.R
import com.example.apptest.adapter.NotifyAdapter
import com.example.apptest.databinding.FragmentNotificationBinding
import com.example.apptest.viewModel.notify.NotifyViewModel

class NotificationFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentNotificationBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val notifyViewModel = ViewModelProvider(this).get(NotifyViewModel::class.java)
        binding.notify = notifyViewModel
        binding.notifyList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.notifyList.adapter = NotifyAdapter()
        notifyViewModel.status.observe(viewLifecycleOwner, Observer {
            when (it!!) {
                com.example.apptest.viewModel.notify.Status.LOADING -> {
                    binding.status.visibility = View.VISIBLE
                    binding.status.setImageResource(R.drawable.loading_main_animation)
                }
                com.example.apptest.viewModel.notify.Status.DONE -> {
                    binding.status.visibility = View.GONE
                }
                com.example.apptest.viewModel.notify.Status.ERROR -> {
                    binding.status.visibility = View.GONE
                    binding.offline.root.visibility = View.VISIBLE
                }
            }
        })
        notifyViewModel.isOffline.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.offline.root.visibility = View.VISIBLE
                binding.notificationFragment.visibility = View.GONE
            }
        })
        notifyViewModel.isOnline.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.offline.root.visibility = View.GONE
                binding.notificationFragment.visibility = View.VISIBLE
            }
        })
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                notifyViewModel.isOffline()
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                notifyViewModel.isOnline()
            }
        })
        return binding.root
    }
}
