package com.example.medicinestore.presentation.dashboard.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinestore.R
import com.example.medicinestore.presentation.dashboard.medicin.Medicine

class UserMedicineAdapter(private var data: ArrayList<Medicine>, internal var context: Context) :
    RecyclerView.Adapter<UserMedicineAdapter.ViewModel>() {
    class ViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView
        val medicineCompany: TextView
        val image: ImageView
        val card: CardView

        init {
            medicineName = itemView.findViewById(R.id.userMedicineName)
            medicineCompany = itemView.findViewById(R.id.userMedicineCompany)
            image = itemView.findViewById(R.id.userMedicineCardImage)
            card = itemView.findViewById(R.id.userMedicineCv)
        }

        fun bind(medicine: Medicine) {
            medicineName.text = medicine.name
            medicineCompany.text = medicine.company
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.user_medicine_item, parent, false)
        return ViewModel(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    fun searchUserMedicineList(searchMedicine: List<Medicine>) {
        data = searchMedicine as ArrayList<Medicine>
        notifyDataSetChanged()
    }
}