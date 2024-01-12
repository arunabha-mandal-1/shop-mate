package com.example.shopmate.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.shopmate.model.Shop
import com.example.shopmate.repository.RegisterRepository
import com.example.shopmate.utils.Response

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val registerRepository = RegisterRepository()

    val response: LiveData<Response<String>> = registerRepository.response

    fun storeShopData(shop: Shop, image: Uri){
        registerRepository.storeShopData(shop, image)
    }
}