package com.example.jounrnal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.jounrnal.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.createAcctBTN.setOnClickListener(){
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.emailSignInButton.setOnClickListener (){

            loginWithEmailPassword(
                binding.email.text.toString().trim() , binding.password.text.toString().trim()
            )


        }

        auth = Firebase.auth


    }

    private fun loginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){

               var journal:JournalUser = JournalUser.instance!!

                journal.userId = auth.currentUser!!.uid
                journal.username = auth.currentUser!!.displayName!!

                val intent = Intent(this,JournalList::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,JournalList::class.java)
            startActivity(intent)
        }
    }


}