package com.example.medicinestore.presentation.dashboard.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicinestore.R


class EmployeeAdapter(
    private var data: ArrayList<EmployeeModel>, private val context: Context) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    var onItemClick: ((EmployeeModel) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.employeelistItemName)
        val post: TextView = itemView.findViewById(R.id.employeelistItemPost)
        val imageV: ImageView = itemView.findViewById(R.id.employeeListItemImage)
        val card: CardView = itemView.findViewById(R.id.employeelistItemCard)

        fun bind(employee: EmployeeModel) {
            name.text = employee.name
            post.text = employee.post
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.employee_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        Glide.with(context).load(item.image).into(holder.imageV)
        holder.card.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }
    fun searchDataList(searchListData: List<EmployeeModel>) {
        data = ArrayList(searchListData)
        notifyDataSetChanged()
    }
}
