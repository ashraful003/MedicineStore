package com.example.medicinestore.presentation.dashboard.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentAddEmployeeBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddEmployeeFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var viewModel:AdminHomeViewModel
    private lateinit var binding:FragmentAddEmployeeBinding
    lateinit var auth: FirebaseAuth
    lateinit var database:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_employee, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        isEnableContinueBTN(false)
        auth = Firebase.auth
        database = Firebase.database.reference
        val fullNameStream = RxTextView.textChanges(binding.eFullNameEt)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        fullNameStream.subscribe {
            binding.eFullNameEt.error = if (it) getString(R.string.error_name) else null
        }

        val emailStream = RxTextView.textChanges(binding.eEmailEt)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            binding.eEmailEt.error = if (it) getString(R.string.error_email) else null
        }

        val postStream = RxTextView.textChanges(binding.ePostEt)
            .skipInitialValue()
            .map { post ->
                post.isEmpty()
            }
        postStream.subscribe {
            binding.ePostEt.error = if (it) getString(R.string.error_post) else null
        }

        val phoneStream = RxTextView.textChanges(binding.ePhoneNumberEt)
            .skipInitialValue()
            .map { phone ->
                phone.length < 11
            }
        phoneStream.subscribe {
            binding.ePhoneNumberEt.error = if (it) getString(R.string.error_number) else null
        }

        val locationStream = RxTextView.textChanges(binding.eLocationEt)
            .skipInitialValue()
            .map { location ->
                location.isEmpty()
            }
        locationStream.subscribe {
            binding.eLocationEt.error = if (it) getString(R.string.error_address) else null
        }

        val dobStream = RxTextView.textChanges(binding.eDobEt)
            .skipInitialValue()
            .map { dob ->
                dob.isEmpty()
            }
        dobStream.subscribe {
            binding.eDobEt.error = if (it) getString(R.string.error_dob) else null
        }
        binding.eDobEt.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action==MotionEvent.ACTION_UP){
                val datePicker = DatePickerDialog(requireContext())
                datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year,month,dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.eDobEt.setText(formattedDate)
                }
                datePicker.show()
                return@OnTouchListener true
            }
            false
        })

        val passwordStream = RxTextView.textChanges(binding.ePasswordEt)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        val confirmPasswordStream = io.reactivex.Observable.merge(
            RxTextView.textChanges(binding.ePasswordEt)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.eConfirmPassEt.text.toString()
                },
            RxTextView.textChanges(binding.eConfirmPassEt)
                .skipInitialValue()
                .map { passConfirm ->
                    passConfirm.toString() != binding.ePasswordEt.text.toString()
                })
        val invalidFieldsStream = Observable.combineLatest(
            fullNameStream,
            emailStream,
            postStream,
            phoneStream,
            locationStream,
            dobStream,
            passwordStream,
            confirmPasswordStream
        ){nameInvalid:Boolean, emailInvalid:Boolean, postInvalid:Boolean, phoneInvalid:Boolean, locationInvalid:Boolean,dobInvalid:Boolean,passwordInvalid:Boolean, confirmPassInvalid:Boolean ->
            !nameInvalid && !emailInvalid && !postInvalid && !phoneInvalid && !locationInvalid && !dobInvalid && !passwordInvalid && !confirmPassInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            isEnableContinueBTN(isValid)
        }

        binding.eContinueButton.setOnClickListener {
            val email = binding.eEmailEt.text.toString().trim()
            val password = binding.ePasswordEt.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()){
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(requireActivity()){
                        if (it.isSuccessful){
                            Toast.makeText(activity,getString(R.string.auth_massage),Toast.LENGTH_SHORT).show()
                            saveData()
                            findNavController().navigate(R.id.action_addEmployeeFragment_to_employeeFragment)
                        }
                        else{
                            Toast.makeText(activity, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

        return binding.root
    }

    private fun saveData() {
        activityUtil.setFullScreenLoading(true)
        database = FirebaseDatabase.getInstance().getReference("User")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val employee = HashMap<String, String>()
        employee.put("uid", userId)
        employee.put("image","")
        employee.put("name", binding.eFullNameEt.text.toString().trim())
        employee.put("email", binding.eEmailEt.text.toString().trim())
        employee.put("post", binding.ePostEt.text.toString().trim())
        employee.put("number", binding.ePhoneNumberEt.text.toString().trim())
        employee.put("location", binding.eLocationEt.text.toString().trim())
        employee.put("dob", binding.eDobEt.text.toString().trim())
        employee.put("password", binding.ePasswordEt.text.toString().trim())
        employee.put("Employee", "1")
        database.child(userId).setValue(employee)
        activityUtil.setFullScreenLoading(false)
        binding.eFullNameEt.text?.clear()
        binding.eEmailEt.text?.clear()
        binding.ePostEt.text?.clear()
        binding.ePhoneNumberEt.text?.clear()
        binding.eLocationEt.text?.clear()
        binding.eDobEt.text?.clear()
        binding.ePasswordEt.text?.clear()
        binding.eConfirmPassEt.text?.clear()
    }

    private fun isEnableContinueBTN(isEnable:Boolean){
        if (isEnable){
            binding.eContinueButton.isEnabled = true
            binding.eContinueButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.colorPrimary)
        }
        else{
            binding.eContinueButton.isEnabled = false
            binding.eContinueButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.blue_300)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
    }
}