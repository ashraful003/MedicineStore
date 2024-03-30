package com.example.medicinestore.presentation.dashboard.medicin

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentAddMedicinBinding
import com.example.medicinestore.util.MSActivityUtil
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddMedicinFragment : Fragment() {
    @Inject
    lateinit var activityUtil : MSActivityUtil
    private lateinit var binding:FragmentAddMedicinBinding
    private lateinit var viewModel:MedicinViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_medicin, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        isEnableSaveButton(false)
        val nameStream = RxTextView.textChanges(binding.nameEt)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            binding.nameEt.error = if (it) getString(R.string.error_name) else null
        }

        val companyStream = RxTextView.textChanges(binding.companyEt)
            .skipInitialValue()
            .map { company ->
                company.isEmpty()
            }
        companyStream.subscribe {
            binding.companyEt.error = if (it) getString(R.string.error_company_name) else null
        }

        val detailsStream = RxTextView.textChanges(binding.detailsEt)
            .skipInitialValue()
            .map { details ->
                details.isEmpty()
            }
        detailsStream.subscribe {
            binding.detailsEt.error = if (it) getString(R.string.error_details) else null
        }

        val priceStream = RxTextView.textChanges(binding.priceEt)
            .skipInitialValue()
            .map { price ->
                price.isEmpty()
            }
        priceStream.subscribe {
            binding.priceEt.error = if (it) getString(R.string.error_price) else null
        }

        val expireDateStream = RxTextView.textChanges(binding.expireDateEt)
            .skipInitialValue()
            .map { expire ->
                expire.isEmpty()
            }
        expireDateStream.subscribe {
            binding.expireDateEt.error = if (it) getString(R.string.error_expire_date) else null
        }
        binding.expireDateEt.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val datePickerDialog = DatePickerDialog(requireContext())
                datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("DD/MM/YYYY", Locale.US)
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.expireDateEt.setText(formattedDate)
                }
                datePickerDialog.show()
                return@OnTouchListener true
            }
            false
        })
        val selfStream = RxTextView.textChanges(binding.selfEt)
            .skipInitialValue()
            .map { self ->
                self.isEmpty()
            }
        selfStream.subscribe {
            binding.selfEt.error = if (it) getString(R.string.error_self) else null
        }

        val rowStream = RxTextView.textChanges(binding.rowEt)
            .skipInitialValue()
            .map { row ->
                row.isEmpty()
            }
        rowStream.subscribe {
            binding.rowEt.error = if (it) getString(R.string.error_row) else null
        }

        val columnStream = RxTextView.textChanges(binding.columnEt)
            .skipInitialValue()
            .map { column ->
                column.isEmpty()
            }
        columnStream.subscribe {
            binding.columnEt.error = if (it) getString(R.string.error_column) else null
        }

        val invalidFiledStream = Observable.combineLatest(
            nameStream,
            companyStream,
            detailsStream,
            priceStream,
            expireDateStream,
            selfStream,
            rowStream,
            columnStream
        ){nameInvalid:Boolean, companyInvalid:Boolean,detailsInvalid:Boolean,priceInvalid:Boolean, expiredateInvalid:Boolean, selfInvalid:Boolean,rowInvalid:Boolean,columnInvalid:Boolean ->
            !nameInvalid && !columnInvalid && !detailsInvalid && !priceInvalid && !expiredateInvalid && !selfInvalid && !rowInvalid && !columnInvalid
        }
        invalidFiledStream.subscribe { isValid ->
            isEnableSaveButton(isValid)
        }

        return binding.root
    }
    private fun isEnableSaveButton(isEnable:Boolean){
        if (isEnable){
            binding.btnSave.isEnabled = true
            binding.btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.colorPrimary)
        }
        else{
            binding.btnSave.isEnabled = false
            binding.btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.blue_500)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}