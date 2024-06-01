package com.example.jounrnal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.jounrnal.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        auth = Firebase.auth

        binding.accSignUpButton.setOnClickListener(){

            createUser()




        }
    }

    private fun createUser() {
        val email = binding.emailCreate.text.toString()
        val password = binding.passwordCreate.text.toString()
        val username= binding.userNameCreateET.text.toString()


        if (validateInput(username,email, password)) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("User Created", "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)

                        val intent = Intent(this,JournalList::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("User Not Created", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Already Have An Account",
                            Toast.LENGTH_SHORT,
                        ).show()
                        updateUI(null)
                    }
                }.addOnFailureListener(this) {

                    Toast.makeText(this,"Account Not Created",Toast.LENGTH_SHORT).show()

                }

        }

    }

    private fun validateInput(username: String,email: String, password: String): Boolean {
        var isValid = true


        if (username.isEmpty()) {
            binding.userNameCreateET.error = "Username cannot be empty"
            isValid = false
        } else {
            binding.userNameCreateET.error = null
        }

        if (email.isEmpty()) {
            binding.emailCreate.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailCreate.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailCreate.error = null
        }

        if (password.isEmpty()) {
            binding.passwordCreate.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordCreate.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordCreate.error = null
        }

        return isValid
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun reload() {
        TODO("Not yet implemented")
    }
}