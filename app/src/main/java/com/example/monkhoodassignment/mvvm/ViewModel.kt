package com.example.monkhoodassignment.mvvm

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyApplication
import com.example.monkhoodassignment.modal.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    val usersfb = MutableLiveData<List<User>>()
    val userssp = MutableLiveData<List<User>>()

    val firestore = FirebaseFirestore.getInstance()
    val sharedPreferences = MyApplication.getAppContext().getSharedPreferences("UsersCollection", Context.MODE_PRIVATE)

    fun getAllUsersfromFirebase() : LiveData<List<User>> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Users")
                    .addSnapshotListener {snapshot, exception ->
                        if (exception!=null) {
                            // Handle the exception here
                            return@addSnapshotListener
                        }
                        val userList = snapshot?.documents?.mapNotNull {    //mapNotNull is used to convert the list of Firestore documents into a list of Users objects.
                            it.toObject(User::class.java)                                  //Here, mapNotNull is a higher-order function applied to the list of documents.
                        }                                                                   //For each document in the list, the lambda function inside mapNotNull is executed.
                        usersfb.postValue(userList!!) //Set the value of users to the userList. This is done using postValue() instead of directly setting the value to ensure it happens on the main (UI) thread, as LiveData is usually used for UI observation.
                    }
            } catch (e: Exception) {
                // handle exception
            }
        }
        return usersfb
    }

    fun removeUserfromFirebase(UUIDtoDelete : String){

        val collectionRef = firestore.collection("Users")
        val documentRef = collectionRef.document(UUIDtoDelete)    // Get a reference to the specific document

        documentRef.delete()
            .addOnSuccessListener {
                // Document deletion successful
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during deletion
            }
    }

    fun getAllUsersfromSharedPreference() : LiveData<List<User>> {
        val userList = mutableListOf<User>()

        val allEntries = sharedPreferences.all

        for ((key, value) in allEntries) {
            val userDataArray = (value as String).split(",")
            if (userDataArray.size == 6) {
                val UUID = userDataArray[0]
                val name = userDataArray[1]
                val profileImage = (userDataArray[2])
                val email = userDataArray[3]
                val phone = userDataArray[4]
                val dob = userDataArray[5]
                val user = User(UUID, name, profileImage, email, phone.toInt(), dob)
                userList.add(user)
            }
        }

        userssp.postValue(userList!!)
        return userssp
    }
}