package com.work.news.view.news.main.presenter

import com.work.news.data.model.NewsItem
import com.work.news.data.repository.NewsRepository
import com.work.news.data.repository.NewsRepositoryCallback
import com.work.news.network.model.NewsResponse
import com.work.news.view.news.main.NewsActivity

class NewsPresenter(
    private val newsView: NewsContract.View,
    private val newsRepository: NewsRepository
) : NewsContract.Presenter {

    private val redundancyPreventionForNewsItem by lazy { mutableSetOf<NewsItem>() }

    override fun getNewsData(url: String) {
        newsView.showDataProgress(NewsActivity.LOADING_DATA)
        getConvertNewsData(url)
    }

    private fun getConvertNewsData(url: String) {
        newsRepository.getNewsData(url, object : NewsRepositoryCallback {
            override fun onSuccess(newsList: List<NewsResponse>) {
                newsView.showLoadDataErrorState(false)
                newsList.map { newsResponse ->

                    newsResponse.toNewsItem(callback = { newsItem ->

                        if (!redundancyPreventionForNewsItem.contains(newsItem)) {
                            redundancyPreventionForNewsItem.add(newsItem)
                            newsView.showNewsData(newsItem)

                            if (newsList.size == redundancyPreventionForNewsItem.size) {
                                newsView.showDataProgress(NewsActivity.END_DATA_LOAD)
                            }
                        }
                    })
                }
            }

            override fun onFailure() {
                newsView.showLoadDataErrorState(true)
                newsView.showDataProgress(NewsActivity.END_DATA_LOAD)
            }
        })

    }

    //새로고침시 중복확인에 사용되는 list 초기화
    fun resetNewsItemList() {
        redundancyPreventionForNewsItem.clear()
    }

}