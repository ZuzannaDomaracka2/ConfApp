package com.example.confapp

import android.content.ClipData
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.user_row.view.*
import kotlin.coroutines.coroutineContext

class UserDisplay(val user:Users): Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {




        viewHolder.itemView.each_user_name.text = user.name

        Picasso.get().load(user.urlPhoto).into(viewHolder.itemView.each_user_photo)


        if (user.status == "online"){
            viewHolder.itemView.image_online.visibility=View.VISIBLE
           viewHolder.itemView.image_offline.visibility=View.GONE
        }
        else
        {
            viewHolder.itemView.image_online.visibility=View.GONE
            viewHolder.itemView.image_offline.visibility=View.VISIBLE
        }

    }


    override fun getLayout():Int {
        return R.layout.user_row

    }
}