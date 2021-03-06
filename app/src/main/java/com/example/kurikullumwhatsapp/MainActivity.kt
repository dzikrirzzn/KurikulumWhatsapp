package com.example.kurikullumwhatsapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kurikullumwhatsapp.activity.ContactActivity
import com.example.kurikullumwhatsapp.activity.LoginActivity
import com.example.kurikullumwhatsapp.activity.ProfileActivity
import com.example.kurikullumwhatsapp.adapter.SectionPagerAdapter
import com.example.kurikullumwhatsapp.fragments.ChatsFragment
import com.example.kurikullumwhatsapp.listener.FailureCallback
import com.example.kurikullumwhatsapp.util.DATA_USERS
import com.example.kurikullumwhatsapp.util.DATA_USER_PHONE
import com.example.kurikullumwhatsapp.util.PERMISSION_REQUEST_READ_CONTACT
import com.example.kurikullumwhatsapp.util.REQUEST_NEW_CHATS
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), FailureCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var mySectionPagerAdapter: SectionPagerAdapter? = null
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val chatsFragment = ChatsFragment()

    companion object {
        const val PARAM_NAME = "name"
        const val PARAM_PHONE = "phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsFragment.setFailureCallbackListener(this)

        setSupportActionBar(toolbar)

        mySectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        container.adapter = mySectionPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(
            TabLayout.ViewPagerOnTabSelectedListener(
                container
            )
        )
        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fab.hide()
                    1 -> fab.show()
                    2 -> fab.hide()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

        fab.setOnClickListener {
            onNewChat()
        }
    }

    override fun onUserError() {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun onNewChat() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts Permission")
                    .setMessage("This App Requires Access to Your Contacts to Initiation A Concersation")
                    .setPositiveButton("Yes") { dialogInterface, which ->
                        izinMintaKontak()
                    }
                    .setNegativeButton("No") { dialogInterface, which ->

                    }
                    .show()
            } else {
                izinMintaKontak()
            }
        } else {
            startNewActivity()
        }
    }

    private fun startNewActivity() {
        startActivityForResult(Intent(this, ContactActivity::class.java), REQUEST_NEW_CHATS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_NEW_CHATS -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChat(name, phone)
                }
            }
        }
    }

    private fun checkNewChat(name: String, phone: String) {
        if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
            firebaseDb.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, phone)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        chatsFragment.newChat(it.documents[0].id)
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("User not found") // aplikasi mengirim pesan
                            .setMessage("$name does not have an account. Send them an SMS to install this app.")
                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW) // intent implicit untuk kirim pesan
                                intent.data = Uri.parse("sms:$phone") // query untuk mengirim pesan intent
                                intent.putExtra(
                                    "sms_body",
                                    "Hi I'm using this new cool WhatsAppClone app. You should install it too so we can chat there."
                                )
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "An error occured. Please try again later", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }


    private fun izinMintaKontak() { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivity()
                }
            }
        }
    }

    private fun resizeTabs() {
        val layout = (tabs.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams = layoutParams
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> onLogout()
            R.id.action_profile -> onProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun onLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}