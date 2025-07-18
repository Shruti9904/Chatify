package com.example.chatify

data class UserProfile(
    val name: String = "",
    val phoneNo: String = "",
    val userId: String = "",
    val profileImage: String? = null,
    val status: String? = null
)

data class FirebaseUserProfile(
    val userId: String? = null,
    val name: String? = null,
    val phoneNo: String? = null,
    val profileImage: String? = null,
    val status: String? = null
){
    fun toUserProfile():UserProfile{
        return UserProfile(
            userId = userId?:"",
            name = name?:"New User",
            phoneNo = phoneNo?:"",
            profileImage = profileImage,
            status = status
        )
    }
}
