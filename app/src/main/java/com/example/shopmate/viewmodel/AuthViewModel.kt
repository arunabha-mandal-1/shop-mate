package com.example.shopmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.shopmate.repository.AuthRepository
import com.example.shopmate.utils.Response
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()

    val user: LiveData<FirebaseUser?> = authRepository.user
    val response: LiveData<Response<String>> = authRepository.response

    fun login(email: String, password: String){
        authRepository.login(email, password)
    }

    fun signup(email: String, password: String){
        authRepository.signup(email, password)
    }

    fun logout(){
        authRepository.logout()
    }
}