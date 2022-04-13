package com.example.macizorestaurant

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.macizorestaurant.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//settings progress
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Login In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //firebase
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //login user
        binding.btnlogin.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            validate()
        }
        //sing in a new acount in class sing in
        binding.newacount.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }

    private fun validate() {
        //get
        email = binding.etxgmail.text.toString().trim()
        password = binding.etxpassword.text.toString().trim()
        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //format invalid
            Toast.makeText(this, "Email Invalid", Toast.LENGTH_LONG).show()
        } else if (TextUtils.isEmpty(password)) {
            //Password is blank
            Toast.makeText(this, "Password is empy", Toast.LENGTH_LONG).show()
        } else {
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Login as $email", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

}