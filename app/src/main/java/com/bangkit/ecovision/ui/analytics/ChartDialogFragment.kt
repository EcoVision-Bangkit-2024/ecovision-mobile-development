package com.bangkit.ecovision.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bangkit.ecovision.databinding.DialogChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry

class ChartDialogFragment(private val chartData: List<BarEntry>, private val materialNames: List<String>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogChartBinding.inflate(inflater, container, false)

        val barChart = binding.barChartPopup

        // Prepare the BarChart
        updateBarChart(barChart, chartData, materialNames)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // Handle the bar click and show the amount
                if (e != null && h != null) {
                    val index = e.x.toInt() // Get the index of the clicked bar
                    val materialName = materialNames.getOrNull(index)
                    val amount = chartData.getOrNull(index)?.y

                    if (materialName != null && amount != null) {
                        // Show the amount in a Toast or in a custom TextView
                        showMaterialAmount(materialName, amount)
                    }
                }
            }

            override fun onNothingSelected() {
                // Handle the case when nothing is selected
            }
        })

        return binding.root
    }

    private fun updateBarChart(barChart: BarChart, chartData: List<BarEntry>, materialNames: List<String>) {
        val chartEntries = chartData.mapIndexed { index, barEntry -> barEntry }
        val barDataSet = BarDataSet(chartEntries, "")
        val colors = chartData.map { generateRandomColor() }
        barDataSet.colors = colors

        // Set custom legend entries
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

        // Enable pinch zoom and scale
        barChart.setPinchZoom(true)
        barChart.setScaleEnabled(true)

        barChart.xAxis.setEnabled(false)
        barChart.xAxis.setDrawGridLines(false)

        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.invalidate()

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
    }

    private fun showMaterialAmount(materialName: String, amount: Float) {
        // Display a message with the material name and the amount
        // For simplicity, you can use a Toast, or you can add a custom TextView in the dialog layout
        Toast.makeText(context, "Material: $materialName, Amount: $amount", Toast.LENGTH_SHORT).show()
    }

    private fun generateRandomColor(): Int {
        val random = java.util.Random()
        val alpha = random.nextInt(156) + 100
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.argb(alpha, red, green, blue)
    }
}
