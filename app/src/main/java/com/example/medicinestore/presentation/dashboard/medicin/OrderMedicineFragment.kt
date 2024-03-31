package com.example.medicinestore.presentation.dashboard.medicin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentOrderMedicineBinding
import com.example.medicinestore.util.MSActivityUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderMedicineFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var binding: FragmentOrderMedicineBinding
    private lateinit var viewModel: MedicinViewModel
    lateinit var adapter: OrderMedicineAdapter
    lateinit var userArray:ArrayList<Medicine>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_order_medicine, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.orderMedicineRecycle.layoutManager = LinearLayoutManager(activity)
        userArray = arrayListOf()
        adapter = OrderMedicineAdapter(userArray,this.requireContext())
        binding.orderMedicineRecycle.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}