package com.example.medicinestore.presentation.dashboard.admin

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import android.Manifest
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentEmployeeDetailsBinding
import com.example.medicinestore.util.MSActivityUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeDetailsFragment : Fragment() {
  @Inject
  lateinit var activityUtil: MSActivityUtil
  private lateinit var binding:FragmentEmployeeDetailsBinding
    private val REQUEST_CALL_PERMISSION = 1001
    private var phoneNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_employee_details, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        phoneNumber = requireArguments().getString("number")
        binding.empNameTv.text = requireArguments().getString("name")
        binding.empEmailTv.text = requireArguments().getString("email")
        binding.empPostTv.text = requireArguments().getString("post")
        binding.empPhoneTv.text = phoneNumber
        binding.empLocationTv.text = requireArguments().getString("location")
        binding.empDobTv.text = requireArguments().getString("dob")
        val pimage = requireArguments().getString("image")
        if (!pimage.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimage)
                .error(R.drawable.ic_human)
                .into(binding.profileIv)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        binding.btnCall.setOnClickListener {
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
}