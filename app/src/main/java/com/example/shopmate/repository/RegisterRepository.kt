package com.example.shopmate.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopmate.model.Shop
import com.example.shopmate.utils.Response
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class RegisterRepository {
    private val _response = MutableLiveData<Response<String>>()
//    private val _shopDetails = MutableLiveData<DocumentSnapshot>()

    val response: LiveData<Response<String>> = _response
//    val shopDetails: LiveData<DocumentSnapshot> = _shopDetails

    private val auth = Firebase.auth
    private val database = Firebase.firestore
    private val storage = Firebase.storage

    fun storeShopData(shop: Shop, image: Uri) {

        // getting reference of storage
        val storageRef = storage.getReference("images")
            .child(auth.currentUser!!.uid)
            .child("${image.lastPathSegment}")

        // storing image and data
        storageRef.putFile(image)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { imageUri ->
                        database.collection("shops")
                            .document(auth.currentUser!!.uid)
                            .set(shop)
                            .addOnSuccessListener {
                                database.collection("shops").document(auth.currentUser!!.uid)
                                    .update("image", imageUri)
                                    .addOnSuccessListener {
                                        _response.postValue(Response.Success())
                                    }
                                    .addOnFailureListener {
                                        _response.postValue(Response.Failure(it.message.toString()))
                                    }
//                                _response.postValue(Response.Success())
                            }
                            .addOnFailureListener {
                                _response.postValue(Response.Failure(it.message.toString()))
                            }
                    }
                    .addOnFailureListener {
                        _response.postValue(Response.Failure(it.message.toString()))
                    }
            }
            .addOnFailureListener {
                _response.postValue(Response.Failure(it.message.toString()))
            }

    }
}