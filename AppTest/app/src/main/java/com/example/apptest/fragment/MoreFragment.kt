package com.example.apptest.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dance.materialdialoglib.ConfirmDialogHelper
import com.example.apptest.R
import com.example.apptest.databinding.FragmentMoreBinding
import com.example.apptest.viewModel.login.LoginViewModel

class MoreFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMoreBinding.inflate(inflater)
        val loginViewModel: LoginViewModel by activityViewModels()

        binding.aboutusMain.setOnClickListener {
            navigate("http://www.kicksnflicks.com/WeddingDance/aboutus.aspx")
        }
        binding.subscriptionMain.setOnClickListener {
            navigate("http://www.kicksnflicks.com/WeddingDance/subscription.aspx")
        }
        binding.contactusMain.setOnClickListener {
            navigate("http://www.kicksnflicks.com/WeddingDance/contactus.aspx")
        }
        binding.helpMain.setOnClickListener {

        }
        binding.logoutMain.setOnClickListener {
            val logoutAlertDialog = ConfirmDialogHelper(requireActivity())
            logoutAlertDialog.setNegativeClickListener {
                logoutAlertDialog.dialog!!.dismiss()
            }
            logoutAlertDialog.setPositiveClickListener {
                logoutAlertDialog.dialog!!.dismiss()
                clearData()
                loginViewModel.refuseAuthentication()
                findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToLoginFragment())
            }
            logoutAlertDialog.create().show()
            logoutAlertDialog.setMessage("Are you sure you want to logout?")
            logoutAlertDialog.setPositiveButtonText("Yes")
            logoutAlertDialog.setPositiveButtonTextColor(R.color.primaryLightColor)
            logoutAlertDialog.setNegativeButtonTextColor(R.color.primaryTextColor)
            logoutAlertDialog.setMessageTextColor(R.color.primaryTextColor)
        }
        binding.myFavouriteMain.setOnClickListener {

        }
        binding.myListMain.setOnClickListener {
            navigate("http://www.kicksnflicks.com/WeddingDance/listofdance.aspx")
        }
        binding.profileMain.setOnClickListener {
            navigate("http://www.kicksnflicks.com/WeddingDance/userprofile.aspx")
        }
        return binding.root
    }

    private fun clearData() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString("EmailId", null)
            putString("Token", null)
            putString("Phone", null)
            putString("UserName", null)
            putString("ResponseMessage", null)
            apply()
        }
    }

    fun navigate(url: String) {
        this.requireActivity().findNavController(this.id).navigate(MoreFragmentDirections.actionMoreFragmentToMoreWebviewFragment(url))
    }
}
