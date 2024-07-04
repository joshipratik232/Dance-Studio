package com.example.apptest.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dance.materialdialoglib.ConfirmDialogHelper
import com.example.apptest.R
import com.example.apptest.activities.Utils
import com.example.apptest.databinding.FragmentLoginBinding
import com.example.apptest.viewModel.login.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginBinding.inflate(inflater)
        val loginViewModel: LoginViewModel by activityViewModels()
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        val bottom: BottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottom.visibility = View.GONE

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
                    if (!(binding.emailEdittext.text.isNullOrEmpty() || binding.passwordEdittext.text.isNullOrEmpty()))
                        showErrorMessage()
                    binding.emailEdittext.text!!.clear()
                    binding.passwordEdittext.text!!.clear()
                }
                else -> {
                }
            }
        })
        binding.emailEdittext.addTextChangedListener(afterTextChanged = {
            binding.email.error = null
        })
        binding.passwordEdittext.addTextChangedListener(afterTextChanged = {
            binding.password.error = null
        })
        binding.login.setOnClickListener {
            Utils.hideKeyboardFrom(requireContext(), requireView())
            if (binding.emailEdittext.text.isNullOrEmpty() || binding.passwordEdittext.text.isNullOrEmpty()) {
                if (binding.emailEdittext.text.isNullOrEmpty()) {
                    binding.email.error = "Email cannot be empty"
                }
                if (binding.passwordEdittext.text.isNullOrEmpty()) {
                    binding.password.error = "Password cannot be empty"
                }
            } else {
                loginViewModel.createPost(binding.email.editText?.text.toString(), binding.password.editText?.text.toString())
            }
        }
        binding.forgot.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMoreWebviewFragment("http://www.kicksnflicks.com/WeddingDance/forgotpassword"))
        }
        binding.signup.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMoreWebviewFragment("http://www.kicksnflicks.com/WeddingDance/register"))
        }
        return binding.root
    }

    private fun showErrorMessage() {
//        Snackbar.make(requireActivity().findViewById(R.id.mainConstraint_login), "invalid email or password", Snackbar.LENGTH_SHORT).show()
        val error = ConfirmDialogHelper(requireActivity())
        error.setNegativeClickListener {
            error.dialog!!.dismiss()
        }
        error.setPositiveClickListener {
            error.dialog!!.dismiss()
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMoreWebviewFragment("http://www.kicksnflicks.com/WeddingDance/register"))
        }
        error.create().show()
        error.setMessage("Your Email or Password is failed.")
        error.setPositiveButtonText("Sign up")
        error.setPositiveBackground(R.color.colorCheckOk)
        error.setNegativeButtonText("Retry")

        error.setPositiveButtonTextColor(R.color.primaryTextColor)
        error.setNegativeButtonTextColor(R.color.primaryTextColor)
        error.setMessageTextColor(R.color.primaryTextColor)
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            if (sharedPref.getString("Token", "null").toString() != "null") {
                this@LoginFragment.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                apply()
            }
        }

    }
}