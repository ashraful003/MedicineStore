package com.example.medicinestore.presentation.dashboard.medicin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinestore.R

class MedicineListAdapter(private var data: ArrayList<Medicine>, internal var context: Context) :
    RecyclerView.Adapter<MedicineListAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val company: TextView
        val edite: TextView
        val card: CardView

        init {
            name = itemView.findViewById(R.id.medicineListItemName)
            company = itemView.findViewById(R.id.medicineListItemCompany)
            edite = itemView.findViewById(R.id.itemEditeBtn)
            card = itemView.findViewById(R.id.medicineItemCard)
        }

        fun bind(medicine: Medicine) {
            name.text = medicine.name
            company.text = medicine.company
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.medicine_list_item, parent, false)
        return viewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }
}