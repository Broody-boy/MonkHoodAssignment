package com.example.monkhoodassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monkhoodassignment.adapters.adapterFirebase
import com.example.monkhoodassignment.databinding.FragmentFirebaseBinding
import com.example.monkhoodassignment.databinding.FragmentSharedPrefencesBinding
import com.example.monkhoodassignment.mvvm.ViewModel

class SharedPrefencesFragment : Fragment() {

    private lateinit var binding: FragmentSharedPrefencesBinding
    private lateinit var vm : ViewModel
    private lateinit var adapter : adapterFirebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedPrefencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(ViewModel::class.java)

        adapter = adapterFirebase()
        binding.rvSharedPreference.adapter = adapter
        binding.rvSharedPreference.layoutManager = LinearLayoutManager(requireContext())


        vm.getAllUsersfromSharedPreference().observe(viewLifecycleOwner, Observer {
            adapter.setUserList(it)
        })
    }
}