package com.work.news.network.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.work.news.App
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.util.AppExecutors
import org.jsoup.Jsoup
import java.net.URL

class NewsResponse(
    var newsResponseName: String = "",
    var newsResponseUrl: String = ""
) {

    interface ToNewsItemCallback {
        fun convertData(newsItem: NewsItem)
    }

    fun toNewsItem(callback: ToNewsItemCallback) {

        AppExecutors().diskIO.execute {

            try {
                val document = Jsoup.connect(newsResponseUrl).get()

                val content =
                    document.select(CSS_QUERY_CONTENT).attr(ATTRIBUTE_KEY_CONTENT)
                val imagePath =
                    document.select(CSS_QUERY_IMAGE).attr(ATTRIBUTE_KEY_IMAGE)

                val imageBitmap = imagePathToBitmap(imagePath)

                AppExecutors().mainThread.execute {

                    val newsItem = NewsItem(
                        newsResponseName,
                        newsResponseUrl,
                        content,
                        imageBitmap
                    )
                    callback.convertData(newsItem)
                }

            } catch (e: Exception) {

                val failToBringImage =
                    ContextCompat.getDrawable(
                        App.instance.context(),
                        R.drawable.fail_to_bring_image
                    ) as BitmapDrawable

                val toImageBitmap = failToBringImage.bitmap

                AppExecutors().mainThread.execute {

                    val newsItem = NewsItem(
                        newsResponseName,
                        newsResponseUrl,
                        "",
                        toImageBitmap
                    )
                    callback.convertData(newsItem)
                }
            }
        }
    }


    private fun imagePathToBitmap(path: String): Bitmap {
        val url = URL(path)
        val conn = url.openConnection().apply { connect() }
        val stream = conn.getInputStream()
        return BitmapFactory.decodeStream(stream)
    }


    companion object {

        private const val ATTRIBUTE_KEY_CONTENT = "content"
        private const val CSS_QUERY_CONTENT = "meta[property=og:description]"
        private const val ATTRIBUTE_KEY_IMAGE = "content"
        private const val CSS_QUERY_IMAGE = "meta[property=og:image]"
    }

}