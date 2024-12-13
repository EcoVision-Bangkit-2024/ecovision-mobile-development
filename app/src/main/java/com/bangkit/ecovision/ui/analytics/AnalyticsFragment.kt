package com.bangkit.ecovision.ui.analytics

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.databinding.FragmentAnalyticsBinding
import com.bangkit.ecovision.ui.LoadingDialogFragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

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
        analyticsViewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        analyticsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        }

        sharedViewModel.selectedType.observe(viewLifecycleOwner) { selectedType ->
            binding.title.text = selectedType
        }

        analyticsViewModel.loadBase64ImageFromFile(requireContext())

        val type = sharedViewModel.selectedType.value
        analyticsViewModel.loadBase64TypeImageFromFile(requireContext(), type)

        analyticsViewModel.base64Image.observe(viewLifecycleOwner) { base64Image ->
            val bitmap = decodeBase64ToBitmap(base64Image)
            binding.IVDay.setImageBitmap(bitmap)
        }

        analyticsViewModel.base64TypeImage.observe(viewLifecycleOwner) { base64AnorganicImage ->
            val bitmap = decodeBase64ToBitmap(base64AnorganicImage)
            binding.IVType.setImageBitmap(bitmap)
        }

        analyticsViewModel.chartDataMasuk.observe(viewLifecycleOwner) { chartData ->
            analyticsViewModel.materialNamesMasuk.observe(viewLifecycleOwner) { materialNames ->
                updateBarChart(binding.chartMasuk, chartData, materialNames)
            }
        }

        analyticsViewModel.chartDataKeluar.observe(viewLifecycleOwner) { chartData ->
            analyticsViewModel.materialNamesKeluar.observe(viewLifecycleOwner) { materialNames ->
                updateBarChart(binding.chartKeluar, chartData, materialNames)
            }
        }

        analyticsViewModel.sumMasuk.observe(viewLifecycleOwner) { sumMasuk ->
            binding.materialIn.text = sumMasuk.toString()
        }

        analyticsViewModel.sumKeluar.observe(viewLifecycleOwner) { sumKeluar ->
            binding.materialOut.text = sumKeluar.toString()
        }

        analyticsViewModel.loadWasteData(sharedViewModel.selectedType.value)

        binding.IVDay.setOnClickListener {
            val drawable = binding.IVDay.drawable
            if (drawable != null) {
                binding.photoView.visibility = View.VISIBLE
                binding.photoView.setImageDrawable(drawable)
                binding.photoView.setScaleType(ImageView.ScaleType.FIT_CENTER)
                disableScrollView(true)
            }
        }

        binding.IVType.setOnClickListener {
            val drawable = binding.IVType.drawable
            if (drawable != null) {
                binding.photoView.visibility = View.VISIBLE
                binding.photoView.setImageDrawable(drawable)
                binding.photoView.setScaleType(ImageView.ScaleType.FIT_CENTER)
                disableScrollView(true)
            }
        }

        binding.photoView.setOnClickListener {
            binding.photoView.visibility = View.GONE
            disableScrollView(false)
        }

        binding.chartMasuk.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null && h != null) {
                    val index = e.x.toInt()
                    val materialName = analyticsViewModel.materialNamesMasuk.value?.getOrNull(index)
                    val amount = analyticsViewModel.chartDataMasuk.value?.getOrNull(index)?.y

                    if (materialName != null && amount != null) {
                        showMaterialAmount(materialName, amount)
                    }
                }
            }

            override fun onNothingSelected() {}
        })

        binding.chartKeluar.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null && h != null) {
                    val index = e.x.toInt()
                    val materialName = analyticsViewModel.materialNamesKeluar.value?.getOrNull(index)
                    val amount = analyticsViewModel.chartDataKeluar.value?.getOrNull(index)?.y

                    if (materialName != null && amount != null) {
                        showMaterialAmount(materialName, amount)
                    }
                }
            }

            override fun onNothingSelected() {}
        })

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

    @SuppressLint("ClickableViewAccessibility")
    private fun disableScrollView(disable: Boolean) {
        val scrollView = binding.root
        if (disable) {
            scrollView.setOnTouchListener { _, _ -> true }
        } else {
            scrollView.setOnTouchListener(null)
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
        val chartEntries = chartData.mapIndexed { _, barEntry -> barEntry }

        val barDataSet = BarDataSet(chartEntries, "")

        val colors = chartData.map { generateRandomColor() }
        barDataSet.colors = colors

        val legendEntries = materialNames.mapIndexed { index, materialName ->
            com.github.mikephil.charting.components.LegendEntry(
                materialName,
                com.github.mikephil.charting.components.Legend.LegendForm.SQUARE,
                10f,
                10f,
                null,
                colors[index]
            )
        }

        barChart.legend.setCustom(legendEntries)

        val barData = BarData(barDataSet)
        barChart.data = barData

        barChart.xAxis.isEnabled = false
        barChart.xAxis.setDrawGridLines(false)

        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.invalidate()

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
    }

    private fun generateRandomColor(): Int {
        val random = java.util.Random()
        val alpha = random.nextInt(156) + 100
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.argb(alpha, red, green, blue)
    }

    private fun showMaterialAmount(materialName: String, amount: Float) {
        Toast.makeText(context, "Material: $materialName, Amount: $amount", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}