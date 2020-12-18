package com.example.kurikullumwhatsapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.kurikullumwhatsapp.MainActivity
import com.example.kurikullumwhatsapp.R
import com.example.kurikullumwhatsapp.model.User
import com.example.kurikullumwhatsapp.util.DATA_USERS
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setTextChangeListener(edt_email_signup, til_email_signup)
        setTextChangeListener(edt_password, til_password)
        setTextChangeListener(edt_name, til_name)
        setTextChangeListener(edt_phone, til_phone)
        progress_layout.setOnTouchListener { v, event -> true }

        txt_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_signup.setOnClickListener {
            onSignUp()
        }
    }

    private fun setTextChangeListener(edt: EditText, til: TextInputLayout) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun onSignUp() {
        var proceed = true
        if (edt_email_signup.text.isNullOrEmpty()) {
            til_email_signup.error = "Membutuhkan Email"
            til_email_signup.isErrorEnabled = true
            proceed = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Membutuhkan Password"
            til_password.isErrorEnabled = true
            proceed = false
        }
        if (edt_name.text.isNullOrEmpty()) {
            til_name.error = "Membutuhkan Nama"
            til_name.isErrorEnabled = true
            proceed = false
        }
        if (edt_phone.text.isNullOrEmpty()) {
            til_phone.error = "Membutuhkan Nomer Telephone"
            til_phone.isErrorEnabled = true
            proceed = false
        }
        if (proceed) {
            progress_layout.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(
                edt_email_signup.text.toString(),
                edt_password.text.toString()
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful){
                    progress_layout.visibility = View.GONE
                    Toast.makeText(this,
                        "SignUp error: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (firebaseAuth.uid != null){
                    val email = edt_email_signup.text.toString()
                    val password = edt_password.text.toString()
                    val name = edt_name.text.toString()
                    val phone = edt_phone.text.toString()
                    val user = User(
                        email,
                        phone,
                        name,"",
                        "Say Hello",
                        "",
                        "")
                    firebaseDb.collection(DATA_USERS)
                        .document(firebaseAuth.uid!!).set(user)
                }
                progress_layout.visibility = View.GONE
            }
                .addOnFailureListener { it ->
                    progress_layout.visibility = View.GONE
                    it.printStackTrace()
                }
        }
    }
    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }
}
