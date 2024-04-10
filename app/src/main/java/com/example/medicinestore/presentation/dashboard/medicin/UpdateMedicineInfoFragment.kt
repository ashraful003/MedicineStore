package com.example.medicinestore.presentation.dashboard.medicin

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentUpdateMedicineInfoBinding
import com.example.medicinestore.util.MSActivityUtil
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
class UpdateMedicineInfoFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var viewModel: MedicinViewModel
private lateinit var binding:FragmentUpdateMedicineInfoBinding
private lateinit var database: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_medicine_info, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        database = Firebase.database.reference
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        isEnableSaveButton(false)
        val nameStream = RxTextView.textChanges(binding.nameEt)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }

        val companyStream = RxTextView.textChanges(binding.companyEt)
            .skipInitialValue()
            .map { company ->
                company.isEmpty()
            }

        val detailsStream = RxTextView.textChanges(binding.detailsEt)
            .skipInitialValue()
            .map { details ->
                details.isEmpty()
            }

        val priceStream = RxTextView.textChanges(binding.priceEt)
            .skipInitialValue()
            .map { price ->
                price.isEmpty()
            }

        val expireDateStream = RxTextView.textChanges(binding.expireDateEt)
            .skipInitialValue()
            .map { expire ->
                expire.isEmpty()
            }

        binding.expireDateEt.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val datePickerDialog = DatePickerDialog(requireContext())
                datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
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

        val rowStream = RxTextView.textChanges(binding.rowEt)
            .skipInitialValue()
            .map { row ->
                row.isEmpty()
            }

        val columnStream = RxTextView.textChanges(binding.columnEt)
            .skipInitialValue()
            .map { column ->
                column.isEmpty()
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
         showMedicine()
         binding.btnSave.setOnClickListener {
            updateMedicineInfo(requireArguments().getString("id"))
         }
        return binding.root
    }

    private fun updateMedicineInfo(id: String?) {
        var name = binding.nameEt.text.toString().trim()
        var company = binding.companyEt.text.toString().trim()
        var details = binding.detailsEt.text.toString().trim()
        var price = binding.priceEt.text.toString().trim()
        var date = binding.expireDateEt.text.toString().trim()
        var self = binding.selfEt.text.toString().trim()
        var row = binding.rowEt.text.toString().trim()
        var column = binding.columnEt.text.toString().trim()
        if (name.isNotEmpty() && company.isNotEmpty() && details.isNotEmpty() && price.isNotEmpty() && date.isNotEmpty() && self.isNotEmpty() && row.isNotEmpty() && column.isNotEmpty()){
            var medicineinfo = HashMap<String,Any>()
            medicineinfo.put("name",name)
            medicineinfo.put("company",company)
            medicineinfo.put("details",details)
            medicineinfo.put("price",price)
            medicineinfo.put("date",date)
            medicineinfo.put("self",self)
            medicineinfo.put("row",row)
            medicineinfo.put("column",column)
            id.let {medicineId ->
                activityUtil.setFullScreenLoading(true)
             database.child("Medicine").child(medicineId!!).ref.updateChildren(medicineinfo)
                 .addOnSuccessListener {
                  activityUtil.setFullScreenLoading(false)
                     findNavController().navigate(R.id.action_updateMedicineInfoFragment_to_medicineListFragment)
                  Toast.makeText(requireContext(), getString(R.string.update_massage), Toast.LENGTH_SHORT).show()
                 }.addOnFailureListener {
                     Toast.makeText(requireContext(), getString(R.string.update_fail_massage),Toast.LENGTH_SHORT).show()
                 }

            }
        }
    }

    private fun showMedicine(){
        binding.nameEt.setText(requireArguments().getString("name"))
        binding.companyEt.setText(requireArguments().getString("company"))
        binding.detailsEt.setText(requireArguments().getString("details"))
        binding.priceEt.setText(requireArguments().getString("price"))
        binding.expireDateEt.setText(requireArguments().getString("date"))
        binding.selfEt.setText(requireArguments().getString("self"))
        binding.rowEt.setText(requireArguments().getString("row"))
        binding.columnEt.setText(requireArguments().getString("column"))
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