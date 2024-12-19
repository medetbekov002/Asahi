package com.example.asahi.presentation.fragments.search.simplesearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.asahi.R
import com.example.asahi.databinding.FragmentSimpleSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimpleSearchFragment : Fragment(R.layout.fragment_simple_search) {

    private var _binding: FragmentSimpleSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSimpleSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}