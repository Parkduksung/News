package com.work.news.data.repository

import com.work.news.App
import com.work.news.data.source.remote.NewsRemoteDataSource
import com.work.news.data.source.remote.NewsRemoteDataSourceCallback
import com.work.news.ext.isConnectedToNetwork
import com.work.news.network.model.NewsResponse

class NewsRepositoryImpl(
    private val newsRemoteDataSource: NewsRemoteDataSource
) : NewsRepository {

    override fun getNewsData(url: String, callback: NewsRepositoryCallback) {

        if (App.instance.context().isConnectedToNetwork()) {
            newsRemoteDataSource.getNewsData(url, object : NewsRemoteDataSourceCallback {
                override fun onSuccess(newsList: List<NewsResponse>) {
                    callback.onSuccess(newsList)
                }

                override fun onFailure() {
                    callback.onFailure()
                }
            })
        } else {
            callback.onFailure()
        }
    }

    companion object {

        private var instance: NewsRepositoryImpl? = null
        fun getInstance(
            newsRemoteDataSource: NewsRemoteDataSource
        ): NewsRepositoryImpl =
            instance ?: NewsRepositoryImpl(newsRemoteDataSource).also {
                instance = it
            }

    }

}