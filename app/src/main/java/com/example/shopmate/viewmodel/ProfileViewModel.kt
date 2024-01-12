package com.example.shopmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopmate.model.Shop
import com.example.shopmate.utils.Response
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // implementing everything here, ain't creating repository
    private val _shopInfo = MutableLiveData<Shop>()
    private val _response = MutableLiveData<Response<String>>()

    val shopInfo: LiveData<Shop> = _shopInfo
    val response: LiveData<Response<String>> = _response

    private val auth = Firebase.auth
    private val database = Firebase.firestore

    fun fetchShopInfo() {
        database.collection("shops").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.exists()) {
                    _shopInfo.postValue(snapshot.toObject(Shop::class.java))
                    _response.postValue(Response.Success())
                }else{
                    _response.postValue(Response.Failure("Database error!!"))
                }
            }
            .addOnFailureListener { error ->
                _response.postValue(Response.Failure(error.message.toString()))
            }
    }

}