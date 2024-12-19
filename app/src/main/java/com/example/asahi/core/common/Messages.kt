package com.example.asahi.core.common

sealed interface Messages {
    data object NetworkIsDisconnected : Messages
    data object ShowProgressBar: Messages
    data object HideProgressBar: Messages
}