package com.example.medicinestore.presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentPasswordBinding
import com.example.medicinestore.presentation.dashboard.admin.AdminHomeViewModel
import com.example.medicinestore.util.MSActivityUtil
import com.example.medicinestore.util.SharePreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.annotation.ElementType
import javax.inject.Inject

@AndroidEntryPoint
class PasswordFragment : Fragment() {
    val actionChangePass = Navigation.createNavigateOnClickListener(R.id.action_passwordFragment_to_loginChangePassFragment2)
    @Inject
    lateinit var activityUtil: MSActivityUtil
    @Inject
    lateinit var sharedPref: SharePreferenceUtil
    private lateinit var binding:FragmentPasswordBinding
    private lateinit var viewModel: AdminHomeViewModel
    private var uid = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_password, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)
        database = Firebase.database.reference
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        val passwordStream = RxTextView.textChanges(binding.passwordEt)
            .skipInitialValue()
            .map { password ->
                password.isNotEmpty()
            }
        passwordStream.subscribe {
            if (it){
                binding.btnCNext.isEnabled = true
                binding.btnCNext.backgroundTintList = ContextCompat.getColorStateList(requireActivity(),R.color.colorPrimary)
            }
            else{
                binding.btnCNext.isEnabled = false
                binding.btnCNext.backgroundTintList = ContextCompat.getColorStateList(requireActivity(),R.color.blue_500)
            }
        }
        binding.subtitleTv.visibility = View.GONE
        binding.btnCNext.setOnClickListener {
          updateData(binding.passwordEt.text.toString().trim())
        }
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateData(password:String){
        activityUtil.setFullScreenLoading(true)
        if (password.isNotEmpty()){
            database = FirebaseDatabase.getInstance().getReference("User")
            val user = HashMap<String,Any>()
            user.put("Password",password)
            database.child(uid).get().addOnSuccessListener {
                val passwords = it.child("Password").value.toString()
                if (passwords==password){
                    database.child(uid).updateChildren(user).addOnSuccessListener {
                        Handler().postDelayed({
                            sharedPref.setAuthToken(uid)
                            activity?.let {
                                findNavController().navigate(R.id.action_passwordFragment_to_loginChangePassFragment2)
                                binding.passwordEt.text!!.clear()
                                activityUtil.setFullScreenLoading(false)
                            }
                        }, 3000)
                    }
                } else{
                    binding.subtitleTv.visibility = View.VISIBLE
                    activityUtil.setFullScreenLoading(false)
                }
            }
        }else{
            binding.subtitleTv.visibility = View.VISIBLE
            activityUtil.setFullScreenLoading(false)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
    }
}