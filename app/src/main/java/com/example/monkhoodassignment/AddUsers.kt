package com.example.monkhoodassignment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import com.example.monkhoodassignment.databinding.ActivityAddUsersBinding

class AddUsers : AppCompatActivity() {

    private lateinit var binding : ActivityAddUsersBinding
    private var imgBmp : Bitmap? = null

    private enum class MODE{
        OPEN_CAMERA, OPEN_EXT_STORAGE
    }

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
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, MODE.OPEN_EXT_STORAGE.ordinal)
        }
    }

    private fun OpenCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, MODE.OPEN_CAMERA.ordinal)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == MODE.OPEN_EXT_STORAGE.ordinal) {
                val imageUri = data?.data
                imgBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            }

            if(requestCode == MODE.OPEN_CAMERA.ordinal) {
                imgBmp = data?.extras?.get("data") as Bitmap
            }

            try {
                binding.imgProfile.setImageBitmap(imgBmp)
            }catch (e :Exception){
                Toast.makeText(this@AddUsers, "Image is not valid", Toast.LENGTH_SHORT).show()
                imgBmp = null
            }
        }


    }
}