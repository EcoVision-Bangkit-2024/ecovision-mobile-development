package com.bangkit.ecovision.ui.add

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.databinding.FragmentAddBinding
import java.util.Calendar

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    // Register the ActivityResultContracts.GetContent() for image selection
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val uploadText = binding.uploadText
                val selectedImageView = binding.selectedImageView

                // Hide the upload text and display the selected image
                uploadText.visibility = View.GONE
                selectedImageView.setImageURI(uri) // Display the selected image
                selectedImageView.visibility = View.VISIBLE // Show the ImageView
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addViewModel =
            ViewModelProvider(this).get(AddViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupDatePicker()
        setupMaterialDropdown()
        setupTypeDropdown()
        setupImageUpload()

        return root
    }

    private fun setupDatePicker() {
        val dateInput = binding.dateInput // Make sure the ID in XML is correct

        // Disable manual input so users can't type directly
        dateInput.isFocusable = false
        dateInput.isClickable = true

        // Show DatePickerDialog when EditText is clicked
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format date and set it to EditText
                    val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    dateInput.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    private fun setupMaterialDropdown() {
        // Data for the material dropdown
        val materials = listOf("Plastic", "Paper", "Metal", "Glass", "Organic")

        // Adapter for AutoCompleteTextView
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            materials
        )

        // Set adapter to AutoCompleteTextView
        binding.materialName.setAdapter(adapter)

        // Show dropdown when the user clicks the field
        binding.materialName.setOnClickListener {
            binding.materialName.showDropDown()
        }

        // Listener to handle selected item
        binding.materialName.setOnItemClickListener { _, _, position, _ ->
            val selectedMaterial = materials[position]
            // Do something with the selection
        }
    }

    private fun setupTypeDropdown() {
        // Data for the type dropdown
        val types = listOf("Recyclable", "Non-Recyclable", "Hazardous", "Compostable")

        // Adapter for AutoCompleteTextView Type
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            types
        )

        // Set adapter to AutoCompleteTextView
        binding.type.setAdapter(adapter)

        // Show dropdown when the user clicks the field
        binding.type.setOnClickListener {
            binding.type.showDropDown()
        }

        // Listener to handle selected item
        binding.type.setOnItemClickListener { _, _, position, _ ->
            val selectedType = types[position]
            // Do something with the selection
        }
    }

    private fun setupImageUpload() {
        val imageUploadBox = binding.imageUploadBox

        // Open gallery when the user clicks on the image upload box
        imageUploadBox.setOnClickListener {
            // Launch the image picker using ActivityResultContracts
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}