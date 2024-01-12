package com.example.shopmate.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.shopmate.R
import com.example.shopmate.activity.LoginActivity
import com.example.shopmate.activity.RegisterActivity
import com.example.shopmate.databinding.FragmentProfileBinding
import com.example.shopmate.utils.Response
import com.example.shopmate.viewmodel.AuthViewModel
import com.example.shopmate.viewmodel.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnLogOut.setOnClickListener {
            authViewModel.logout()
            authViewModel.user.observe(this.viewLifecycleOwner, Observer {
                if (it == null){
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            })
        }

//        binding.btnEditProfile.setOnClickListener {
//            val intent = Intent(requireContext(), RegisterActivity::class.java)
//            intent.putExtra("flag", "edit")
//            startActivity(intent)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileViewModel.fetchShopInfo()
        profileViewModel.response.observe(this.viewLifecycleOwner, Observer { response ->
            when (response) {
                is Response.Success -> {
                    profileViewModel.shopInfo.observe(this.viewLifecycleOwner, Observer { shop ->
                        binding.shopName.text = shop.shopName
                        binding.shopName2.text = shop.shopName
                        binding.propName.text = shop.propName
                        binding.businessType.text = shop.businessType
                        binding.established.text = shop.established
                        binding.phoneNumber.text = shop.phoneNumber
                        binding.email.text = shop.email

                        Glide.with(requireContext())
                            .load(shop.image)
                            .placeholder(R.drawable.shop_image)
                            .into(binding.shopImage)
                    })
                }

                is Response.Failure -> {
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }
}