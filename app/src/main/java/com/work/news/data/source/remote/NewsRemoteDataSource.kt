package com.work.news.data.source.remote

interface NewsRemoteDataSource {

    fun getNewsData(
        url: String,
        callback: NewsRemoteDataSourceCallback
    )

}