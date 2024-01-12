package com.example.shopmate.fragments

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.shopmate.R
import com.example.shopmate.adapter.CustomerAdapter
import com.example.shopmate.databinding.CustomerDialogBinding
import com.example.shopmate.databinding.FragmentCustomerBinding
import com.example.shopmate.model.Customer
import com.example.shopmate.utils.UiUtils
import com.example.shopmate.viewmodel.CustomerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CustomerFragment : Fragment() {

    private lateinit var binding: FragmentCustomerBinding
    private lateinit var customerDialogBinding: CustomerDialogBinding
    private lateinit var adapter: CustomerAdapter
    private lateinit var dialog: AlertDialog

    // taking image uri
    private var imageUri: Uri? = null
    private lateinit var selectImage: ActivityResultLauncher<String>

    private var customerList: ArrayList<Customer> = arrayListOf()
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerBinding.inflate(layoutInflater, container, false)
        customerDialogBinding = CustomerDialogBinding.inflate(LayoutInflater.from(requireContext()))
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]


        // select image
        selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it
            customerDialogBinding.customerImage.setImageURI(imageUri)
            Log.d("ImageError1", imageUri.toString())
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // setting adapter
        adapter = CustomerAdapter(requireContext(), customerList)
        binding.recyclerView.adapter = adapter

        // fetching all customers
        customerViewModel.fetchAllCustomer()
        customerViewModel.allCustomers.observe(viewLifecycleOwner, Observer {
            customerList = it as ArrayList<Customer>
            adapter.updateData(customerList)
        })

        // new customer
        binding.btnAddCustomer.setOnClickListener {
            showContactDialog()
        }

        // delete, update a customer
        adapter.setOnItemClickListener(object : CustomerAdapter.OnItemClickListener {
            override fun onDeleteClick(customer: Customer) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Do you want to delete ${customer.name.toString()}?")
                    .setPositiveButton("Yes") { _, _ ->
                        customerList.remove(customer)
                        adapter.updateData(customerList)
                        customerViewModel.deleteCustomer(customer.phoneNumber.toString())
                    }
                    .setNegativeButton("No"){_, _ ->
                        //...
                    }
                    .create()
                    .show()

            }

            override fun onUpdateClick(customer: Customer) {
                showContactDialog(
                    customer.name,
                    customer.phoneNumber,
                    customer.email,
                    customer.image
                )
            }

        })


        // search customer
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(name: String?): Boolean {
                if (name.toString().isNotEmpty()) {
                    adapter.filter.filter(name)
                } else {
                    adapter.updateData(customerList)
                }
                return true
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    // to add, update customer
    private fun showContactDialog(
        customerName: String? = null,
        customerPhone: String? = null,
        customerEmail: String? = null,
        customerImage: String? = null
    ) {
        customerDialogBinding = CustomerDialogBinding.inflate(layoutInflater)
        if (customerName != null && customerEmail != null && customerPhone != null) {
            customerDialogBinding.customerName.setText(customerName)
            customerDialogBinding.customerNumber.setText(customerPhone)
            customerDialogBinding.customerEmail.setText(customerEmail)
            imageUri = null
        }

        if (customerImage != null) {
            Glide.with(requireContext())
                .load(customerImage)
                .placeholder(R.drawable.baseline_person_24)
                .into(customerDialogBinding.customerImage)
        }

        val dialogView = customerDialogBinding.root

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Contact")
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


        // monitor changes in edittext
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val name = customerDialogBinding.customerName.text.toString().trim()
                val email = customerDialogBinding.customerEmail.text.toString().trim()
                val phone = customerDialogBinding.customerName.text.toString().trim()

                saveBtn.isEnabled = name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()
            }
        }

        customerDialogBinding.customerName.addTextChangedListener(textWatcher)
        customerDialogBinding.customerEmail.addTextChangedListener(textWatcher)
        customerDialogBinding.customerNumber.addTextChangedListener(textWatcher)


        customerDialogBinding.customerImage.setOnClickListener {
            saveBtn.isEnabled = true
            selectImage.launch("image/*")
        }


        var imageUrl: String? = customerImage
//        var imageUrlWhenUpdated: String? = customerImage

        Log.d("ImageError2", imageUri.toString())

        customerDialogBinding.uploadCustomerImage.setOnClickListener {
            if (imageUri != null) {
                // reference of storage
                val storageRef = Firebase.storage.getReference("images")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("customer-images")
                    .child("${imageUri?.lastPathSegment}")

                UiUtils.showDialog(requireContext())
                UiUtils.dialog?.setCancelable(true)
                storageRef.putFile(imageUri!!)
                    .addOnSuccessListener {
                        storageRef.downloadUrl
                            .addOnSuccessListener { image ->
                                imageUrl = image.toString()
                                UiUtils.dismissDialog()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                UiUtils.dismissDialog()
                            }
                    }
            } else {
                Toast.makeText(requireContext(), "Please select an image!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        saveBtn.setOnClickListener {
            val name = customerDialogBinding.customerName.text.toString().trim()
            val email = customerDialogBinding.customerEmail.text.toString().trim()
            val phone = customerDialogBinding.customerNumber.text.toString().trim()


//            if(customerImage != null){
//                imageUrl = imageUrlWhenUpdated
//            }

            val customer = Customer(
                name = name,
                phoneNumber = phone,
                email = email,
                image = imageUrl
            )
            customerList.add(customer)
            adapter.updateData(customerList)
            customerViewModel.addCustomer(customer)
            dialog.dismiss()
        }
    }
}