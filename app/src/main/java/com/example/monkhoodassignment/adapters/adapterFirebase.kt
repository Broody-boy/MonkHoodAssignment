package com.example.monkhoodassignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monkhoodassignment.databinding.ItemBinding
import com.example.monkhoodassignment.modal.User

class adapterFirebase : RecyclerView.Adapter<UserViewHolder>() {
    var alluserslist = listOf<User>()
    lateinit var binding : ItemBinding
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
    }

    fun setUserList(list: List<User>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(alluserslist, list))
        alluserslist = list
        diffResult.dispatchUpdatesTo(this)
    }
}

class UserViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val image: ImageView = binding.imgProf
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