package com.jemy.jemychat.ui.main.main.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jemy.jemychat.R
import com.jemy.jemychat.model.User
import com.jemy.jemychat.ui.main.main.contacts.adapter.ContactAdapter
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private var usersList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_contacts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadContacts()

    }

    private fun loadContacts() {
        contactsProgressBar.visibility = View.VISIBLE
        databaseReference.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { usersList.add(it) }
                }
                val adapter = ContactAdapter(usersList)
                adapter.notifyDataSetChanged()
                contactsRecycler.adapter = adapter
                contactsProgressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity!!, error.message, Toast.LENGTH_LONG).show()
                contactsProgressBar.visibility = View.INVISIBLE
            }
        })
    }
}
