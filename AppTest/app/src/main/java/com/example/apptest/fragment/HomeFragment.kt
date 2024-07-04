package com.example.apptest.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.R
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.exit
import com.example.apptest.activities.Utils.Companion.token
import com.example.apptest.adapter.CategoryNameAdapter
import com.example.apptest.databinding.FragmentCategoryNameBinding
import com.example.apptest.databinding.FragmentHomeBinding
import com.example.apptest.databinding.OfflineBinding
import com.example.apptest.viewModel.category.CtyViewModel
import com.example.apptest.viewModel.category.Status
import com.example.apptest.viewModel.login.LoginViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber


class HomeFragment : Fragment() {
    private lateinit var ctyviewModel: CtyViewModel
    private lateinit var mRecycle: RecyclerView
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var offline: OfflineBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater)
        ctyviewModel = ViewModelProvider(this).get(CtyViewModel::class.java)
        requireActivity().findViewById<AppBarLayout>(R.id.appbar).visibility = VISIBLE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .visibility = VISIBLE
        requireActivity().findViewById<View>(R.id.border).visibility = VISIBLE
        binding.lifecycleOwner = this
        mRecycle = binding.mainView
        offline = binding.userOffline
        val catNameBinding = FragmentCategoryNameBinding.inflate(inflater)
        catNameBinding.ctyviewmodel = ctyviewModel
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString("Token", "null").toString()
        authorization["Authorization"] = token
        binding.ctyViewmodel = ctyviewModel
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        ctyviewModel.isOffline.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.userOffline.root.visibility = VISIBLE
                binding.homeFragment.visibility = GONE
            } else {
                binding.userOffline.root.visibility = GONE
                binding.homeFragment.visibility = VISIBLE
            }
        })

        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                ctyviewModel.isOffline()
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                ctyviewModel.isOnline()
            }
        })
        ctyviewModel.status.observe(viewLifecycleOwner, Observer {
            when (it!!) {
                Status.LOADING -> {
                    binding.status.visibility = VISIBLE
                    binding.status.setImageResource(R.drawable.loading_main_animation)
                }
                Status.DONE -> {
                    binding.status.visibility = GONE
                }
                Status.ERROR -> {
                    binding.status.visibility = GONE
                    binding.userOffline.root.visibility = VISIBLE
                }
            }
        })
        Timber.plant(Timber.DebugTree())

        binding.mainView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.mainView.adapter = CategoryNameAdapter(CategoryNameAdapter.OnclickListener {
            ctyviewModel.seeAllVideo(it)
        })
        loginViewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            val shared = activity?.getPreferences(Context.MODE_PRIVATE)
                    ?: return@Observer
            with(shared.edit()) {
                for (data in userData) {
                    putString("EmailId", data.EmailId)
                    putString("Token", data.Token)
                    putString("Phone", data.Phone)
                    putString("UserName", data.UserName)
                    putString("ResponseMessage", data.ResponseMessage)
                }
                apply()
            }
        })
        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    val shared = activity?.getPreferences(Context.MODE_PRIVATE)
                            ?: return@Observer
                    token = shared.getString("Token", "null").toString()
                    Timber.d("sharedPref=====> $token")
                    authorization["Authorization"] = token

                }
                else -> {
                }
            }
        })
        ctyviewModel.navigateTo.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.requireActivity().findNavController(this.id).navigate(HomeFragmentDirections.actionHomeFragmentToSeeAllFragment(it))
                ctyviewModel.navComp()
            }
        })
        ctyviewModel.navigateToSelected.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.requireActivity().findNavController(this.id).navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment(it, null, null))
                ctyviewModel.navigateToComplete()
            }
        })

        /* viewModel.navigateToSelected.observe(viewLifecycleOwner, Observer {
             if (null != it) {
                 try {
                     this.requireActivity().findNavController(this.id).navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment(it))
                     viewModel.navigateToComplete()

                 } catch (e: IllegalArgumentException) {
                     Timber.d("can't navigate")
                 }
             }
         })*/
        return binding.root
    }

    override fun onStop() {
        exit = false
        super.onStop()
    }

    override fun onStart() {
        exit = true
        super.onStart()
    }
}
