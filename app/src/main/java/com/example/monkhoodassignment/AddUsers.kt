package com.example.monkhoodassignment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.monkhoodassignment.databinding.ActivityAddUsersBinding

class AddUsers : AppCompatActivity() {

    private lateinit var binding : ActivityAddUsersBinding
    private var imgBmp : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAddPic.setOnClickListener {
            displayImageDialog()
        }

        binding.btnSave.setOnClickListener {
            if (!validatAllFields()) {return@setOnClickListener}
        }
    }
    private fun validatAllFields(): Boolean {

        if(imgBmp == null){
            Toast.makeText(this@AddUsers, "Please select a profile image", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.etName.text.isEmpty()) {
            Toast.makeText(this@AddUsers, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etName.requestFocus()
            return false
        }

        if(binding.etMail.text.isEmpty()) {
            Toast.makeText(this@AddUsers, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etMail.requestFocus()
            return false
        }

        if(binding.etPhone.text.isEmpty()) {
            Toast.makeText(this@AddUsers, "Phone field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etPhone.requestFocus()
            return false
        }

        if(binding.tvDOB.text.isEmpty()) {
            Toast.makeText(this@AddUsers, "Please choose your DOB!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etMail.text.toString()).matches()){
            Toast.makeText(this, "Email format is invalid. Please provide a valid email!", Toast.LENGTH_SHORT).show()
            binding.etMail.requestFocus()
            return false
        }

        return true
    }

    private fun displayImageDialog() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Choose Image")
            .setPositiveButton("Open Camera",
                DialogInterface.OnClickListener { dialog, id ->
                    OpenCamera()
                })
            .setNegativeButton("from storage",
                DialogInterface.OnClickListener { dialog, id ->
                    OpenLocalStorage()
                })
        builder.show()
    }

    private fun OpenLocalStorage() {
        TODO("Not yet implemented")
    }

    private fun OpenCamera() {
        TODO("Not yet implemented")
    }
}