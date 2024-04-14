package com.example.medicinestore.presentation.login

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentLoginInputBinding
import com.example.medicinestore.presentation.MainActivity
import com.example.medicinestore.util.MSActivityUtil
import com.example.medicinestore.util.SharePreferenceUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import javax.inject.Inject

@AndroidEntryPoint
class LoginInputFragment : Fragment() {
    @Inject
    lateinit var sharedPref: SharePreferenceUtil

    @Inject
    lateinit var activityUtil: MSActivityUtil
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    val actionSignUp =
        Navigation.createNavigateOnClickListener(R.id.action_loginInputFragment_to_loginCreateFragment)
    val actionForgotPassword =
        Navigation.createNavigateOnClickListener(R.id.action_loginInputFragment_to_forgotPasswordFragment)
    private lateinit var binding: FragmentLoginInputBinding
    private lateinit var viewModel: LoginViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        database = Firebase.database.reference
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_input, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        isEnableSignInButton(false)
        val emailStream = RxTextView.textChanges(binding.emailEt)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            binding.emailEt.error = if (it) getString(R.string.error_email) else null
        }

        val passwordStream = RxTextView.textChanges(binding.passwordEt)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }

        val invalidFiledStream = Observable.combineLatest(
            emailStream,
            passwordStream
        ) { emailInvalid: Boolean, passwordInvalid: Boolean ->
            !emailInvalid && !passwordInvalid
        }
        invalidFiledStream.subscribe { isValid ->
            isEnableSignInButton(isValid)
        }
        binding.loginErrorTv.visibility = View.GONE
        binding.btnSignIn.setOnClickListener {
            val email: String = binding.emailEt.text.toString().trim()
            val password: String = binding.passwordEt.text.toString().trim()
            login(email, password)
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login(email: String, password: String) {
        activityUtil.setFullScreenLoading(true)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(OnSuccessListener<AuthResult> {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    database = FirebaseDatabase.getInstance().getReference("User").child(uid)
                    database.get().addOnSuccessListener { data ->
                        Log.d("Tag", "onSuccess" + data.getValue())
                        if (data.exists()) {
                            if (data.hasChild("Admin")||data.hasChild("Employee")) {
                                activityUtil.setFullScreenLoading(false)
                                Handler().postDelayed({
                                    sharedPref.setAuthToken(uid)
                                    activity?.let {
                                        startActivity(MainActivity.getLaunchIntent(it))
                                    }
                                }, 3000)
                            }
                        }
                    }
                })
        }
    }

    private fun isEnableSignInButton(isEnable: Boolean) {
        if (isEnable) {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        } else {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.blue_500)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
}