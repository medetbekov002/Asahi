package com.example.asahi.data.remote.pagingsource

import com.example.asahi.data.remote.pagingsource.DataMapper
import com.example.asahi.data.remote.pagingsource.MyData

data class MyDataDto(
    val id: Int,
    val name: String,
) : DataMapper<MyData> {
    override fun toDomain(): MyData {
        return MyData(id = id, name = name)
    }

}