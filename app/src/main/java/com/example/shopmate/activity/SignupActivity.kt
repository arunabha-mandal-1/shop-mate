package com.example.shopmate.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopmate.databinding.ActivitySignupBinding
import com.example.shopmate.utils.Response
import com.example.shopmate.utils.UiUtils
import com.example.shopmate.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: AuthViewModel

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confPassword: TextInputEditText
    private lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        email = binding.etEmail
        password = binding.etPassword
        confPassword = binding.etConfPassword
        btnSignup = binding.btnSignup

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        btnSignup.setOnClickListener {

            UiUtils.showDialog(this)
            btnSignup.isClickable = false

            if (password.text!!.toString() != confPassword.text!!.toString()) {
                Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show()
                UiUtils.dismissDialog()
                btnSignup.isClickable = true
            } else if (email.text.toString().isNotEmpty() && password.text.toString()
                    .isNotEmpty() && confPassword.text.toString().isNotEmpty()
            ) {
                viewModel.signup(email.text.toString(), password.text.toString())
                viewModel.response.observe(this, Observer {
                    when (it) {
                        is Response.Success -> {
                            startActivity(Intent(this, RegisterActivity::class.java))
                            UiUtils.dismissDialog()
                            btnSignup.isClickable = true
                            finishAffinity()
                        }

                        is Response.Failure -> {
                            Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                            UiUtils.dismissDialog()
                            btnSignup.isClickable = true
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Fill up all the credentials", Toast.LENGTH_SHORT).show()
                UiUtils.dismissDialog()
                btnSignup.isClickable = true
            }
        }

    }
}