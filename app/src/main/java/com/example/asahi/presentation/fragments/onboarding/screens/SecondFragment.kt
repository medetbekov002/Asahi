package com.example.asahi.presentation.fragments.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.asahi.R
import com.example.asahi.databinding.FragmentSecondBinding
import com.example.asahi.presentation.fragments.onboarding.ViewPagerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondFragment : Fragment(R.layout.fragment_second) {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSecondBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSecondNext.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.nextPage()
        }

        binding.tvSecondBack.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.previousPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}