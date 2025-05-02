package com.example.medicinestore.presentation.dashboard.medicin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicinestore.R
import com.google.firebase.database.DatabaseReference

class OrderMedicineAdapter(private var data: ArrayList<Medicine>, internal var context: Context) :
    RecyclerView.Adapter<OrderMedicineAdapter.ViewHolder>() {
    var onItemClick:((Medicine) -> Unit)? = null
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val company: TextView
        val image: ImageView
        val donTv:TextView
        val layout: CardView

        init {
            name = itemView.findViewById(R.id.orderMedicineItemName)
            company = itemView.findViewById(R.id.orderMedicineItemCompany)
            layout = itemView.findViewById(R.id.orderMedicineItemCard)
            image = itemView.findViewById(R.id.orderMedicineListItemImage)
            donTv = itemView.findViewById(R.id.done)
        }

        fun bind(medicine: Medicine) {
            name.text = medicine.name
            company.text = medicine.company
            donTv.text = medicine.complete
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.order_medicine_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun searchMedicineList(newList: List<Medicine>) {
        this.data = ArrayList(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        Glide.with(context).load(item.image).into(holder.image)
        holder.layout.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }
}