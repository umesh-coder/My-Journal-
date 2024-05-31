package com.example.jounrnal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
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
import java.util.Date

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
    lateinit var imageUri: Uri

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
                currentUserName = auth.currentUser?.displayName.toString()

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

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null){

        //saving path of image

            val filepath :StorageReference = storageReference.child("journal_images").child("my_image_"+Timestamp.now().seconds)


                //uploading image to firebase storage
            filepath.putFile(imageUri).addOnSuccessListener {
                filepath.getDownloadUrl().addOnSuccessListener {
                    var imageUri : String = it.toString()
                    var timeAdded : Timestamp = Timestamp(Date())

                    //creating object of journal
                    var journal :JournalModel = JournalModel(title,thoughts,imageUri, currentUserId ,currentUserName, timeAdded )
                    Log.d("Data", "saveJournal: $journal")

                    //adding to firestore
                        collectionReference
                        .add(journal)
                        .addOnSuccessListener {
                        binding.postProgressBar.visibility = View.INVISIBLE
                        startActivity(Intent(this, JournalList::class.java))
                        finish()
                    }.addOnFailureListener {
                        binding.postProgressBar.visibility = View.INVISIBLE
                                Log.w("not added"+it, "Error adding document" )
                    }
                }
            }.addOnFailureListener {

                binding.postProgressBar.visibility = View.INVISIBLE

            }

         }else
        {

            binding.postProgressBar.visibility = View.INVISIBLE

        }


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