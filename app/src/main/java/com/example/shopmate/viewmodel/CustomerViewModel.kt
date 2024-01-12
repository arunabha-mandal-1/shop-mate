package com.example.shopmate.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopmate.model.Customer
import com.example.shopmate.utils.Response
import com.example.shopmate.utils.UiUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val _addResponse = MutableLiveData<Response<String>>()
    private val _deleteResponse = MutableLiveData<Response<String>>()
    private val _updateResponse = MutableLiveData<Response<String>>()
    private val _fetchResponse = MutableLiveData<Response<String>>()
    private val _searchResponse = MutableLiveData<Response<String>>()

    private val _allCustomers = MutableLiveData<List<Customer>>()
    private val _searchedCustomers = MutableLiveData<List<Customer>>()

    val addResponse: LiveData<Response<String>> = _addResponse
    val deleteResponse: LiveData<Response<String>> = _deleteResponse
    val updateResponse: LiveData<Response<String>> = _updateResponse
    val fetchResponse: LiveData<Response<String>> = _fetchResponse
    val searchResponse: LiveData<Response<String>> = _searchResponse

    val allCustomers: LiveData<List<Customer>> = _allCustomers
    val searchedCustomers: LiveData<List<Customer>> = _searchedCustomers

    private val auth = Firebase.auth
    private val database = Firebase.firestore

    fun addCustomer(customer: Customer) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("customers").document(customer.phoneNumber!!)
            .set(customer)
            .addOnSuccessListener {
                _addResponse.postValue(Response.Success())
            }
            .addOnFailureListener {
                _addResponse.postValue(Response.Failure(it.message.toString()))
            }
    }

    fun updateCustomer(customer: Customer) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("customers").document(customer.phoneNumber!!)
            .set(customer)
            .addOnSuccessListener {
                _updateResponse.postValue(Response.Success())
            }
            .addOnFailureListener {
                _updateResponse.postValue(Response.Failure(it.message.toString()))
            }
    }

    fun deleteCustomer(phoneNumber: String) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("customers").document(phoneNumber)
            .delete()
            .addOnSuccessListener {
                _deleteResponse.postValue(Response.Success())
            }
            .addOnFailureListener {
                _deleteResponse.postValue(Response.Failure(it.message.toString()))
            }
    }

    fun fetchAllCustomer() {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("customers")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _fetchResponse.postValue(Response.Failure(error.message.toString()))
                }
//                Log.d("Abc", allCustomers.value.toString())
                _allCustomers.postValue(value?.toObjects(Customer::class.java))
                _fetchResponse.postValue(Response.Success())
            }
    }


    fun searchCustomer(name: String) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("customers")
            .whereEqualTo("name", name)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _searchResponse.postValue(Response.Failure(error.message.toString()))
                    return@addSnapshotListener
                }
                _searchedCustomers.postValue(value?.toObjects(Customer::class.java))
            }
    }

    init {
        fetchAllCustomer()
    }

}