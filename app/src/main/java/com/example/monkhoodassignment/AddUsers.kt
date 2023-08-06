package com.example.monkhoodassignment

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import com.example.monkhoodassignment.databinding.ActivityAddUsersBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID

class AddUsers : AppCompatActivity() {

    private lateinit var binding : ActivityAddUsersBinding
    private lateinit var pd: ProgressDialog

    private lateinit var UUIDString : String
    private var imgBmp : Bitmap? = null
    private lateinit var sharedPreferences : SharedPreferences

    private var uri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore : FirebaseFirestore

    private enum class MODE{
        OPEN_CAMERA, OPEN_EXT_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = this.getSharedPreferences("UsersCollection", Context.MODE_PRIVATE)
        pd = ProgressDialog(this)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding = ActivityAddUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UUIDString = generateUUID()

        binding.imgAddPic.setOnClickListener {
            displayImageDialog()
        }

        binding.tvDOB.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,{view, year, monthOfYear, dayOfMonth ->
                binding.tvDOB.setText("${dayOfMonth}/${monthOfYear + 1}/${year}")   //it is an et, not tv
            }, year, month, day)

            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            if (!validatAllFields()) {return@setOnClickListener}
            saveToSharedPreference()
            uploadImageToFirebaseStorage(imgBmp)
        }
    }

    private fun generateUUID() : String{
        return UUID.randomUUID().toString()
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

    private fun StoreLocallyAndReturnLink(imgBmp: Bitmap?, UUIDString: String?): String? {
        val directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (directory != null) {
            val file = File(directory, UUIDString + ".jpg")
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                imgBmp?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                return file.path
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    outputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }

    private fun saveToSharedPreference() {
        val editor = sharedPreferences.edit()

        val imglink = StoreLocallyAndReturnLink(imgBmp, UUIDString)
        val currentUserString = "${UUIDString}, ${binding.etName.text},${imglink}, ${binding.etMail.text},${binding.etPhone.text},${binding.tvDOB.text}"

        editor.putString(UUIDString, currentUserString)
        editor.apply()
    }

    private fun uploadImageToFirebaseStorage(bmpToSave: Bitmap?) {
        val baos = ByteArrayOutputStream()
        bmpToSave?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnSuccessListener { it ->
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener {
                uri = it
                saveToFirebase(uri)
            }
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirebase(uri: Uri?) {
        val hashMap = hashMapOf<Any, Any>("UUID" to UUIDString, "name" to binding.etName.text.toString(),
            "imgProfile" to uri.toString(), "mail" to binding.etMail.text.toString(),
            "phone" to binding.etPhone.text.toString().toLong(),"dob" to binding.tvDOB.text.toString())

        firestore.collection("Users").document(UUIDString).set(hashMap)
    }

}