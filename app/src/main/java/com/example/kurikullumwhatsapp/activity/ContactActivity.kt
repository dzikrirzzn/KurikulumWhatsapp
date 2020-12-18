package com.example.kurikullumwhatsapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurikullumwhatsapp.MainActivity
import com.example.kurikullumwhatsapp.R
import com.example.kurikullumwhatsapp.adapter.ContactsAdapter
import com.example.kurikullumwhatsapp.listener.ContactClickListener
import com.example.kurikullumwhatsapp.model.Contact
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity(), ContactClickListener {

    private val contactList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        ngambilKontak()
        setupList()
    }

    private fun ngambilKontak() {
        progress_layout_contact.visibility = View.VISIBLE
        contactList.clear()
        val newList = ArrayList<Contact>()
        val phone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )
        while (phone!!.moveToNext()) {
            val name = phone.getString(
                phone
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
            val phoneNumber = phone.getString(
                phone
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            )
            newList.add(Contact(name, phoneNumber))
        }
        contactList.addAll(newList)
        phone.close()
    }

    private fun setupList() {
        progress_layout_contact.visibility = View.GONE
        val contactsAdapter = ContactsAdapter(contactList) // memasukkan data ke dalam adapter
        contactsAdapter.setOnItemClickListener(this) // memberikan aksi ketika item kontak diklik
        rv_contacts.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onContactClicked(name: String?, phone: String?) {
        val intent = Intent() // selain hanya berpindah activity intent bisa juga berpindah activity
        intent.putExtra(MainActivity.PARAM_NAME, name) // dengan membawa data
        intent.putExtra(MainActivity.PARAM_PHONE, phone) // seperti ini
        setResult(Activity.RESULT_OK, intent)
        finish()

    }
}
