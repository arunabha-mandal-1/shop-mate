package com.example.shopmate.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopmate.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _allProducts = MutableLiveData<List<Product>>()
    private val _searchedProducts = MutableLiveData<List<Product>>()

    val allProducts: LiveData<List<Product>> = _allProducts
    val searchedProducts: LiveData<List<Product>> = _searchedProducts

    private val auth = Firebase.auth
    private val database = Firebase.firestore


    fun addProduct(product: Product) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("products").document(product.name!!)
            .set(product)
    }

    fun deleteProduct(product: Product) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("products").document(product.name!!)
            .delete()
    }

    fun updateProduct(product: Product) {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("products").document(product.name!!)
            .set(product)
    }

    fun fetchAllProducts() {
        database.collection("shops").document(auth.currentUser!!.uid)
            .collection("products")
            .addSnapshotListener { value, _ ->
                _allProducts.postValue(value?.toObjects(Product::class.java))
            }
    }

    init {
        fetchAllProducts()
    }
}