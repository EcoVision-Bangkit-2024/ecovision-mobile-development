package com.bangkit.ecovision.ui.add

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.MainActivity
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.databinding.FragmentAddBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var addViewModel: AddViewModel
    private var currentImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImageUri = it
                val uploadText = binding.uploadText
                val selectedImageView = binding.selectedImageView

                uploadText.visibility = View.GONE
                selectedImageView.setImageURI(uri)
                selectedImageView.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = WasteRepository(ApiConfig.getApiService())
        val factory = AddViewModelFactory(repository)
        addViewModel = ViewModelProvider(this, factory).get(AddViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupStatusDropdown()
        setupDatePicker()
        setupMaterialDropdown()
        setupTypeDropdown()
        setupImageUpload()

        binding.submit.setOnClickListener {
            onSubmitButtonClicked()
        }

        addViewModel.submitStatus.observe(viewLifecycleOwner) { status ->
            val message = status.second
            if (status.first) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                resetForm()
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        // Menonaktifkan akses AnalyticsFragment saat kembali ke HomeFragment
        (activity as MainActivity).disallowAnalyticsAccess()
    }


    private fun resetForm() {
        binding.statusInput.setText("")
        binding.dateInput.setText("")
        binding.materialName.setText("")
        binding.type.setText("")
        binding.amount.setText("")

        currentImageUri = null
        binding.uploadText.visibility = View.VISIBLE
        binding.selectedImageView.visibility = View.GONE
    }


    private fun setupStatusDropdown() {
        val statuses = listOf("Masuk", "Keluar")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statuses
        )

        binding.statusInput.setAdapter(adapter)

        binding.statusInput.setOnClickListener {
            binding.statusInput.showDropDown()
        }

        binding.statusInput.setOnItemClickListener { _, _, position, _ ->
            val selectedStatus = statuses[position]

            println("Status yang dipilih: $selectedStatus")
        }
    }


    private fun setupDatePicker() {
        val dateInput = binding.dateInput

        dateInput.isFocusable = false
        dateInput.isClickable = true

        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the date to YY-MM-DD
                    val formattedDate = String.format(
                        "%02d-%02d-%02d", // Format as "YY-MM-DD"
                        selectedYear % 100, // Get last two digits of the year
                        selectedMonth + 1,   // Month is 0-based, so we add 1
                        selectedDay
                    )
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
        val materials = listOf("Slipper", "Candle", "Plastic bottle", "Plastic Bag Liner",
            "General Paper Residue", "General Plastic Residue", "Glass Bottle", "Tetra Pack",
            "Aluminium Can", "Pet", "Dry Organic (Garden)", "Wet Organic (Food Waste)", "Other")

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
        val types = listOf("Organic", "Non Organic", "Residue", "Other")

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
        binding.imageUploadBox.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun onSubmitButtonClicked() {
        val keterangan = binding.statusInput.text.toString()
        val date = binding.dateInput.text.toString()
        val materialName = binding.materialName.text.toString()
        val type = binding.type.text.toString()
        val amount = binding.amount.text.toString().toIntOrNull()
        val photoUri = currentImageUri

        if (photoUri == null) {
            Toast.makeText(context, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (keterangan.isNotEmpty() && date.isNotEmpty() && materialName.isNotEmpty() && type
            .isNotEmpty() && amount != null) {
            val photoFile = uriToFile(photoUri)
            if (photoFile.exists()) {
                addViewModel.submitWasteWithImage(keterangan, date, materialName, type, amount,
                    photoFile)
            } else {
                Toast.makeText(context, "Gambar tidak valid", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Pastikan semua data telah diisi dengan benar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToFile(selectedImage: Uri): File {
        val contentResolver = requireContext().contentResolver
        val myFile = createTempFile(requireContext())

        val inputStream = contentResolver.openInputStream(selectedImage)
        val outputStream = FileOutputStream(myFile)

        val buffer = ByteArray(1024)
        var length: Int
        while (true) {
            length = inputStream?.read(buffer) ?: -1
            if (length == -1) break
            outputStream.write(buffer, 0, length)
        }

        outputStream.close()
        inputStream?.close()

        return myFile
    }

    private fun createTempFile(context: Context): File {
        val tempDir = context.cacheDir
        val tempFileName = "temp_image_${System.currentTimeMillis()}"
        return File.createTempFile(tempFileName, ".jpg", tempDir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}