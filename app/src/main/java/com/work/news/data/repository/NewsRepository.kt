package com.work.news.data.repository

interface NewsRepository {

    fun getNewsData(
        url: String,
        callback: NewsRepositoryCallback
    )

}