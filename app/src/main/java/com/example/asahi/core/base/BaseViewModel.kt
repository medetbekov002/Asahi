package com.example.asahi.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asahi.core.common.Either
import com.example.asahi.core.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected open fun <T> Flow<Either<Throwable, T>>.collectFlowAsState(
        state: MutableStateFlow<UiState<T>>,
    ) {
        viewModelScope.launch {
            this@collectFlowAsState.collect { result ->
                when (result) {
                    is Either.Left -> {
                        result.left?.let { throwable ->
                            val message = throwable.message ?: "Неизвестная ошибка!"
                            state.value = UiState.Error(throwable, message)
                        }
                    }

                    is Either.Right -> {
                        result.right?.let { data ->
                            state.value = UiState.Success(data)
                        }
                    }
                }
            }
        }
    }

}