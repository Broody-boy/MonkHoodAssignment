package com.example.monkhoodassignment

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.monkhoodassignment.databinding.ActivityModifyUserBinding
import com.example.monkhoodassignment.modal.User
import com.example.monkhoodassignment.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ModifyUser : AppCompatActivity() {

    private lateinit var binding : ActivityModifyUserBinding

    private lateinit var vm : ViewModel
    private var CurrentUUIDtoBeModified : String? = ""
    private var imgBmp : Bitmap? = null
    //private lateinit var sharedPreferences : SharedPreferences

    private var uri: Uri? = null
    //private lateinit var storageRef: StorageReference
    //private lateinit var storage: FirebaseStorage
    private lateinit var firestore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModifyUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModel()
        firestore = FirebaseFirestore.getInstance()

        CurrentUUIDtoBeModified = intent.getStringExtra("UUID_TO_UPDATE")

        var userGot : User? = null

        vm.getUserFromUUID(CurrentUUIDtoBeModified){ user->
            userGot = user
            Glide.with(this).load(userGot!!.imgProfile).into(binding.imgProfile)
            binding.etName.setText(userGot!!.name)
            binding.etMail.setText(userGot!!.mail)
            binding.etPhone.setText(userGot!!.phone.toString())
            binding.tvDOB.setText(userGot!!.dob)
        }

        binding.btnSave.setOnClickListener {
            val hashMap = hashMapOf<Any, Any>("UUID" to CurrentUUIDtoBeModified!!, "name" to binding.etName.text.toString(),
                "imgProfile" to userGot!!.imgProfile!!, "mail" to binding.etMail.text.toString(),
                "phone" to binding.etPhone.text.toString().toLong(),"dob" to binding.tvDOB.text.toString())

            firestore.collection("Users").document(CurrentUUIDtoBeModified!!).set(hashMap)
        }
    }
}