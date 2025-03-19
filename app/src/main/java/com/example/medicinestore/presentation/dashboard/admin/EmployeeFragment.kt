package com.example.medicinestore.presentation.dashboard.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentEmployeeBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeFragment : Fragment() {
    val actionAddEmployee = Navigation.createNavigateOnClickListener(R.id.action_employeeFragment_to_addEmployeeFragment)
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var viewModel: AdminHomeViewModel
    private lateinit var binding: FragmentEmployeeBinding
    lateinit var employeeArray: ArrayList<EmployeeModel>
    lateinit var adapter: EmployeeAdapter
    lateinit var database:DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employee, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        loadEmployee()
        binding.searchMedicine.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text!=null){
                    searchList(text)
                }
                return true
            }

        })
        binding.employeeListRecycle.layoutManager = GridLayoutManager(activity,2)
        employeeArray = arrayListOf()
        adapter = EmployeeAdapter(employeeArray, requireContext())
        binding.employeeListRecycle.adapter = adapter

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("id",it.id)
            bundle.putString("name",it.name)
            bundle.putString("email",it.email)
            bundle.putString("post",it.post)
            bundle.putString("number",it.number)
            bundle.putString("location",it.location)
            bundle.putString("dob",it.dob)
            bundle.putString("image",it.image)
            findNavController().navigate(R.id.action_employeeFragment_to_employeeDetailsFragment,bundle)
        }

        return binding.root
    }

    fun loadEmployee(){
        activityUtil.setFullScreenLoading(true)
        database = FirebaseDatabase.getInstance().getReference("User")
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
             activityUtil.setFullScreenLoading(false)
                if (snapshot.exists()){
                    for (snap in snapshot.children){
                      if (snap.hasChild("Employee")){
                          val employee = snap.getValue(EmployeeModel::class.java)
                          if (!employeeArray.contains(employee)){
                              employeeArray.add(employee!!)
                          }
                      }
                    }

                    adapter.searchDataList(employeeArray)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(),error.toString(),Toast.LENGTH_SHORT).show()
            }

        })
    }
    fun searchList(text:String){
        val search = ArrayList<EmployeeModel>()
        for (employee in employeeArray){
            if (employee.name?.lowercase()?.contains(text.lowercase())==true){
                search.add(employee)
            }
        }
        adapter.searchDataList(search)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
    }
}