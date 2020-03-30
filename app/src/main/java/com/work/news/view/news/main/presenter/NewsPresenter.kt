package com.work.news.view.news.main.presenter

import com.work.news.data.model.NewsItem
import com.work.news.data.repository.NewsRepository
import com.work.news.data.repository.NewsRepositoryCallback
import com.work.news.network.model.NewsResponse

class NewsPresenter(
    private val newsView: NewsContract.View,
    private val newsRepository: NewsRepository
) : NewsContract.Presenter {

    override fun getNewsData() {

        newsView.showLoadingProgress()

        newsRepository.getNewsData(NEWS_URL, object : NewsRepositoryCallback {
            override fun onSuccess(newsList: List<NewsResponse>) {

                newsList.map {
                    it.toNewsItem(object : NewsResponse.ToNewsItemCallback {
                        override fun convertData(newsItem: NewsItem) {
                            newsView.showNewsData(newsItem)
                        }
                    })
                }
            }

            override fun onFailure() {

            }
        })
    }

    companion object {
        private const val NEWS_URL = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"


    }
}