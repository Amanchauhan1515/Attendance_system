package com.example.attendancesystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancesystem.LowTotalAttendanceActivity.UserWithPercentage
import com.example.attendancesystem.R

class UserAdapter(private val userList: ArrayList<UserWithPercentage>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvUserName)
        val roll: TextView = itemView.findViewById(R.id.tvUserRoll)
        val email: TextView = itemView.findViewById(R.id.tvUserEmail)
        val percent: TextView = itemView.findViewById(R.id.tvUserPercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = userList[position]
        holder.name.text = current.user.username
        holder.roll.text = "Roll: ${current.user.rollNo}"
        holder.email.text = current.user.email
        holder.percent.text = "Attendance: ${current.percent}%"
    }

    override fun getItemCount(): Int = userList.size
}
