package com.example.asahi.data.remote.pagingsource

data class BasePagingResponse<ValueDto>(
    val data: List<ValueDto>,
    val next: Int?
)