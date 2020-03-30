package com.work.news.data.repository

import com.work.news.network.model.NewsResponse

interface NewsRepositoryCallback {

    fun onSuccess(newsList: List<NewsResponse>)
    fun onFailure()

}