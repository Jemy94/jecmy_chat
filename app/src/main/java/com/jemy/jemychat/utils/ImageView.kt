package com.jemy.jemychat.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jemy.jemychat.R

fun ImageView.load(url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_avatar)
        .into(this)
}