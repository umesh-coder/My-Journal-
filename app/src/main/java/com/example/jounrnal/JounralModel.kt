package com.example.jounrnal

import com.google.firebase.Timestamp


data class JournalModel (
    val title: String,
    val thoughts: String,
    val imageUrl : String ,
    val userId :String ,
    val username : String ,
    val timeAdded: String
)