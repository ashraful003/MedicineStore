package com.example.medicinestore.presentation.dashboard.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentEditProfileBinding
import com.example.medicinestore.util.MSActivityUtil
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        val fullNameStream = RxTextView.textChanges(binding.fullNameEt)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        fullNameStream.subscribe {
            binding.fullNameEt.error = if (it) getString(R.string.error_name) else null
        }
        val passwordStream = RxTextView.textChanges(binding.passwordEt)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        val phoneNumberStream = RxTextView.textChanges(binding.phoneNumberEt)
            .skipInitialValue()
            .map { phone ->
                phone.length < 11
            }
        phoneNumberStream.subscribe {
            binding.phoneNumberEt.error = if (it) getString(R.string.error_number) else null
        }

        val dobStream = RxTextView.textChanges(binding.dobEt)
            .skipInitialValue()
            .map { dob ->
                dob.isEmpty()
            }
        dobStream.subscribe {
            binding.dobEt.error = if (it) getString(R.string.error_dob) else null
        }
        binding.dobEt.setOnTouchListener(View.OnTouchListener { e, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val datePickerDialog = DatePickerDialog(requireContext())
                datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.US)
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.dobEt.setText(formattedDate)
                }
                datePickerDialog.show()
                return@OnTouchListener true
            }
            false
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
    }

}