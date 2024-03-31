package com.example.medicinestore.presentation.dashboard.medicin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinestore.R

class OrderMedicineAdapter(private var data:ArrayList<Medicine>, internal var context: Context):RecyclerView.Adapter<OrderMedicineAdapter.ViewHolder>() {
class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    val name:TextView
    val company:TextView
    val layout:LinearLayout
    init {
        name = itemView.findViewById(R.id.orderMedicineItemName)
        company = itemView.findViewById(R.id.orderMedicineItemCompany)
        layout = itemView.findViewById(R.id.orderMedicineId)
    }
    fun bind(medicine: Medicine){
        name.text = medicine.name
        company.text = medicine.company
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.order_medicine_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }
}