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

data class NewsResponse(
    var newsResponseTitle: String = "",
    var newsResponseUrl: String = ""
) {

    interface ToNewsItemCallback {
        fun convertData(newsItem: NewsItem)
    }

    fun toNewsItem(callback: ToNewsItemCallback) {

        AppExecutors().diskIO.execute {

            try {
                val document = Jsoup.connect(newsResponseUrl).get()

                val content = document.select(CSS_QUERY_CONTENT).text()

                val keywordList = getKeyword(content)

                val imagePath =
                    document.select(CSS_QUERY_IMAGE).attr(ATTRIBUTE_KEY_IMAGE)

                val imageBitmap = imagePathToBitmap(imagePath)

                AppExecutors().mainThread.execute {

                    val newsItem = NewsItem(
                        newsResponseTitle,
                        newsResponseUrl,
                        content,
                        imageBitmap,
                        keywordList
                    )
                    callback.convertData(newsItem)
                }

            } catch (e: Exception) {

                AppExecutors().mainThread.execute {
                    val newsItem = NewsItem(
                        newsResponseTitle,
                        newsResponseUrl,
                        "",
                        getFailToBringBitmap(),
                        emptyList()
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


    private fun getFailToBringBitmap(): Bitmap {
        val failToBringImage =
            ContextCompat.getDrawable(
                App.instance.context(),
                R.drawable.fail_to_bring_image
            ) as BitmapDrawable
        return failToBringImage.bitmap
    }


    private fun getKeyword(content: String): List<String> {

        //반환할 키워드 3개의 리스트
        val getKeywordList = mutableListOf<String>()


        if (content.isEmpty()) {
            return emptyList()
        }

        val foundCountPerSentencesHashMap = mutableMapOf<String, Int>()

        val toSplitContent = content.split(" ")
        val findSentencesCount = mutableListOf<Int>()

        //중복되는 갯수를 얻는 for 문
        for (i in 0 until toSplitContent.size) {
            var count = 0
            toSplitContent.forEach { splitSentences ->
                if (toSplitContent[i] == splitSentences) {
                    count++
                }
            }
            findSentencesCount.add(count)
        }

        //문장에 대한 발견된 수를 합치는 for 문
        for (i in 0 until toSplitContent.size) {
            foundCountPerSentencesHashMap[toSplitContent[i]] = findSentencesCount[i]
        }

        //문장에 대한 오름차순
        val toSortSentencesMap =
            foundCountPerSentencesHashMap.toList().sortedWith(compareBy { it.second }).toMap()

        val segList = hashMapOf<Int, MutableList<String>>()
        val i = toSortSentencesMap.entries.iterator()

        //발견된 횟수에 대한 문장들 집합 작업
        while (i.hasNext()) {
            val next = i.next()
            if (segList[next.value] != null) {
                val city = segList[next.value]

                city?.let {
                    it.add(next.key)
                    segList[next.value] = it
                }
            } else {
                val city = ArrayList<String>()
                city.add(next.key)
                segList[next.value] = city
            }
        }


        //발견된 숫자에 대한 내림차순
        val allSentencesByFoundCount = segList.toList().sortedByDescending { it.first }.toMap()

        var getKeywordCount = 0


        //발견된 횟수가 많은 순서대로의 문장들 중 3개를 선별하는 작업.
        allSentencesByFoundCount.forEach {
            // it.value => 발견된 횟수
            it.value.forEach { sentences ->

                if (getKeywordCount < 3 && sentences.length >= 2) {
                    getKeywordCount++
                    getKeywordList.add(sentences)
                }
            }
        }

        return getKeywordList
    }


    companion object {

        const val CSS_QUERY_CONTENT = "div[itemprop=articleBody]"
        private const val ATTRIBUTE_KEY_IMAGE = "content"
        private const val CSS_QUERY_IMAGE = "meta[property=og:image]"
    }

}