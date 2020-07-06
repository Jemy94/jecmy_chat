package com.jemy.jemychat.ui.main.main.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.jemy.jemychat.R
import com.jemy.jemychat.model.User
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var imageUri: Uri
    private var imageUrl = ""
    private val firebaseReference by lazy { FirebaseDatabase.getInstance() }
    private val storageReference by lazy {
        FirebaseStorage.getInstance().getReference("profile_images")
    }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPickImageClickListener()
        getUserProfile()
        setupSaveButtonClickListener()
    }

    private fun setupPickImageClickListener() {
        pickImageButton.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                intent,
                PICK_IMAGE_REQUEST
            )
        })
    }

    private fun getUserProfile() {
        profileProgressBar.visibility = View.VISIBLE
        val currentUseId = auth.currentUser?.uid.toString()
        firebaseReference.getReference("Users").child(currentUseId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    profileNameEditText.setText(user?.name.toString())
                    statusEditText.setText(user?.status.toString())
                    imageUrl = user?.image.toString()
                    if (!imageUrl.isBlank() && imageUrl != "default") {
                        Glide.with(activity!!).load(imageUrl).into(userImage)
                    }
                    profileProgressBar.visibility = View.INVISIBLE
                }

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(activity!!, p0.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setupSaveButtonClickListener() {
        saveButton.setOnClickListener {
            profileProgressBar.visibility = View.VISIBLE
            val name = profileNameEditText.text.toString()
            val status = statusEditText.text.toString()
            if (name.isBlank() || status.isBlank()) {
                Toast.makeText(activity!!, "Please fill the messing data", Toast.LENGTH_LONG).show()
                profileProgressBar.visibility = View.INVISIBLE
            } else {
                val user = User(name, status, imageUrl)
                firebaseReference.reference.child("Users").child(auth.currentUser?.uid.toString())
                    .setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            profileProgressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                activity!!,
                                "Profile updated successfully",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(activity!!, error.message, Toast.LENGTH_LONG).show()
                        profileProgressBar.visibility = View.INVISIBLE
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            profileProgressBar.visibility = View.VISIBLE
            imageUri = data.data!!
            Glide.with(activity!!).load(imageUri).into(userImage)
            val storageRef = storageReference.child("${auth.currentUser!!.uid}.jpg")
            storageRef.putFile(imageUri).addOnSuccessListener { _ ->
                storageReference.child("${auth.currentUser!!.uid}.jpg").downloadUrl
                    .addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        Log.d("Profile url:", imageUrl)
                    }
                Toast.makeText(
                    activity!!,
                    "Image uploaded successfully",
                    Toast.LENGTH_LONG
                ).show()

                profileProgressBar.visibility = View.INVISIBLE
            }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        activity!!,
                        error.message,
                        Toast.LENGTH_LONG
                    ).show()
                    profileProgressBar.visibility = View.INVISIBLE
                }
        }
    }
}
