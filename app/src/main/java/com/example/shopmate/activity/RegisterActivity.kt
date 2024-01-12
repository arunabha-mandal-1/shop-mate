package com.example.shopmate.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopmate.model.Shop
import com.example.shopmate.databinding.ActivityRegisterBinding
import com.example.shopmate.utils.Response
import com.example.shopmate.utils.UiUtils
import com.example.shopmate.viewmodel.AuthViewModel
import com.example.shopmate.viewmodel.RegisterViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var shopName: TextInputEditText
    private lateinit var propName: TextInputEditText
    private lateinit var businessType: TextInputEditText
    private lateinit var established: TextInputEditText
    private lateinit var phoneNo: TextInputEditText
    private lateinit var uploadImage: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var btnRegister: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var authViewModel: AuthViewModel
    private lateinit var registerViewModel: RegisterViewModel

    private var image: Uri? = null
    private lateinit var selectImage: ActivityResultLauncher<String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // if profile is being edited
//        val flag = intent.getStringExtra("flag")
//        if(flag == "edit"){
//            binding.btnRegister.text = "Save"
//        }

        shopName = binding.etShopName
        propName = binding.etProprietorName
        businessType = binding.etTypeOfBusiness
        established = binding.etEstablished
        phoneNo = binding.etPhoneNumber
        uploadImage = binding.uploadImage
        checkBox = binding.checkBox
        btnRegister = binding.btnRegister

        auth = Firebase.auth
        database = Firebase.firestore

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            image = it
            binding.shopImage.setImageURI(image)
        }

        uploadImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        btnRegister.setOnClickListener {

            btnRegister.isClickable = false
            UiUtils.showDialog(this)

            if (shopName.text.toString().isEmpty()
                || propName.text.toString().isEmpty()
                || businessType.text.toString().isEmpty()
                || established.text.toString().isEmpty()
                || phoneNo.text.toString().isEmpty()
            ) {
                btnRegister.isClickable = true
                UiUtils.dismissDialog()

                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else if (image == null) {
                btnRegister.isClickable = true
                UiUtils.dismissDialog()

                Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show()
            }else if(!checkBox.isChecked){
                btnRegister.isClickable = true
                UiUtils.dismissDialog()

                Toast.makeText(this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show()
            }
            else {
                val shop = Shop(
                    email = auth.currentUser!!.email.toString(),
                    shopName = shopName.text.toString(),
                    businessType = businessType.text.toString(),
                    established = established.text.toString(),
                    phoneNumber = phoneNo.text.toString()
                )
                registerViewModel.storeShopData(shop, image!!)
                registerViewModel.response.observe(this, Observer {
                    when (it) {
                        is Response.Success -> {
                            startActivity(Intent(this, MainActivity::class.java))
                            btnRegister.isClickable = true
                            UiUtils.dismissDialog()
                            finish()
                        }

                        is Response.Failure -> {
                            Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                            btnRegister.isClickable = true
                            UiUtils.dismissDialog()
                        }
                    }
                })
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        authViewModel.logout()
    }
}