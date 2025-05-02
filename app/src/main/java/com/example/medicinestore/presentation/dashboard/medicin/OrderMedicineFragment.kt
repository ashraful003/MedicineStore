package com.example.medicinestore.presentation.dashboard.medicin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentOrderMedicineBinding
import com.example.medicinestore.presentation.dashboard.medicin.MedicinViewModel
import com.example.medicinestore.presentation.dashboard.medicin.Medicine
import com.example.medicinestore.presentation.dashboard.medicin.OrderMedicineAdapter
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderMedicineFragment : Fragment() {

    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var binding: FragmentOrderMedicineBinding
    private lateinit var viewModel: MedicinViewModel
    private lateinit var adapter: OrderMedicineAdapter
    private var userArray: ArrayList<Medicine> = arrayListOf()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_medicine, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)

        database = Firebase.database.reference

        setupRecyclerView()
        setupSearchView()
        setupItemClick()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.orderMedicineRecycle.layoutManager = LinearLayoutManager(activity)
        adapter = OrderMedicineAdapter(userArray, requireContext())
        binding.orderMedicineRecycle.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchMedicine.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchList(it) }
                return true
            }
        })
    }

    private fun setupItemClick() {
        adapter.onItemClick = {
            val bundle = Bundle().apply {
                putString("id", it.medicineId)
                putString("uid", it.userId)
                putString("name", it.name)
                putString("company", it.company)
                putString("price", it.price)
                putString("date", it.date)
                putString("details", it.details)
                putString("address", it.address)
                putString("image", it.image)
                putString("twoNumbers", it.twoNumbers)
            }
            findNavController().navigate(
                R.id.action_orderMedicineFragment_to_orderDetailsFragment,
                bundle
            )
        }
    }

    override fun onResume() {
        super.onResume()
        activityUtil.setFullScreenLoading(true)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().getReference("User").child(uid)
        userRef.get().addOnSuccessListener {
            if (it.hasChild("Admin") || it.hasChild("Employee")) {
                adminLoadOrder()
            } else if (it.hasChild("User")) {
                userLoadOrder()
            }
        }
    }

    private fun adminLoadOrder() {
        userArray.clear()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("Order")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityUtil.setFullScreenLoading(false)
                userArray.clear()
                for (uid in snapshot.children) {
                    val userID = uid.key
                    for (snap in uid.children) {
                        val medicineId = snap.key
                        val medicine = snap.getValue(Medicine::class.java)
                        medicine?.medicineId = snap.key
                        medicine?.let {
                            it.medicineId = medicineId
                            it.userId = userID
                            userArray.add(0, it)
                        }
                    }
                }
                adapter.searchMedicineList(userArray)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun userLoadOrder() {
        userArray.clear()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("Order").child(uid)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityUtil.setFullScreenLoading(false)
                userArray.clear()
                for (snap in snapshot.children) {
                    val medicine = snap.getValue(Medicine::class.java)
                    medicine?.medicineId = snap.key
                    medicine?.let { userArray.add(0, it) }
                }
                adapter.searchMedicineList(userArray)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun searchList(text: String) {
        val filteredList = userArray.filter {
            it.name?.lowercase()?.contains(text.lowercase()) == true
        }
        adapter.searchMedicineList(ArrayList(filteredList))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MedicinViewModel::class.java)
    }
}
