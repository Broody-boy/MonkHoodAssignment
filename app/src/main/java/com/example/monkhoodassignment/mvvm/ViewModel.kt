package com.example.monkhoodassignment.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkhoodassignment.modal.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    val users = MutableLiveData<List<User>>()
    val firestore = FirebaseFirestore.getInstance()
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
                        users.postValue(userList!!) //Set the value of users to the userList. This is done using postValue() instead of directly setting the value to ensure it happens on the main (UI) thread, as LiveData is usually used for UI observation.
                    }
            } catch (e: Exception) {
                // handle exception
            }
        }
        return users
    }

    fun removeUser(UUIDtoDelete : String){

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
}