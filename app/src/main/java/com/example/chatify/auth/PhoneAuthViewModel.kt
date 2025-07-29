package com.example.chatify.auth

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.chatify.home.profile.FirebaseUserProfile
import com.example.chatify.IS_LOGGED_IN
import com.example.chatify.PHONE_NO
import com.example.chatify.PreferencesManager
import com.example.chatify.USER_ID
import com.example.chatify.home.profile.UserProfile
import com.example.chatify.home.profile.convertBitmapToBase64
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class AuthState {
    data object Ideal : AuthState()
    data object Loading : AuthState()
    data class CodeSent(val verificationId: String) : AuthState()

    data object NewRegistration : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private var _authState = MutableStateFlow<AuthState>(AuthState.Ideal)
    var authState = _authState.asStateFlow()
    val currentUserId = auth.currentUser?.uid

    private val userRef = database.reference.child("users")

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun isUserLoggedIn(): Boolean {
        return preferencesManager.getBoolean(IS_LOGGED_IN)
    }

    fun updateAuthState(authState: AuthState){
        _authState.value = authState
    }

    fun isSignedIn(): Boolean {
        val userId = preferencesManager.getString(USER_ID,"")
        return userId?.isNotEmpty()==true  && !isUserLoggedIn()
    }

    init {
        if(currentUserId!=null){
            fetchUserProfile(currentUserId)
        }
    }

    fun sendOtp(phoneNo: String, context: Context) {
        val activity = context as Activity
        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneNoCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
                Log.d("Firebase auth", "Verification failed ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                resendToken = token
                _authState.value = AuthState.CodeSent(verificationId)
            }

        }

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNo)
            .setActivity(activity)
            .setTimeout(60, TimeUnit.SECONDS)
            .setCallbacks(callbacks)

        resendToken?.let {
            builder.setForceResendingToken(it)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())

    }

    fun verifyOtp(code: String) {
        val currentAuthState = _authState.value

        if (currentAuthState !is AuthState.CodeSent || currentAuthState.verificationId.isEmpty()) {
            _authState.value = AuthState.Error("Verification not started or invalid Id")
            return
        }
        _authState.value = AuthState.Loading
        val credential = PhoneAuthProvider.getCredential(currentAuthState.verificationId, code)
        signInWithPhoneNoCredential(credential)
    }

    fun signInWithPhoneNoCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val phoneNo = auth.currentUser?.phoneNumber ?: ""
                    preferencesManager.saveString(PHONE_NO,phoneNo)
                    preferencesManager.saveString(USER_ID, userId)
                    fetchUserProfile(userId)

                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign-in failed")
                }
            }
    }

    private fun fetchUserProfile(userId: String) {
        userRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val existingUser = snapshot.getValue(FirebaseUserProfile::class.java)
                    userProfile = existingUser?.toUserProfile()
                    preferencesManager.saveBoolean(IS_LOGGED_IN, true)
                    _authState.value = AuthState.Success(userProfile!!)
                }else{
                    Log.d("Auth","user profile doesnt exist")
                    _authState.value = AuthState.NewRegistration
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _authState.value = AuthState.Error("Db error: ${error.message}")
            }

        })
    }

    fun saveUserProfile(name: String, bitmap: Bitmap, status: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _authState.value = AuthState.Error("User not logged in")
            return
        }
        val encodedImage = convertBitmapToBase64(bitmap)


        val newUserProfile = FirebaseUserProfile(
            userId = userId,
            name = name,
            status = status,
            phoneNo = auth.currentUser?.phoneNumber ?: "",
            profileImage = encodedImage,
        )

        userRef.child(userId).setValue(newUserProfile)
            .addOnSuccessListener {
                userProfile = newUserProfile.toUserProfile()
                preferencesManager.saveBoolean(IS_LOGGED_IN, true)
                _authState.value = AuthState.Success(userProfile!!)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to register new user")
            }

    }

    fun signOut() {
        auth.signOut()
        preferencesManager.saveBoolean(IS_LOGGED_IN, false)
        preferencesManager.saveString(USER_ID,"")
        _authState.value = AuthState.Ideal
    }
}

