package com.example.asahi.core.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.asahi.data.remote.pagingsource.BasePagingResponse
import com.example.asahi.data.remote.pagingsource.DataMapper
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BasePagingSource<ValueDto : DataMapper<Value>, Value : Any>(
    private val request: suspend (position: Int, pageSize: Int) -> Response<BasePagingResponse<ValueDto>>,
    private val startingPageIndex: Int = 1,
    private val pageSize: Int = 20,
) : PagingSource<Int, Value>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val position = params.key ?: startingPageIndex

        return try {
            val response = request(position, pageSize)
            val body = response.body()
                ?: return LoadResult.Error(NullPointerException("Response body is null"))

            val data = body.data.map { it.toDomain() }

            LoadResult.Page(
                data = data,
                prevKey = if (position == startingPageIndex) null else position - 1,
                nextKey = if (body.next == null || data.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            when (exception) {
                is IOException,
                is HttpException,
                is NullPointerException,
                -> LoadResult.Error(exception)

                else -> throw exception
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}