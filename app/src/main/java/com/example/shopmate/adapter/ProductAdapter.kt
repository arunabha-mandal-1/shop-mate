package com.example.shopmate.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.shopmate.databinding.ProductItemLayoutBinding
import com.example.shopmate.model.Product

class ProductAdapter(
    private val context: Context,
    private var list: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private lateinit var myListener: OnItemClickListener
    private var filteredList: List<Product> = list

    interface OnItemClickListener {
        fun onDeleteClick(product: Product)
        fun onUpdateClick(product: Product)
        fun onItemClick()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    inner class ProductViewHolder(
        val binding: ProductItemLayoutBinding,
        val listener: OnItemClickListener // imported
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = ProductItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(view, myListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.binding.productName.text = list[position].name
        holder.binding.btnDelete.setOnClickListener {
            holder.listener.onDeleteClick(list[position])
        }
        holder.binding.btnEdit.setOnClickListener {
            holder.listener.onUpdateClick(list[position])
        }
        holder.binding.productName.setOnClickListener {
            holder.listener.onItemClick()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<Product>) {
        list = newList.sortedBy { it.name }
        notifyDataSetChanged()
    }

    // implementing search functionality using filter
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString().lowercase().trim()
                val filtered = if (query.isEmpty()) {
                    list
                } else {
                    list.filter { it.name.toString().lowercase().contains(query) }
                }
                val filteredResult = FilterResults()
                filteredResult.values = filtered
                return filteredResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredList = p1?.values as? List<Product> ?: emptyList()
                val newList: ArrayList<Product> = arrayListOf()
                for(i in filteredList){
                    newList.add(i)
                }
                updateData(newList)
                notifyDataSetChanged()
            }

        }
    }
}