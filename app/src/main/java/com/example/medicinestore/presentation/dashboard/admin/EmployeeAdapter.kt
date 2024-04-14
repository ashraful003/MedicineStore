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

class EmployeeAdapter(private var data: ArrayList<EmployeeModel>, internal var context: Context) :
    RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {
    var onItemClick:((EmployeeModel) -> Unit)? = null
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val post: TextView
        val image: ImageView
        val card: CardView

        init {
            name = itemView.findViewById(R.id.cardEmpName)
            post = itemView.findViewById(R.id.cardEmpPost)
            image = itemView.findViewById(R.id.cardImage)
            card = itemView.findViewById(R.id.empCard)
        }

        fun bind(employee: EmployeeModel) {
            name.text = employee.name
            post.text = employee.post
         //  image.setImageResource(employee.image!!.toInt())
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
        holder.card.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }
    fun searchDataList(searchListData:List<EmployeeModel>){
        data = searchListData as ArrayList<EmployeeModel>
        notifyDataSetChanged()
    }
}