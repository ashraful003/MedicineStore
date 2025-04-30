package com.example.medicinestore.presentation.dashboard.medicin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentExpiterMedicinneDetailsBinding
import com.example.medicinestore.databinding.FragmentMedicineDetailsBinding
import com.example.medicinestore.presentation.MainActivity
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExpiterMedicinneDetailsFragment : Fragment() {
  @Inject
  lateinit var activityUtil: MSActivityUtil
    private lateinit var binding: FragmentExpiterMedicinneDetailsBinding
    private lateinit var viewModel: MedicinViewModel
    lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_expiter_medicinne_details, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        database = Firebase.database.reference
        val mid = requireArguments().getString("id")
        binding.expireMedicineName.text = requireArguments().getString("name")
        binding.companyNameTv.text = requireArguments().getString("company")
        binding.expireDateTv.text = requireArguments().getString("date")
        binding.medicinePriceTv.text = requireArguments().getString("price")
        binding.medicineSelfTv.text = requireArguments().getString("self")
        binding.medicineRowTv.text = requireArguments().getString("row")
        binding.medicineColumnTv.text = requireArguments().getString("column")
        binding.medicineDetailsTv.text = requireArguments().getString("details")
        val pimage = requireArguments().getString("image")
        if (!pimage.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimage)
                .error(R.drawable.ic_human)
                .into(binding.expireMedicineImage)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure!")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { _, _ ->
                database.child("Medicine").child(mid!!).removeValue()
                findNavController().navigate(R.id.action_expiterMedicinneDetailsFragment_to_expireMedicineFragment )
            }
            builder.setNegativeButton("No") { _, _ -> }
            val alartDialog = builder.create()
            alartDialog.show()
        }

        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}