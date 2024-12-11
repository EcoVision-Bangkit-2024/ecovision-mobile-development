package com.bangkit.ecovision.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.ecovision.MainActivity
import com.bangkit.ecovision.R
import com.bangkit.ecovision.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val materials = listOf(
        "Slipper", "Candle", "Plastic bottle", "Plastic Bag Liner",
        "General Paper Residue", "General Plastic Residue", "Glass Bottle", "Tetra Pack",
        "Aluminium Can", "Pet", "Dry Organic (Garden)", "Wet Organic (Food Waste)", "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = MaterialAdapter(materials)
        binding.materialRecyclerView.adapter = adapter

        // Menambahkan LayoutManager
        binding.materialRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchView.queryHint = "Find Waste"

        binding.kartuMasuk.setOnClickListener {
            (activity as MainActivity).allowAnalyticsAccess()

            findNavController().navigate(R.id.navigation_analytics)

            // Mengatur item BottomNavigationView ke Analytics
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.selectedItemId = R.id.navigation_analytics
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        // Menonaktifkan akses AnalyticsFragment saat kembali ke HomeFragment
        (activity as MainActivity).disallowAnalyticsAccess()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}