package com.example.shopmate.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopmate.databinding.ActivityLoginBinding
import com.example.shopmate.utils.Response
import com.example.shopmate.utils.UiUtils
import com.example.shopmate.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var goToSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.etEmail
        password = binding.etPassword
        btnLogin = binding.btnLogin
        goToSignup = binding.goToSignup
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]


        goToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {

            btnLogin.isClickable = false
            UiUtils.showDialog(this)

            if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                viewModel.login(email.text.toString(), password.text.toString())
                viewModel.response.observe(this, Observer {
                    when (it) {
                        is Response.Success -> {
                            Firebase.firestore.collection("shops")
                                .document(Firebase.auth.currentUser!!.uid).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        btnLogin.isClickable = true
                                        UiUtils.dismissDialog()
                                        finish()
                                    } else {
                                        startActivity(Intent(this, RegisterActivity::class.java))
                                        btnLogin.isClickable = true
                                        UiUtils.dismissDialog()
                                        finish()
                                    }
                                }
                                .addOnFailureListener { err ->
                                    Toast.makeText(this, err.message, Toast.LENGTH_SHORT).show()
                                    btnLogin.isClickable = true
                                    UiUtils.dismissDialog()
                                }
                        }

                        is Response.Failure -> {
                            Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                            btnLogin.isClickable = true
                            UiUtils.dismissDialog()
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Fill up all the credentials", Toast.LENGTH_SHORT).show()
                btnLogin.isClickable = true
                UiUtils.dismissDialog()
            }
        }
    }
}