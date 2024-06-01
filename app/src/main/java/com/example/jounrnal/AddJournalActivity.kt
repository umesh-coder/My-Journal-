package com.example.jounrnal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.jounrnal.databinding.ActivityAddJournalBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddJournalActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddJournalBinding

    //credentials
    var currentUserId: String = ""
    var currentUserName: String= ""

    //firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    //firestore
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference = db.collection("Journal")
    private var imageUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)


        storageReference = FirebaseStorage.getInstance().getReference()

        auth = Firebase.auth

        binding.apply {

            postProgressBar.visibility = View.INVISIBLE

            if (JournalUser.instance != null) {

//                currentUserId = JournalUser.instance!!.userId.toString()
//                currentUserName = JournalUser.instance!!.username.toString()

                currentUserId = auth.currentUser?.uid.toString()
//                currentUserName = auth.currentUser?.displayName.toString()

                postUsernameTextview.text = currentUserName
            }


            postCameraButton.setOnClickListener() {

            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, 1)
            }

            postSaveJournalButton.setOnClickListener (){

            saveJournal()

            }

        }


    }

    private fun saveJournal() {
        var title:String = binding.postTitleEt.text.toString().trim()
        var thoughts:String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if (validateInput(title, thoughts, imageUri)) {
            // Saving path of the image
            val filepath: StorageReference = storageReference.child("journal_images").child("my_image_" + Timestamp.now().seconds)

            // Uploading image to Firebase storage
            filepath.putFile(imageUri!!).addOnSuccessListener {
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val imageUri: String = uri.toString()
                    val timeAdded: Timestamp = Timestamp(Date())
                    val readableTimeAdded = formatTimestampToReadableString(timeAdded)

                    // Creating object of Journal
                    val journal = JournalModel(title, thoughts, imageUri, currentUserId, currentUserName, readableTimeAdded)
                    Log.d("Data", "saveJournal: $journal")

                    // Adding to Firestore
                    collectionReference.add(journal)
                        .addOnSuccessListener {
                            binding.postProgressBar.visibility = View.INVISIBLE
                            Toast.makeText(this,"Journal Posted Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, JournalList::class.java))
                            finish()
                        }.addOnFailureListener { e ->
                            binding.postProgressBar.visibility = View.INVISIBLE
                            Log.w("Firestore", "Error adding document", e)
                        }
                }
            }.addOnFailureListener { e ->
                binding.postProgressBar.visibility = View.INVISIBLE
                Log.e("Storage", "Error uploading image", e)
            }
        } else {
            binding.postProgressBar.visibility = View.INVISIBLE
        }


    }

    private fun validateInput(title: String, thoughts: String, imageUri: Uri?): Boolean {
        var isValid = true

        if (title.isEmpty()) {
            binding.postTitleEt.error = "Title cannot be empty"
            isValid = false
        } else {
            binding.postTitleEt.error = null
        }

        if (thoughts.isEmpty()) {
            binding.postDescriptionEt.error = "Thoughts cannot be empty"
            isValid = false
        } else {
            binding.postDescriptionEt.error = null
        }

        if (imageUri == null) {
            Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK){

            if(data != null){
                imageUri = data.data!! //getimg
                binding.postImageView.setImageURI(imageUri) //show img
            }

        }

    }

    private fun formatTimestampToReadableString(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = timestamp.toDate()
        return sdf.format(date)
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser!!


    }

    override fun onStop() {
        super.onStop()

        if (auth!= null){


        }
    }
}