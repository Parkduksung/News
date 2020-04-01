package com.work.news.view.news.main.presenter

import com.work.news.data.model.NewsItem

interface NewsContract {

    interface View {

        fun showNewsData(item: NewsItem)

        fun showDataProgress(state: Boolean)

    }

    interface Presenter {

        fun getNewsData()

    }

}