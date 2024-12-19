package com.example.asahi.core.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.asahi.core.common.Either
import com.example.asahi.core.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseFragment<Binding : ViewBinding, ViewModel : BaseViewModel>(
    @LayoutRes layoutId: Int,
) : Fragment(layoutId) {
    protected abstract val binding: Binding
    protected abstract val viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        launchObserver()
        constructorListeners()
    }

    protected open fun init() {}
    protected open fun launchObserver() {}
    protected open fun constructorListeners() {}

    protected fun <T> StateFlow<UiState<T>>.observeUIState(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        success: ((data: T) -> Unit)? = null,
        loading: (() -> Unit)? = null,
        error: ((message: String, throwable: Throwable) -> Unit)? = null,
        gatherIfSucceed: ((state: UiState<T>) -> Unit)? = null,
    ) {
        safeFlowGather(lifecycleState) {
            collect { state ->
                gatherIfSucceed?.invoke(state)
                when (state) {
                    is UiState.Loading -> loading?.invoke()
                    is UiState.Success -> success?.invoke(state.data)
                    is UiState.Error -> error?.invoke(state.message, state.throwable)
                }
            }
        }
    }

    private fun safeFlowGather(
        lifeCycleState: Lifecycle.State = Lifecycle.State.STARTED,
        gather: suspend () -> Unit,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(lifeCycleState) {
                gather()
            }
        }
    }

    fun <T> Flow<Either<String, T>>.safeFlowGather(
        actionIfEitherRight: suspend (T) -> Unit,
        actionIfEitherLeft: (error: String?) -> Unit,
    ) {
        safeFlowGather {
            collect { either ->
                when (either) {
                    is Either.Right -> either.right?.let { actionIfEitherRight(it) }
                    is Either.Left -> actionIfEitherLeft(either.left)
                }
            }
        }
    }

}