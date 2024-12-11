package com.bangkit.ecovision

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.bangkit.ecovision.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Flag untuk mengontrol akses ke AnalyticsFragment
    private var allowAnalyticsAccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home, null, navOptions {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    })
                    true
                }
                R.id.navigation_add -> {
                    navController.navigate(R.id.navigation_add, null, navOptions {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    })
                    true
                }
                R.id.navigation_analytics -> {
                    // Hanya navigasi jika akses diizinkan
                    if (allowAnalyticsAccess) {
                        navController.navigate(R.id.navigation_analytics, null, navOptions {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        })
                        true
                    } else {
                        // Blok akses langsung
                        false
                    }
                }
                else -> false
            }
        }
    }

    // Fungsi untuk mengizinkan akses ke AnalyticsFragment
    fun allowAnalyticsAccess() {
        allowAnalyticsAccess = true
    }
    fun disallowAnalyticsAccess() {
        allowAnalyticsAccess = false
    }

}