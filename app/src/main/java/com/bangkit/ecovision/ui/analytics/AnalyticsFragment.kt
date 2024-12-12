package com.bangkit.ecovision.ui.analytics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.databinding.FragmentAnalyticsBinding
import com.bangkit.ecovision.ui.LoadingDialogFragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var analyticsViewModel: AnalyticsViewModel
    private var loadingDialog: LoadingDialogFragment? = null

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

        analyticsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        }

        // Di dalam metode onCreateView() pada AnalyticsFragment
        sharedViewModel.selectedType.observe(viewLifecycleOwner) { selectedType ->
            // Mengubah teks pada TextView sesuai dengan nilai selectedType
            binding.title.text = selectedType
        }


        // Load the base64 image from the file
        analyticsViewModel.loadBase64ImageFromFile(requireContext())

        analyticsViewModel.loadBase64AnorganicImageFromFile(requireContext())

        // Observe the LiveData for base64 image string
        analyticsViewModel.base64Image.observe(viewLifecycleOwner) { base64Image ->
            val bitmap = decodeBase64ToBitmap(base64Image)
            binding.IVDay.setImageBitmap(bitmap)
        }

        analyticsViewModel.base64AnorganicImage.observe(viewLifecycleOwner) { base64AnorganicImage ->
            val bitmap = decodeBase64ToBitmap(base64AnorganicImage)
            binding.IVAnorganic.setImageBitmap(bitmap)  // Assuming IVAnorganic is the ImageView for the anorganic image
        }

        // Observe data for "Masuk" chart
        analyticsViewModel.chartDataMasuk.observe(viewLifecycleOwner) { chartData ->
            analyticsViewModel.materialNamesMasuk.observe(viewLifecycleOwner) { materialNames ->
                updateBarChart(binding.chartMasuk, chartData, materialNames)
            }
        }

        // Observe data for "Keluar" chart
        analyticsViewModel.chartDataKeluar.observe(viewLifecycleOwner) { chartData ->
            analyticsViewModel.materialNamesKeluar.observe(viewLifecycleOwner) { materialNames ->
                updateBarChart(binding.chartKeluar, chartData, materialNames)
            }
        }

        analyticsViewModel.sumMasuk.observe(viewLifecycleOwner) { sumMasuk ->
            // Update the TextView with the sum
            binding.materialIn.text = sumMasuk.toString()
        }

        analyticsViewModel.sumKeluar.observe(viewLifecycleOwner) { sumKeluar ->
            binding.materialOut.text = sumKeluar.toString()  // Update material_out TextView
        }

        // Load data
        analyticsViewModel.loadWasteData(sharedViewModel.selectedType.value)

        // Handle click on ImageView to show PhotoView
        binding.IVDay.setOnClickListener {
            val drawable = binding.IVDay.drawable
            if (drawable != null) {
                // Show the PhotoView and disable ScrollView scroll
                binding.photoView.visibility = View.VISIBLE
                binding.photoView.setImageDrawable(drawable)
                binding.photoView.setScaleType(ImageView.ScaleType.FIT_CENTER)
                disableScrollView(true)
            }
        }

        binding.IVAnorganic.setOnClickListener {
            val drawable = binding.IVAnorganic.drawable
            if (drawable != null) {
                // Show the PhotoView and disable ScrollView scroll
                binding.photoView.visibility = View.VISIBLE
                binding.photoView.setImageDrawable(drawable)
                binding.photoView.setScaleType(ImageView.ScaleType.FIT_CENTER)
                disableScrollView(true)
            }
        }

        binding.photoView.setOnClickListener {
            // Hide PhotoView and enable ScrollView scroll
            binding.photoView.visibility = View.GONE
            disableScrollView(false)
        }

        // Menangani klik pada chartMasuk
        binding.chartMasuk.setOnClickListener {
            // Mengambil data chart yang relevan
            val chartData = analyticsViewModel.chartDataMasuk.value ?: emptyList()
            val materialNames = analyticsViewModel.materialNamesMasuk.value ?: emptyList()
            val chartDialog = ChartDialogFragment(chartData, materialNames)
            chartDialog.show(childFragmentManager, "chartMasukDialog")
        }

        // Menangani klik pada chartKeluar
        binding.chartKeluar.setOnClickListener {
            // Mengambil data chart yang relevan
            val chartData = analyticsViewModel.chartDataKeluar.value ?: emptyList()
            val materialNames = analyticsViewModel.materialNamesKeluar.value ?: emptyList()
            val chartDialog = ChartDialogFragment(chartData, materialNames)
            chartDialog.show(childFragmentManager, "chartKeluarDialog")
        }

        return root
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
        loadingDialog?.show(childFragmentManager, "loadingDialog")
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun disableScrollView(disable: Boolean) {
        val scrollView = binding.root as ScrollView
        if (disable) {
            // Disable ScrollView scroll by intercepting touch events
            scrollView.setOnTouchListener { _, _ -> true } // Disables touch interaction
        } else {
            // Re-enable ScrollView touch interaction
            scrollView.setOnTouchListener(null) // Removes the touch listener
        }
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

    private fun updateBarChart(barChart: com.github.mikephil.charting.charts.BarChart, chartData: List<BarEntry>, materialNames: List<String>) {
        // Create BarDataSet from the chartData
        val chartEntries = chartData.mapIndexed { index, barEntry -> barEntry }

        // Create BarDataSet for the chart
        val barDataSet = BarDataSet(chartEntries, "")

        // Assign random colors to the bars
        val colors = chartData.map { generateRandomColor() }
        barDataSet.colors = colors // Set colors for the bars

        // Set legend for each color with the corresponding material name
        val legendLabels = materialNames
        val legendEntries = legendLabels.mapIndexed { index, materialName ->
            // Create LegendEntry for each color and corresponding material name
            com.github.mikephil.charting.components.LegendEntry(
                materialName,
                com.github.mikephil.charting.components.Legend.LegendForm.SQUARE,
                10f,
                10f,
                null,
                colors[index]
            )
        }

        // Set the custom legend
        barChart.legend.setCustom(legendEntries)

        // Set the data and refresh the chart
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Disable X-Axis and remove grid lines
        barChart.xAxis.setEnabled(false)
        barChart.xAxis.setDrawGridLines(false)

        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.invalidate()

        // Set chart axis properties
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
    }


    // Function to generate a random color
    private fun generateRandomColor(): Int {
        val random = java.util.Random()
        val alpha = random.nextInt(156) + 100 // Alpha (0 to 255)
        val red = random.nextInt(256)   // Red (0 to 255)
        val green = random.nextInt(256) // Green (0 to 255)
        val blue = random.nextInt(256)  // Blue (0 to 255)
        return Color.argb(alpha, red, green, blue) // Generate random color with alpha, red, green, blue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}