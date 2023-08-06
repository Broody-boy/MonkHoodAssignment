package com.example.monkhoodassignment.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monkhoodassignment.ModifyUser
import com.example.monkhoodassignment.databinding.ItemBinding
import com.example.monkhoodassignment.modal.User
import com.example.monkhoodassignment.mvvm.ViewModel

class adapterFirebase : RecyclerView.Adapter<UserViewHolder>() {
    var alluserslist = listOf<User>()
    lateinit var binding : ItemBinding
    val vm = ViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alluserslist.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentuser = alluserslist[position]
        Glide.with(holder.itemView.context).load(currentuser.imgProfile).into(holder.image)

        holder.ItemName.text = currentuser.name
        holder.ItemMail.text = currentuser.mail
        holder.ItemPhone.text = currentuser.phone.toString()
        holder.ItemDOB.text = currentuser.dob

        holder.imgEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context,ModifyUser::class.java)
            intent.putExtra("UUID",currentuser.UUID)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgDel.setOnClickListener {
            vm.removeUserfromFirebase(currentuser.UUID!!)
            vm.removeUserfromSharedPreference(currentuser.UUID!!)
        }
    }

    fun setUserList(list: List<User>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(alluserslist, list))
        alluserslist = list
        diffResult.dispatchUpdatesTo(this)
    }
}

class UserViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val image: ImageView = binding.imgProf
    val imgEdit: ImageView = binding.imgEdit
    val imgDel: ImageView = binding.imgDelete

    val ItemName: TextView = binding.tvName
    val ItemMail: TextView = binding.tvMail
    val ItemPhone: TextView = binding.tvPhone
    val ItemDOB: TextView = binding.tvDOB
}

class MyDiffCallback(
    private val oldList : List<User>,
    private val newList : List<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}