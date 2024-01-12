package com.example.shopmate.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopmate.utils.Response
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthRepository() {
    private val _user = MutableLiveData<FirebaseUser?>()
    private val _response = MutableLiveData<Response<String>>()

    val user: LiveData<FirebaseUser?> = _user
    val response : LiveData<Response<String>> = _response


    private val auth = Firebase.auth

    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _user.postValue(auth.currentUser)
                    _response.postValue(Response.Success())
                }else{
                    _response.postValue(Response.Failure(it.exception!!.message!!))
                }
            }
    }

    fun signup(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _user.postValue(auth.currentUser)
                    _response.postValue(Response.Success())
                }else{
                    _response.postValue(Response.Failure(it.exception!!.message!!))
                }
            }
    }

    fun logout(){
        auth.signOut()
        _user.postValue(null)
    }


    init {
        if(auth.currentUser != null){
            _user.postValue(auth.currentUser)
        }else{
            _user.postValue(null)
        }
    }
}