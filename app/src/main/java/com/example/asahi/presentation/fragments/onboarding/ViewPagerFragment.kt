package com.example.asahi.presentation.fragments.onboarding

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.asahi.R
import com.example.asahi.presentation.fragments.onboarding.screens.FirstFragment
import com.example.asahi.presentation.fragments.onboarding.screens.ThirdFragment
import com.example.asahi.core.utils.viewBinding
import com.example.asahi.databinding.FragmentViewPagerBinding
import com.example.asahi.presentation.fragments.onboarding.adapter.ViewPagerAdapter
import com.example.asahi.presentation.fragments.onboarding.screens.SecondFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    val binding by viewBinding(FragmentViewPagerBinding::bind)

    private var isTransactionPending = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = listOf(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment()
        )

        val adapter = ViewPagerAdapter(this, fragmentList)
        binding.vpOnboarding.adapter = adapter
        binding.indicator.setViewPager(binding.vpOnboarding)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            previousPage()
        }
    }

    fun nextPage() {
        if (!isTransactionPending) {
            isTransactionPending = true
            binding.vpOnboarding.currentItem += 1
            isTransactionPending = false
        }
    }

    fun previousPage() {
        if (!isTransactionPending) {
            isTransactionPending = true
            if (binding.vpOnboarding.currentItem > 0) {
                binding.vpOnboarding.currentItem -= 1
            } else {
                requireActivity().finish()
            }
            isTransactionPending = false
        }
    }

}