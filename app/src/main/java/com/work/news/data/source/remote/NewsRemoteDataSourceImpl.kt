package com.work.news.data.source.remote

import com.work.news.data.parser.NewsXmlPullParserHandler
import com.work.news.util.AppExecutors
import java.net.URL

class NewsRemoteDataSourceImpl(
    private val appExecutors: AppExecutors
) : NewsRemoteDataSource {
    override fun getNewsData(url: String, callback: NewsRemoteDataSourceCallback) {
        val targetUrl = URL(url)

        appExecutors.diskIO.execute {

            val inputStream = targetUrl.openStream()
            val getNewsList = NewsXmlPullParserHandler().parse(inputStream)

            appExecutors.mainThread.execute {
                if (getNewsList.isNotEmpty()) {
                    callback.onSuccess(getNewsList)
                } else {
                    callback.onFailure()
                }
            }

        }
    }


    companion object {

        private var instance: NewsRemoteDataSourceImpl? = null

        fun getInstance(appExecutors: AppExecutors): NewsRemoteDataSourceImpl =
            instance ?: NewsRemoteDataSourceImpl(appExecutors).also {
                instance = it
            }


    }


}