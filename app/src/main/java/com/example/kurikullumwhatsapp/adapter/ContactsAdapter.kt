package com.example.kurikullumwhatsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikullumwhatsapp.R
import com.example.kurikullumwhatsapp.activity.ContactActivity
import com.example.kurikullumwhatsapp.listener.ContactClickListener
import com.example.kurikullumwhatsapp.model.Contact
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_contact.*

class ContactsAdapter(private val contacts: ArrayList<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private var clickListener: ContactClickListener? = null

    override fun onCreateViewHolder(view: ViewGroup, viewType: Int)=
        ContactViewHolder(LayoutInflater.from(view.context).inflate(R.layout.item_contact, view, false))

    override fun getItemCount(): Int = contacts.size

    
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bindItem(contacts[position], clickListener)
    }

    class ContactViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindItem(contact: Contact, listener: ContactClickListener?) {
            txt_contact_name.text = contact.name
            txt_contact_number.text = contact.phone
            itemView.setOnClickListener { // memberikan aksi ketika item kontak diklik
                listener?.onContactClicked(contact.name, contact.phone)
            }
        }
    }

    fun setOnItemClickListener(listener: ContactActivity) {
        clickListener = listener
        notifyDataSetChanged()
    }
}