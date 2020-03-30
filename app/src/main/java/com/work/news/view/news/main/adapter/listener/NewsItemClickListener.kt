package com.work.news.view.news.main.adapter.listener

import com.work.news.data.model.NewsItem

interface NewsItemClickListener {
    fun newsItemClick(newsItem: NewsItem)
}