package com.example.monkhoodassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.monkhoodassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    var firstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(firstTime)
            Toast.makeText(this@MainActivity, "Hi, I am Arshdeep Singh", Toast.LENGTH_LONG).show()
        firstTime = false

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddUsers::class.java))
        }

        val navView = binding.navViewBottom
        val firebase_frag = FirebaseFragment()
        val sharedPref_frag = SharedPrefencesFragment()

        setFragment(sharedPref_frag)

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.firebase -> {

                    setFragment(firebase_frag)
                }
                R.id.sharedPreference -> {

                    setFragment(sharedPref_frag)
                }


            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_holder, fragment)
            commit()
        }
    }
}