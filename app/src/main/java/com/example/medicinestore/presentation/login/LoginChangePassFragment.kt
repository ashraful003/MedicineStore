package com.example.medicinestore.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentLoginChangePassBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class LoginChangePassFragment : Fragment() {
val actionSignIn = Navigation.createNavigateOnClickListener(R.id.action_loginChangePassFragment_to_loginInputFragment)
    private lateinit var binding:FragmentLoginChangePassBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_change_pass, container, false)
        binding.model = this
        isEnableDoneButton(false)
        val passwordStream = RxTextView.textChanges(binding.newPasswordEt)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        val confirmPasswordStream = Observable.merge(
            RxTextView.textChanges(binding.newPasswordEt)
                .skipInitialValue()
                .map { pasword ->
                  pasword.toString() != binding.newConfirmPasswordEt.text.toString()
                },
            RxTextView.textChanges(binding.newConfirmPasswordEt)
                .skipInitialValue()
                .map { confirmPass ->
                    confirmPass.toString() != binding.newPasswordEt.text.toString()
                }
        )
        val invalidFiledStream = Observable.combineLatest(
            passwordStream,
            confirmPasswordStream
        ){passwordInvalid:Boolean,confirmPassInvalid:Boolean ->
            !passwordInvalid && !confirmPassInvalid
        }
        invalidFiledStream.subscribe {isValid ->
            isEnableDoneButton(isValid)
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }
    private fun isEnableDoneButton(isEnable:Boolean){
        if (isEnable){
            binding.btnDone.isEnabled = true
            binding.btnDone.backgroundTintList = ContextCompat.getColorStateList(requireActivity(),R.color.colorPrimary)
        }
        else{
            binding.btnDone.isEnabled = false
            binding.btnDone.backgroundTintList = ContextCompat.getColorStateList(requireActivity(),R.color.blue_500)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
}