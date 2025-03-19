package com.example.medicinestore.presentation.dashboard.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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

        binding.empNameTv.text = requireArguments().getString("name")
        binding.empEmailTv.text = requireArguments().getString("email")
        binding.empPostTv.text = requireArguments().getString("post")
        binding.empPhoneTv.text = requireArguments().getString("number")
        binding.empLocationTv.text = requireArguments().getString("location")
        binding.empDobTv.text = requireArguments().getString("dob")
        val pimage = requireArguments().getString("image")
        if (!pimage.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimage)
                .error(R.drawable.human) // Error image if URL is broken
                .into(binding.profileIv)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        return binding.root
    }
}