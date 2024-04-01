package com.example.medicinestore.presentation.dashboard.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinestore.R

class EmployeeAdapter(private var data: ArrayList<EmployeeModel>, internal var context: Context) :
    RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val email: TextView
        val delete: TextView
        val card: CardView

        init {
            name = itemView.findViewById(R.id.employeeNameTv)
            email = itemView.findViewById(R.id.employeeEmailTv)
            delete = itemView.findViewById(R.id.employeeDeleteBtn)
            card = itemView.findViewById(R.id.employeeItemCard)
        }

        fun bind(employee: EmployeeModel) {
            name.text = employee.name
            email.text = employee.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.employee_list_item, parent, false)
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