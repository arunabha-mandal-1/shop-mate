package com.example.shopmate.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopmate.R
import com.example.shopmate.databinding.CustomerItemLayoutBinding
import com.example.shopmate.model.Customer

class CustomerAdapter(
    private val context: Context,
    private var list: List<Customer>
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>(), Filterable {

    private lateinit var myListener: OnItemClickListener
    private var filteredList: List<Customer> = list

    interface OnItemClickListener {
        fun onDeleteClick(customer: Customer)
        fun onUpdateClick(customer: Customer)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    inner class CustomerViewHolder(
        val binding: CustomerItemLayoutBinding,
        val listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = CustomerItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomerViewHolder(view, myListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.binding.customerName.text = list[position].name
        holder.binding.customerPhNo.text = list[position].phoneNumber
        holder.binding.customerEmail.text = list[position].email
        Glide.with(context)
            .load(list[position].image)
            .placeholder(R.drawable.baseline_person_24)
            .into(holder.binding.customerImage)

        holder.binding.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + list[position].phoneNumber)
            context.startActivity(intent)
        }

        holder.binding.sendEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:" + list[position].email)
            context.startActivity(intent)
        }

        holder.binding.deleteCustomer.setOnClickListener {
            holder.listener.onDeleteClick(list[position])
        }

        holder.binding.customerImage.setOnClickListener{
            holder.listener.onUpdateClick(list[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<Customer>) {
        list = newList.sortedBy { it.name }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().lowercase().trim()
                val filtered = if(query.isEmpty()){
                    list
                }else{
                    list.filter { it.name.toString().lowercase().contains(query)}
                }
                val filteredResult = FilterResults()
                filteredResult.values = filtered
                return filteredResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Customer> ?: emptyList()
                val newList: ArrayList<Customer> = arrayListOf()
                for(i in filteredList){
                    newList.add(i)
                }
                updateData(newList)
                notifyDataSetChanged()
            }

        }
    }
}

