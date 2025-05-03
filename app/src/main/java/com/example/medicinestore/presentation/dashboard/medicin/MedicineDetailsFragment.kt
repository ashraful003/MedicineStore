package com.example.medicinestore.presentation.dashboard.medicin

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
import com.example.medicinestore.databinding.FragmentMedicineDetailsBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MedicineDetailsFragment : Fragment() {
  @Inject
  lateinit var activityUtil: MSActivityUtil
  private lateinit var binding: FragmentMedicineDetailsBinding
  private lateinit var viewModel: MedicinViewModel
    lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_medicine_details, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        val id = requireArguments().getString("id")
        binding.medicineName.text = requireArguments().getString("name")
        binding.companyNameTv.text = requireArguments().getString("company")
        binding.medicineDateTv.text = requireArguments().getString("date")
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
                .into(binding.medicineListImage)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        activityUtil.setFullScreenLoading(true)
        binding.btnEdite.visibility = View.GONE
        database = Firebase.database.reference
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("User").child(uid)
        database.get().addOnSuccessListener {
            if (it.hasChild("Admin") || it.hasChild("Employee")) {
                binding.btnEdite.visibility = View.VISIBLE
                activityUtil.setFullScreenLoading(false)
                binding.btnEdite.setOnClickListener {
                    var bundle = Bundle()
                    bundle.putString("id",id)
                    bundle.putString("name", binding.medicineName.text.toString())
                    bundle.putString("company",binding.companyNameTv.text.toString())
                    bundle.putString("price",binding.medicinePriceTv.text.toString())
                    bundle.putString("date",binding.medicineDateTv.text.toString())
                    bundle.putString("self",binding.medicineSelfTv.text.toString())
                    bundle.putString("row",binding.medicineRowTv.text.toString())
                    bundle.putString("column",binding.medicineColumnTv.text.toString())
                    bundle.putString("details",binding.medicineDetailsTv.text.toString())
                    bundle.putString("image",pimage)
                    findNavController().navigate(R.id.action_medicineDetailsFragment_to_updateMedicineInfoFragment,bundle)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}