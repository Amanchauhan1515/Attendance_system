package com.example.attendancesystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.attendancesystem.Models.subject_dataview
import com.example.attendancesystem.R

class MyAdapter(private val userList: ArrayList<subject_dataview>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item, parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.firstName.text = currentItem.userName
        holder.lastName.text = currentItem.Roll
        holder.timestamp.text = currentItem.timestamp

        Glide.with(holder.profileImage.context)
            .load(currentItem.profileImageUrl)
            .placeholder(R.drawable.img_7) // you should add this drawable
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.tvfirstName)
        val lastName: TextView = itemView.findViewById(R.id.tvrollNo)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImageView)
        val timestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }
}
