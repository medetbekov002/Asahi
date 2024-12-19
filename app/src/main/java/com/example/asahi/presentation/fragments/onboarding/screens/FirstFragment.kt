package com.example.asahi.presentation.fragments.onboarding.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.asahi.databinding.FragmentFirstBinding
import com.example.asahi.presentation.fragments.onboarding.ViewPagerFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferences.getBoolean("SkipClicked", false)) {
            binding.imgSecondIcon.visibility = View.GONE
        } else {
            binding.imgSecondIcon.visibility = View.VISIBLE
        }

        binding.imgSecondIcon.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.let {
                it.binding.vpOnboarding.currentItem = 2
                val editor = sharedPreferences.edit()
                editor.putBoolean("SkipClicked", true)
                editor.apply()
            }
        }

        binding.btnSecondNext.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.nextPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}