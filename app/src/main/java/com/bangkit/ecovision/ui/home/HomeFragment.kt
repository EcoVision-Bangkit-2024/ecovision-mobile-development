package com.bangkit.ecovision.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.ecovision.MainActivity
import com.bangkit.ecovision.R
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.response.get.Data
import com.bangkit.ecovision.data.response.get.WasteResponse
import com.bangkit.ecovision.databinding.FragmentHomeBinding
import com.bangkit.ecovision.ui.analytics.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchMaterialsFromApi()

        binding.kartuMasuk.setOnClickListener {
            (activity as MainActivity).allowAnalyticsAccess()

            findNavController().navigate(R.id.navigation_analytics)

            // Mengatur item BottomNavigationView ke Analytics
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.selectedItemId = R.id.navigation_analytics
        }

        // Pada HomeFragment.kt
        binding.nonAnorganic.setOnClickListener {
            (activity as MainActivity).allowAnalyticsAccess()
            // Mengirimkan data ke SharedViewModel
            sharedViewModel.setSelectedType("Non Organic")

            // Navigasi ke AnalyticsFragment
            findNavController().navigate(R.id.navigation_analytics)

            // Mengatur item BottomNavigationView ke Analytics
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.selectedItemId = R.id.navigation_analytics
        }



        return root
    }

    private fun fetchMaterialsFromApi() {
        val apiService = ApiConfig.getApiService()

        apiService.getWastes().enqueue(object : Callback<WasteResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<WasteResponse>, response: Response<WasteResponse>) {
                if (response.isSuccessful) {
                    val materials = response.body()?.data ?: emptyList()
                    setupRecyclerView(materials)
                } else {
                    // Handle error
                    binding.materialRecyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<WasteResponse>, t: Throwable) {
                // Handle failure
                binding.materialRecyclerView.visibility = View.GONE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupRecyclerView(materials: List<Data>) {
        // Memproses data untuk menghilangkan duplikasi dan menjumlahkan amount
        val processedMaterials = processMaterials(materials)

        // Mengatur adapter dengan data yang telah diproses
        val adapter = MaterialAdapter(processedMaterials)
        binding.materialRecyclerView.adapter = adapter
        binding.materialRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processMaterials(materials: List<Data>): List<Data> {
        val materialMap = mutableMapOf<String, Int>()

        // Kelompokkan berdasarkan materialName dan jumlahkan amount
        for (material in materials) {
            val name = material.materialName
            val amount = material.amount
            materialMap[name] = materialMap.getOrDefault(name, 0) + amount
        }

        // Konversi hasil map ke dalam bentuk daftar Data
        return materialMap.map { (name, amount) ->
            Data(
                materialName = name,
                amount = amount,
                id = 0, // ID tidak relevan di sini
                date = "", // Tanggal tidak diperlukan
                keterangan = "", // Keterangan tidak diperlukan
                purpose = "", // Tujuan tidak diperlukan
                photoUrl = "", // Foto tidak diperlukan
                type = "" // Tipe tidak diperlukan
            )
        }
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