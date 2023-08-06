package com.example.monkhoodassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monkhoodassignment.adapters.adapterFirebase
import com.example.monkhoodassignment.databinding.FragmentFirebaseBinding
import com.example.monkhoodassignment.mvvm.ViewModel

class FirebaseFragment : Fragment() {

    private lateinit var binding: FragmentFirebaseBinding
    private lateinit var vm : ViewModel
    private lateinit var adapter : adapterFirebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        adapter = adapterFirebase()
        binding.rvFirebase.adapter = adapter
        binding.rvFirebase.layoutManager = LinearLayoutManager(requireContext())


        vm.getAllUsersfromFirebase().observe(this, Observer {
            adapter.setUserList(it)
        })
    }
}