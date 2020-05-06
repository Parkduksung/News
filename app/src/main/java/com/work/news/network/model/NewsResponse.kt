package com.work.news.network.model

import com.work.news.data.model.NewsItem
import com.work.news.util.JSoupForNews

data class NewsResponse(
    var title: String = EMPTY_TEXT,
    var url: String = EMPTY_TEXT
) {
    fun toNewsItem(callback: (NewsItem) -> Unit) {
        JSoupForNews.getNewsItem(title, url) {
            callback(it)
        }
    }

    companion object {
        private const val EMPTY_TEXT = ""
    }
}