package com.example.jounrnal

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this,R.layout.activity_main)

        // Initialize the progress dialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Logging in...")
            setCancelable(false)
        }

        binding.createAcctBTN.setOnClickListener(){
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.emailSignInButton.setOnClickListener (){

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (!isInternetAvailable()) {
                progressDialog.dismiss()
                showNoInternetDialog()

            }

            if (validateInput(email, password)) {
                showProgressDialog()
                loginWithEmailPassword(email, password)
            }

        }


        auth = Firebase.auth




    }


    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.email.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid email format"
            isValid = false
        } else {
            binding.email.error = null
        }

        if (password.isEmpty()) {
            binding.password.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.password.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.password.error = null
        }

        return isValid
    }

    private fun loginWithEmailPassword(email: String, password: String) {

//        Toast.makeText(this,"Logging in"+email+""+password, Toast.LENGTH_SHORT).show()

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {task->
            if(task.isSuccessful){
                hideProgressDialog()
               var journal:JournalUser = JournalUser.instance!!

                journal.userId = auth.currentUser!!.uid
//                journal.username = auth.currentUser!!.displayName!!


//                Toast.makeText(this,"Logging in sucess"+email+""+password, Toast.LENGTH_SHORT).show()
//                Log.d("Login username",""+journal.username)

                val intent = Intent(this,JournalList::class.java)
                startActivity(intent)
            }else{
                hideProgressDialog()
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

    private fun showProgressDialog() {
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }


}