package com.bangkit.ecovision.ui.analytics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.api.ApiService
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.data.response.get.Data
import com.bangkit.ecovision.databinding.FragmentAnalyticsBinding

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var analyticsViewModel: AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = WasteRepository(ApiConfig.getApiService())
        val factory = AnalyticsViewModelFactory(repository)
        analyticsViewModel = ViewModelProvider(this, factory).get(AnalyticsViewModel::class.java)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load the base64 image from the file
        analyticsViewModel.loadBase64ImageFromFile(requireContext())

        // Observe the LiveData for base64 image string
        analyticsViewModel.base64Image.observe(viewLifecycleOwner) { base64Image ->
            val bitmap = decodeBase64ToBitmap(base64Image)
            binding.IVDay.setImageBitmap(bitmap)
        }

        sharedViewModel.selectedType.observe(viewLifecycleOwner) { selectedType ->
            Log.d("AnalyticsFragment", "Received type: $selectedType")
            analyticsViewModel.loadWasteDataByType(selectedType)
        }

        analyticsViewModel.filteredWasteData.observe(viewLifecycleOwner) { filteredData ->
            // Menampilkan data pada UI, misalnya pada TextView atau RecyclerView
            updateUI(filteredData)
        }

        // Handle click on ImageView to show PhotoView
        binding.IVDay.setOnClickListener {
            val drawable = binding.IVDay.drawable
            if (drawable != null) {
                binding.photoView.visibility = View.VISIBLE
                binding.photoView.setImageDrawable(drawable)
            }
        }

        // Handle click on PhotoView to close it
        binding.photoView.setOnClickListener {
            binding.photoView.visibility = View.GONE
        }

        return root
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun updateUI(filteredData: List<Data>) {
        // Update UI with filtered data
        val totalAmount = filteredData.sumOf { it.amount }
        binding.judul.text = "Total Waste Amount: $totalAmount"

        val materialNames = filteredData.joinToString(", ") { it.materialName }
        binding.title.text = "Materials: $materialNames"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}