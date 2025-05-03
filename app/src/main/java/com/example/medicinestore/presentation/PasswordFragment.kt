package com.example.medicinestore.presentation

import android.app.AlertDialog
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
import javax.inject.Inject

@AndroidEntryPoint
class PasswordFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil

    @Inject
    lateinit var sharedPref: SharePreferenceUtil
    private lateinit var binding: FragmentPasswordBinding
    private lateinit var viewModel: AdminHomeViewModel
    private var uid = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        val passwordStream = RxTextView.textChanges(binding.emailEt)
            .skipInitialValue()
            .map { password ->
                password.isNotEmpty()
            }
        passwordStream.subscribe {
            if (it) {
                binding.btnCNext.isEnabled = true
                binding.btnCNext.backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
            } else {
                binding.btnCNext.isEnabled = false
                binding.btnCNext.backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.blue_500)
            }
        }
        binding.subtitleTv.visibility = View.GONE
        binding.btnCNext.setOnClickListener {
            updateData(binding.emailEt.text.toString().trim())
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateData(email: String) {
        activityUtil.setFullScreenLoading(true)
        if (email.isNotEmpty()) {
            database = FirebaseDatabase.getInstance().getReference("User")
            val user = HashMap<String, Any>()
            user.put("Password", email)
            database.child(uid).get().addOnSuccessListener {
                val email = it.child("email").value.toString()
                if (email == email) {
                    auth.sendPasswordResetEmail(binding.emailEt.text.toString().trim())
                        .addOnCompleteListener(requireActivity()) {
                            activityUtil.setFullScreenLoading(false)
                            if (it.isSuccessful) {
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Successful")
                                builder.setMessage("Check your email to reset password")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Done") { _, _ ->
                                    activity?.let {
                                        findNavController().navigate(R.id.action_passwordFragment_to_profileFragment)
                                    }
                                }
                                val alartDialog = builder.create()
                                alartDialog.show()
                            }
                        }
                } else {
                    binding.subtitleTv.visibility = View.VISIBLE
                    activityUtil.setFullScreenLoading(false)
                }
            }
        } else {
            binding.subtitleTv.visibility = View.VISIBLE
            activityUtil.setFullScreenLoading(false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
    }
}