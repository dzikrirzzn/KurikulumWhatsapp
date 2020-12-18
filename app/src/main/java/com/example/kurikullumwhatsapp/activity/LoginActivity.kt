package com.example.kurikullumwhatsapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TokenWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Toast
import com.example.kurikullumwhatsapp.MainActivity
import com.example.kurikullumwhatsapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        //mengecek userid yang ada/aktif dan jika ada akan langsung intent ke mainactivity
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE) //menghilangkan action bar di menu login
        setContentView(R.layout.activity_login)

        setTextChangedListener(edt_email, til_email)
        setTextChangedListener(edt_password, til_password)
        progress_layout.setOnTouchListener { _, _ -> true }

        btn_login.setOnClickListener {
            onLogin()
        }
        txt_signup.setOnClickListener {
            onSignUp()
        }
    }

    private fun setTextChangedListener(edt: TextInputEditText?, til: TextInputLayout?) {
        edt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                til?.isErrorEnabled = false
            }
        })

    }

    private fun onLogin() {
        var process = true
        if (edt_email.text.isNullOrEmpty()) {
            til_email.error = "Membutuhkan Email"
            til_email.isErrorEnabled = false
            process = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Membutuhkan Password"
            til_password.isErrorEnabled = true
            process = false
        }
        if (process) {
            progress_layout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                edt_email.text.toString(),
                edt_password.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progress_layout.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Login..${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
                .addOnFailureListener { e ->
                    progress_layout.visibility = View.GONE
                    e.printStackTrace()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener ( firebaseAuthListener )
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener ( firebaseAuthListener )
    }


    private fun onSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}