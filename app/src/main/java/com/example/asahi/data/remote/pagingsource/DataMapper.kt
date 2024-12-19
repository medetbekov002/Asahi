package com.example.asahi.data.remote.pagingsource

interface DataMapper<Value> {
    fun toDomain(): Value
}