package com.example.medicinestore.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentLoginLandingBinding

class LoginLandingFragment : Fragment() {
    val actionSignIn = Navigation.createNavigateOnClickListener(R.id.action_loginLandingFragment_to_loginInputFragment)
    val actionSignUp = Navigation.createNavigateOnClickListener(R.id.action_loginLandingFragment_to_loginCreateFragment)
    private lateinit var binding:FragmentLoginLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_landing, container, false)
        binding.model = this

        return binding.root
    }
}