package com.example.medicinestore.presentation.dashboard.admin

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentAdminHomeBinding
import com.example.medicinestore.presentation.MainActivity
import com.example.medicinestore.presentation.dashboard.medicin.Medicine
import com.example.medicinestore.util.MSActivityUtil
import com.example.medicinestore.util.SharePreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject
@AndroidEntryPoint
class AdminHomeFragment : Fragment() {
    var actionAddMedicin = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_addMedicinFragment)
    val actionMedicinList = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_medicinListFragment)
    val actionExpireMedicin = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_expireMedicinFragment)
    val actionEmployee = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_employeeFragment)

    @Inject
    lateinit var sharedPrefs: SharePreferenceUtil
    @Inject
    lateinit var activityUtil : MSActivityUtil
    private lateinit var viewModel: AdminHomeViewModel
    private lateinit var binding:FragmentAdminHomeBinding
    lateinit var database: DatabaseReference
    lateinit var medicineArray: ArrayList<Medicine>
    lateinit var adapter: UserMedicineAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_home, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)
        medicineArray = arrayListOf()
        adapter = UserMedicineAdapter(medicineArray, requireContext())
        binding.userMedicineRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.userMedicineRecycle.adapter = adapter
        binding.gridLayout.visibility = View.GONE
        binding.gridLayout.visibility = View.GONE
        binding.addMedicine.visibility = View.GONE
        binding.medicineList.visibility = View.GONE
        binding.expireMedicine.visibility = View.GONE
        binding.employee.visibility = View.GONE
        binding.userRecycler.visibility = View.GONE
        database = Firebase.database.reference
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("User").child(uid)
        database.get().addOnSuccessListener {
            if (it.hasChild("Admin")) {
                binding.gridLayout.visibility = View.VISIBLE
                binding.gridLayout.visibility = View.VISIBLE
                binding.addMedicine.visibility = View.VISIBLE
                binding.medicineList.visibility = View.VISIBLE
                binding.expireMedicine.visibility = View.VISIBLE
                binding.employee.visibility = View.VISIBLE
            } else if (it.hasChild("Employee")) {
                binding.gridLayout.visibility = View.VISIBLE
                binding.gridLayout.visibility = View.VISIBLE
                binding.addMedicine.visibility = View.VISIBLE
                binding.medicineList.visibility = View.VISIBLE
                binding.expireMedicine.visibility = View.VISIBLE
            } else if (it.hasChild("User")) {
                binding.userRecycler.visibility = View.VISIBLE
            }
        }
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
            bundle.putString("price", it.price.toString())
            bundle.putString("date",it.date)
            bundle.putString("details",it.details)
            bundle.putString("image",it.image)
            findNavController().navigate(R.id.action_adminHomeFragment_to_userMedicineDetailsFragment,bundle)
        }
        return binding.root
    }

    private fun searchList(text: String) {
        val searchItem = ArrayList<Medicine>()
        for (medicine in medicineArray) {
            if (medicine.name?.lowercase()?.contains(text.lowercase()) == true) {
                searchItem.add(medicine)
            }
        }
        adapter.searchUserMedicineList(searchItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMedicine()
    }

    private fun loadMedicine() {
        activityUtil.setFullScreenLoading(true)
        database = FirebaseDatabase.getInstance().getReference("Medicine")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityUtil.setFullScreenLoading(false)
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val medicine = snap.getValue(Medicine::class.java)
                        medicine?.medicineId = snap.key
                        if (!medicineArray.contains(medicine) && isExpiredDate(medicine?.date)) {
                            medicineArray.add(medicine!!)
                        }
                    }
                    adapter.searchUserMedicineList(medicineArray)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show()
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
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}