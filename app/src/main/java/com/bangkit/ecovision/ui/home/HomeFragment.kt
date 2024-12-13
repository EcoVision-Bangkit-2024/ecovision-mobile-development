package com.bangkit.ecovision.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.ecovision.MainActivity
import com.bangkit.ecovision.R
import com.bangkit.ecovision.data.response.get.Data
import com.bangkit.ecovision.databinding.FragmentHomeBinding
import com.bangkit.ecovision.ui.LoadingDialogFragment
import com.bangkit.ecovision.ui.analytics.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.fetchMaterials()

        homeViewModel.materialList.observe(viewLifecycleOwner) { materials ->
            setupRecyclerView(materials)
        }

        homeViewModel.totalMasuk.observe(viewLifecycleOwner) { totalMasuk ->
            binding.materialIn.text = totalMasuk.toString()
        }

        homeViewModel.totalKeluar.observe(viewLifecycleOwner) { totalKeluar ->
            binding.materialOut.text = totalKeluar.toString()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        }

        binding.nonAnorganic.setOnClickListener {
            handleAnalyticsNavigation("Non Organic")
        }

        binding.organic.setOnClickListener {
            handleAnalyticsNavigation("Organic")
        }

        binding.residu.setOnClickListener {
            handleAnalyticsNavigation("Residue")
        }

        binding.other.setOnClickListener {
            handleAnalyticsNavigation("Other")
        }

        return root
    }

    private fun handleAnalyticsNavigation(type: String) {
        (activity as MainActivity).allowAnalyticsAccess()

        sharedViewModel.setSelectedType(type)
        findNavController().navigate(R.id.navigation_analytics)

        val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_analytics
    }


    private fun setupRecyclerView(materials: List<Data>) {
        val adapter = MaterialAdapter(materials)
        binding.materialRecyclerView.adapter = adapter
        binding.materialRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
        loadingDialog?.show(parentFragmentManager, "loadingDialog")
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setSelectedType(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}