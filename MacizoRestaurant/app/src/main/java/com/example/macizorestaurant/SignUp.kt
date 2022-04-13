package com.example.macizorestaurant

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.macizorestaurant.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var number = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Create new a count...")
        progressDialog.setCanceledOnTouchOutside(false)

        //firebase
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnsingup.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        //get
        email = binding.etxgmailsignin.text.toString().trim()
        password = binding.etxpasswordsingin.text.toString().trim()

        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //format invalid
            Toast.makeText(this, "Email is Invalid", Toast.LENGTH_LONG).show()
        } else if (TextUtils.isEmpty(password)) {
            //Password is blank
            Toast.makeText(this, "Password is empy", Toast.LENGTH_LONG).show()
        } else {
            firebaseSingUp()
        }
    }

    private fun firebaseSingUp() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Acount create as $email", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Sing Upfailed ", Toast.LENGTH_LONG).show()
            }
    }

}