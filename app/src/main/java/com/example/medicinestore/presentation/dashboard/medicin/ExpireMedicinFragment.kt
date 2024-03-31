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
import com.example.medicinestore.databinding.FragmentExpireMedicinBinding
import com.example.medicinestore.util.MSActivityUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExpireMedicinFragment : Fragment() {
    @Inject
    lateinit var activityUtil:MSActivityUtil
    private lateinit var binding:FragmentExpireMedicinBinding
    private lateinit var viewModel: MedicinViewModel
    lateinit var adapter: ExpireMedicineAdapter
    lateinit var userArray:ArrayList<Medicine>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_expire_medicin, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.expireMedicineRecycle.layoutManager = LinearLayoutManager(activity)
        userArray = arrayListOf()
        adapter = ExpireMedicineAdapter(userArray,this.requireContext())
        binding.expireMedicineRecycle.adapter = adapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}