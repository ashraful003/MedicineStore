package com.example.medicinestore.presentation.dashboard.medicin

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentMedicinListBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MedicinListFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var viewModel:MedicinViewModel
     private lateinit var binding:FragmentMedicinListBinding
     lateinit var  adapter:MedicineListAdapter
     lateinit var userArray : ArrayList<Medicine>
     private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_medicin_list, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
         userArray = arrayListOf()
        adapter = MedicineListAdapter(userArray,this.requireContext())
        binding.medicineListRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.medicineListRecycle.adapter = adapter
        binding.searchMedicine.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    searchList(text)
                }
                return true
            }
        })

        adapter.onItemClick = {
            var bundle = Bundle()
            bundle.putString("id",it.medicineId)
            bundle.putString("name",it.name)
            bundle.putString("company",it.company)
            bundle.putString("price",it.price)
            bundle.putString("date",it.date)
            bundle.putString("self",it.self)
            bundle.putString("row",it.row)
            bundle.putString("column",it.column)
            bundle.putString("details",it.details)
            findNavController().navigate(R.id.action_medicineListFragment_to_medicineDetailsFragment,bundle)
        }
        adapter.onUpdateItemClick = {
            var bundle = Bundle()
                bundle.putString("id",it.medicineId)
                bundle.putString("name",it.name)
                bundle.putString("company",it.company)
                bundle.putString("details",it.details)
                bundle.putString("price",it.price)
                bundle.putString("date",it.date)
                bundle.putString("self",it.self)
                bundle.putString("row",it.row)
                bundle.putString("column",it.column)
            findNavController().navigate(R.id.action_medicineListFragment_to_updateMedicineInfoFragment,bundle)
        }
        return binding.root
    }

    private fun searchList(text: String) {
        val searchList = ArrayList<Medicine>()
        for (medicine in userArray){
            if (medicine.name?.lowercase()?.contains(text.lowercase())==true){
                searchList.add(medicine)
            }
        }
        adapter.searchDataList(searchList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMedicine()
    }
    private  fun loadMedicine(){
        activityUtil.setFullScreenLoading(true)
        database = FirebaseDatabase.getInstance().getReference("Medicine")
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityUtil.setFullScreenLoading(false)
                if (snapshot.exists()){
                    for (snap in snapshot.children){
                        val medicine = snap.getValue(Medicine::class.java)
                        medicine?.medicineId = snap.key
                        if (!userArray.contains(medicine) && isExpiredDate(medicine?.date)){
                            userArray.add(medicine!!)
                        }
                    }
                    adapter.searchDataList(userArray)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(),error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isExpiredDate(dateString: String?): Boolean {
        if (dateString.isNullOrEmpty()) {
            return false
        }

        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date: Date = try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return Date().before(date)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}