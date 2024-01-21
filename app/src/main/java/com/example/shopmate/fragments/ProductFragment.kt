package com.example.shopmate.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopmate.adapter.ProductAdapter
import com.example.shopmate.databinding.FragmentProductBinding
import com.example.shopmate.databinding.ProductDialogBinding
import com.example.shopmate.model.Product
import com.example.shopmate.viewmodel.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private lateinit var productDialogBinding: ProductDialogBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var dialog: AlertDialog

    private var productList: ArrayList<Product> = arrayListOf()
    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        productDialogBinding = ProductDialogBinding.inflate(LayoutInflater.from(requireContext()))
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ProductAdapter(requireContext(), productList)
        binding.recyclerView2.adapter = adapter

        // fetching all products
        productViewModel.fetchAllProducts()
        productViewModel.allProducts.observe(viewLifecycleOwner, Observer {
            productList = it as ArrayList<Product>
            adapter.updateData(productList)
        })

        // add new customer
        binding.btnAddProduct.setOnClickListener {
            showProductDialog()
        }

        // delete, update product
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onDeleteClick(product: Product) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Do you want to delete ${product.name.toString()}?")
                    .setPositiveButton("Yes") { _, _ ->
                        productList.remove(product)
                        adapter.updateData(productList)
                        productViewModel.deleteProduct(product)
                    }
                    .setNegativeButton("No", null)
                    .create()
                    .show()
            }

            override fun onUpdateClick(product: Product) {
                showProductDialog(product.name)
            }

            override fun onItemClick() {
                // will open a new activity
                Toast.makeText(requireContext(), "Open a new activity!", Toast.LENGTH_SHORT).show()
            }

        })

        // searching for a product
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.toString().isNotEmpty()) {
                    adapter.filter.filter(p0)
                } else {
                    adapter.updateData(productList)
                }
                return true
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    // to add and update
    private fun showProductDialog(productName: String? = null) {
        productDialogBinding = ProductDialogBinding.inflate(layoutInflater)
        if (productName != null) {
            productDialogBinding.productName.setText(productName)
        }
        val dialogView = productDialogBinding.root
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Product")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(requireContext(), "Cancelled!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        dialog = builder.create()
        dialog.show()
        val saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveBtn.isEnabled = false


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val name = productDialogBinding.productName.text.toString().trim()
                saveBtn.isEnabled = name.isNotEmpty()
            }
        }
        productDialogBinding.productName.addTextChangedListener(textWatcher)

        saveBtn.setOnClickListener {
            val name = productDialogBinding.productName.text.toString().trim()
            val product = Product(name)
            if (productList.contains(product)) {
                Toast.makeText(requireContext(), "${product.name} already exists!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                productList.add(product)
                adapter.updateData(productList)
                productViewModel.addProduct(product)
                dialog.dismiss()
            }
        }
    }
}