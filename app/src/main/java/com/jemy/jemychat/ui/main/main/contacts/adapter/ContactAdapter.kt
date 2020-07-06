package com.jemy.jemychat.ui.main.main.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jemy.jemychat.R
import com.jemy.jemychat.model.User
import com.jemy.jemychat.utils.load
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(private val list: List<User>) : RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = list[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = list.size
}

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var contactName = itemView.contactName
    private var contactStatus = itemView.contactStatus
    private var contactImage = itemView.contactImage

    fun bind(user: User) {
        contactName.text = user.name
        contactStatus.text = user.status
        user.image?.let { contactImage.load(it) }
    }
}