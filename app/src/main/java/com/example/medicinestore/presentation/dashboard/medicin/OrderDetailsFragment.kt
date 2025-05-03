package com.example.medicinestore.presentation.dashboard.medicin

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentOrderDetailsBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
@Inject
lateinit var activityUtil: MSActivityUtil
private lateinit var binding:FragmentOrderDetailsBinding
private lateinit var viewModel: MedicinViewModel
lateinit var database: DatabaseReference
    private val REQUEST_CALL_PERMISSION = 1001
    private var phoneNumber: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_order_details,container,false)
        binding.model = this

        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        database = Firebase.database.reference
        val mid = requireArguments().getString("id")
        phoneNumber = requireArguments().getString("number")
        binding.orderMedicineName.text = requireArguments().getString("name")
        binding.orderMedicineCompanyTv.text = requireArguments().getString("company")
        binding.orderMedicineDateTv.text = requireArguments().getString("date")
        binding.orderMedicinePriceTv.text = requireArguments().getString("price")
        binding.orderMedicineDetailsTv.text = requireArguments().getString("details")
        binding.orderMedicineAmountTv.text = requireArguments().getString("twoNumbers")
        binding.shippingAddressTv.text = requireArguments().getString("address")

        val pimage = requireArguments().getString("image")
        if (!pimage.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimage)
                .error(R.drawable.ic_human)
                .into(binding.expireMedicineImage)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        activityUtil.setFullScreenLoading(true)
        buttonActive(mid)
        binding.btnCallUser.setOnClickListener {
            makePhoneCall()
        }
        return binding.root
    }

    private fun makePhoneCall() {
        if (phoneNumber.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Phone number is not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PERMISSION
            )
        } else {
            // Permission already granted
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buttonActive(mid: String?) {
        binding.btnComplete.visibility = View.GONE
        binding.btnCallUser.visibility = View.GONE
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("User").child(uid)
        database.get().addOnSuccessListener {
            if (it.hasChild("Admin") || it.hasChild("Employee")) {
                binding.btnComplete.visibility = View.VISIBLE
                binding.btnCallUser.visibility = View.VISIBLE
                activityUtil.setFullScreenLoading(false)
                orderSubmit(mid)
            }
            else if (it.hasChild("User")){
                activityUtil.setFullScreenLoading(false)
            }
        }
    }

    private fun orderSubmit(mid: String?) {
        binding.btnComplete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Complete")
            builder.setMessage("Are you sure!")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { _, _ ->
                activity?.let {
                    database = FirebaseDatabase.getInstance().getReference("order")
                    val medicine = HashMap<String, Any>()
                    medicine.put("complete","Done")
                    database.child(mid!!).updateChildren(medicine).addOnSuccessListener {
                        findNavController().navigate(R.id.action_orderDetailsFragment_to_orderMedicineFragment)
                        Toast.makeText(activity, getText(R.string.update_massage), Toast.LENGTH_SHORT).show()
                        activityUtil.setFullScreenLoading(false)
                    }
                }
            }
            builder.setNegativeButton("No") { _, _ -> }
            val alartDialog = builder.create()
            alartDialog.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}