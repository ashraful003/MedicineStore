package com.example.medicinestore.presentation.dashboard.medicin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentUserMedicineDetailsBinding
import com.example.medicinestore.util.MSActivityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserMedicineDetailsFragment : Fragment() {
    @Inject
    lateinit var activityUtil: MSActivityUtil
    private lateinit var binding: FragmentUserMedicineDetailsBinding
    lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_medicine_details, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(true)
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        database = Firebase.database.reference
        binding.userMedicineName.text = requireArguments().getString("name")
        binding.companyNameTv.text = requireArguments().getString("company")
        binding.expireDateTv.text = requireArguments().getString("date")
        binding.medicinePriceTv.text = requireArguments().getString("price")
        binding.medicineDetailsTv.text = requireArguments().getString("details")
        val pimage = requireArguments().getString("image")
        if (!pimage.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimage)
                .error(R.drawable.ic_human)
                .into(binding.userMedicineImage)
        } else {
            Log.e("EmployeeDetailsFragment", "Image URL is null or empty")
        }
        val amountStream = RxTextView.textChanges(binding.orderQuantity)
            .skipInitialValue()
            .map { amount ->
                amount.isEmpty()
            }
        amountStream.subscribe {
            if (it) {
                binding.btnOrder.isEnabled = false
                binding.btnOrder.backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.blue_500)
            } else {
                binding.orderQuantity.error = null
                binding.btnOrder.isEnabled = true
                binding.btnOrder.backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
            }
        }
        fun getQuantity(): Int {
            val text = binding.orderQuantity.text.toString()
            return text.toIntOrNull() ?: 1
        }

        fun setQuantity(quantity: Int) {
            binding.orderQuantity.setText(quantity.toString())
        }

        binding.minusBtn.setOnClickListener {
            var quantity = getQuantity()
            if (quantity > 1) {
                quantity--
                setQuantity(quantity)
            }
        }

        binding.plusBtn.setOnClickListener {
            var quantity = getQuantity()
            quantity++
            setQuantity(quantity)
        }
        binding.btnOrder.setOnClickListener {

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            database.child("User").child(uid).get().addOnSuccessListener {
                val address = it.child("location").value.toString()
                val phone = it.child("number").value.toString()
                order(address,uid,pimage,phone)
                findNavController().navigate(R.id.action_userMedicineDetailsFragment_to_orderMedicineFragment)
            }
        }

        return binding.root
    }

    private fun order(address: String, uid: String, pimage: String?, phone: String){
        activityUtil.setFullScreenLoading(true)

        val priceText = binding.medicinePriceTv.text.toString()
        val amountText = binding.orderQuantity.text.toString()

        val price = priceText.toIntOrNull()
        val amount = amountText.toIntOrNull()

        if (price != null && amount != null) {
            val totalPrice = price * amount

            val medicineId = database.push().key!!
            val medicine = HashMap<String, Any>()
            pimage?.let { medicine["image"] = it }
            medicine["userId"] = uid
            medicine["name"] = binding.userMedicineName.text.toString().trim()
            medicine["company"] = binding.companyNameTv.text.toString().trim()
            medicine["details"] = binding.medicineDetailsTv.text.toString().trim()
            medicine["twoNumbers"] = "$price × $amount"
            medicine["price"] = totalPrice.toString().trim()
            medicine["date"] = binding.expireDateTv.text.toString().trim()
            medicine["address"] = address.trim()
            medicine["number"] = phone.trim()

            database.child("order").child(medicineId).setValue(medicine)
            Toast.makeText(activity, getText(R.string.order_message), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, getText(R.string.invalid_quantity), Toast.LENGTH_SHORT).show()
        }

        activityUtil.setFullScreenLoading(false)
    }
}