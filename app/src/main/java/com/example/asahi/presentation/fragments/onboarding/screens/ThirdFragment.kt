package com.example.asahi.presentation.fragments.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asahi.databinding.FragmentThirdBinding
import com.example.asahi.presentation.fragments.onboarding.ViewPagerFragment
import com.example.asahi.presentation.fragments.onboarding.ViewPagerFragmentDirections
//import com.example.asahi.presentation.fragments.onboarding.ViewPagerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentThirdBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSecondBack.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.previousPage()
        }

        binding.btnSecondNext.setOnClickListener {
            findNavController().navigate(ViewPagerFragmentDirections.actionViewPagerFragmentToAuthFragment())
            onBoardingFinished()
        }
    }

    private fun onBoardingFinished() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("finished", true)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}