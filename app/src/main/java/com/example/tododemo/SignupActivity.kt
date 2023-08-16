package com.example.tododemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tododemo.databinding.ActivitySignupBinding

class signupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this, null)

        binding.btnSignup.setOnClickListener {
            val signupMail = binding.signupMail.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            signupDatabase(signupMail, signupPassword, firstName, lastName)
        }

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupDatabase(username: String, password: String, firstName: String, lastName: String) {
        if (!isValidEmail(username)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return
        }

        val insertedRowId = databaseHelper.setUser(username, password, firstName, lastName)
        if (insertedRowId != -1L) {
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[A-Za-z0-9.]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,4}".toRegex()
        return email.matches(emailRegex)
    }

    override fun onBackPressed() {
        val intent = Intent(this, loginActivity::class.java)
        startActivity(intent)
        finish()
    }
}