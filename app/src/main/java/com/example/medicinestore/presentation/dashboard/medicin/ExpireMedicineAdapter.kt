package com.example.medicinestore.presentation.dashboard.medicin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.medicinestore.R

class ExpireMedicineAdapter(private var data:ArrayList<Medicine>, internal var context: Context):RecyclerView.Adapter<ExpireMedicineAdapter.ViewHolder>() {
    var onItemClick:((Medicine) ->Unit)? = null
    var onDeleteItem:((Medicine) -> Unit)? = null
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name:TextView
        val company:TextView
        val delete:TextView
        val card:CardView
        init {
            name = itemView.findViewById(R.id.expireMedicineItemName)
            company = itemView.findViewById(R.id.expireMedicineItemCompany)
            delete = itemView.findViewById(R.id.itemDeleteBtn)
            card = itemView.findViewById(R.id.expireMedicineItemCard)
        }
        fun bind(medicine: Medicine){
            name.text = medicine.name
            company.text = medicine.company
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.expire_medicine_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }
   fun searchExpireDataList(searchList:List<Medicine>){
       data = searchList as ArrayList<Medicine>
       notifyDataSetChanged()
   }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.card.setOnClickListener {
            onItemClick?.invoke(item)
        }
        holder.delete.setOnClickListener {
            onDeleteItem?.invoke(item)
        }
    }
}