package com.example.medicinestore.presentation.dashboard.medicin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentMedicineDetailsBinding
import com.example.medicinestore.util.MSActivityUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MedicineDetailsFragment : Fragment() {
  @Inject
  lateinit var activityUtil: MSActivityUtil
  private lateinit var binding: FragmentMedicineDetailsBinding
  private lateinit var viewModel: MedicinViewModel
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
        binding.nameTv.text = requireArguments().getString("name")
        binding.companyTv.text = requireArguments().getString("company")
        binding.priceTv.text = requireArguments().getString("price")
        binding.dateTv.text = requireArguments().getString("date")
        binding.selfTv.text = requireArguments().getString("self")
        binding.rowTv.text = requireArguments().getString("row")
        binding.columnTv.text = requireArguments().getString("column")
        binding.detailsTv.text = requireArguments().getString("details")
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}