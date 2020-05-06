package com.work.news.network.model

import com.work.news.data.model.NewsItem
import com.work.news.util.AppExecutors
import com.work.news.util.JSoupForNews
import org.jsoup.Jsoup

data class NewsResponse(
    var title: String = EMPTY_TEXT,
    var url: String = EMPTY_TEXT
) {

    interface ToNewsItemCallback {
        fun convertData(newsItem: NewsItem)
    }

    fun toNewsItem(callback: ToNewsItemCallback) {

        AppExecutors().diskIO.execute {

            try {

                val document =
                    Jsoup.connect(url).get()

                //본문에서 발췌한다 해도 안되면 description 에서 가져오기.
                val content =
                    if (JSoupForNews.getContentByNewsCompany(
                            document,
                            title
                        ).isNotEmpty()
                    ) {
                        JSoupForNews.getContentByNewsCompany(
                            document,
                            title
                        )
                    } else {
                        JSoupForNews.getContentForDescription(document)
                    }

                val keywordList =
                    JSoupForNews.getKeyword(content)

                val image =
                    JSoupForNews.getImageByNewsCompany(document, title)

                AppExecutors().mainThread.execute {

                    val newsItem =
                        NewsItem(
                            title,
                            url,
                            content,
                            image,
                            keywordList
                        )
                    callback.convertData(newsItem)
                }

            } catch (e: Exception) {

                AppExecutors().mainThread.execute {
                    val newsItem =
                        NewsItem(
                            title,
                            url,
                            EMPTY_CONTENT,
                            EMPTY_IMAGE,
                            emptyList()
                        )
                    callback.convertData(newsItem)
                }
            }
        }
    }

    companion object {

        private const val EMPTY_TEXT = ""
        private const val EMPTY_CONTENT = ""
        private const val EMPTY_IMAGE = ""

    }
}