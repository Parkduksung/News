package com.work.news.data.source.remote

import com.work.news.network.model.NewsResponse

interface NewsRemoteDataSourceCallback {

    fun onSuccess(newsList: List<NewsResponse>)
    fun onFailure()

}