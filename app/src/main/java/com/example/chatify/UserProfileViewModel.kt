package com.example.chatify

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

enum class ProfileUpdateType(val key:String){
    NAME("name"),
    STATUS("status"),
    PROFILE_IMAGE("profileImage"),
    PHONE("phoneNo")
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
):ViewModel() {
    private val currentUserId = auth.currentUser?.uid
    val userRef = database.reference.child("users")

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    init{
        fetchUserProfile(currentUserId)
    }

    private fun fetchUserProfile(currentUserId: String?){
        if(currentUserId==null) return
        userRef.child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val existingUser = snapshot.getValue(FirebaseUserProfile::class.java)
                    userProfile = existingUser?.toUserProfile()
                }else{
                    Log.d("Auth","user profile doesnt exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error","Db error: ${error.message}")
            }

        })
    }

    fun updateUserProfile(profileUpdateType: ProfileUpdateType,newValue:String){
        if(currentUserId==null) return
        userRef.child(currentUserId).child(profileUpdateType.key).setValue(newValue).addOnSuccessListener {
            Log.d("Update","success")
        }.addOnFailureListener {
            Log.d("Update","failure")
        }
    }


}


fun decodeBase64ToBitmap(base64Image: String): Bitmap? {
    return try {
        val decodedByte = Base64.decode(base64Image, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    } catch (e: Exception) {
        null
    }
}

//Base64 turns binary data into text format, which can be saved in Firebase as a simple string.
fun convertBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}