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
import org.jsoup.nodes.Document
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
                val content: String

                val contentForText = getContentByNewsCompany(document)

                val contentForDescription =
                    document.select(CSS_QUERY_CONTENT_DESCRIPTION)
                        .attr(ATTRIBUTE_KEY_CONTENT_DESCRIPTION)

                //본문에서 발췌한다 해도 안되면 description 에서 가져오기.
                content = if (contentForText.isNotEmpty()) {
                    contentForText
                } else {
                    contentForDescription
                }

                val keywordList = getKeyword(content)

                val imageBitmap = getImageByNewsCompany(document)

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


    //이미지경로를 Bitmap 으로 변환
    private fun imagePathToBitmap(path: String): Bitmap {
        val url = URL(path)
        val conn = url.openConnection().apply { connect() }
        val stream = conn.getInputStream()
        return BitmapFactory.decodeStream(stream)
    }

    //이미지 로드 실패시 보여줄 이미지
    private fun getFailToBringBitmap(): Bitmap {
        val failToBringImage =
            ContextCompat.getDrawable(
                App.instance.context(),
                R.drawable.fail_to_bring_image
            ) as BitmapDrawable
        return failToBringImage.bitmap
    }

    //회사별 이미지 예외처리
    private fun getImageByNewsCompany(document: Document): Bitmap {

        val imagePath: String

        return when {
            newsResponseTitle.contains("헬스조선") -> {
                imagePath =
                    document.select("img").first().attr("src")
                imagePathToBitmap(imagePath)
            }
            else -> {
                imagePath =
                    document.select(CSS_QUERY_IMAGE).attr(ATTRIBUTE_KEY_IMAGE)
                imagePathToBitmap(imagePath)
            }
        }

    }

    //회사별 본문 예외처리
    private fun getContentByNewsCompany(document: Document): String {

        val content: String

        return when {
            newsResponseTitle.contains("동아일보") -> document.select("div[class=article_txt]").text()
            newsResponseTitle.contains("조선일보") -> document.select("div[class=par]").text()
            newsResponseTitle.contains("지데일리") -> document.select("div[class=cnt_view news_body_area]").text()
            newsResponseTitle.contains("MBN스타") -> document.select("div[class=article_info]").text()
            newsResponseTitle.contains("연합뉴스") -> document.select("div[class=story-news article]").text()
            newsResponseTitle.contains("비즈니스포스트") -> document.select("div[class=post-contents]").text()
            newsResponseTitle.contains("부산일보") -> document.select("div[class=article_content]").text()
            newsResponseTitle.contains("VOA Korean") -> document.select("div[class=article__body]").text()
            newsResponseTitle.contains("국제신문") -> document.select("div[class=news_article]").text()
            newsResponseTitle.contains("tbs뉴스") -> document.select("div[class=text]").text()
            newsResponseTitle.contains("서울경제신문") -> document.select("div[class=article]").text()

            newsResponseTitle.contains("매일경제") -> {
                content = document.select("div[id=Conts]").text()
                if (content.isNotEmpty()) {
                    content
                } else {
                    document.select("div[id=article_txt]").text()
                }
            }

            newsResponseTitle.contains("연합뉴스") -> {

                content = document.select(CSS_QUERY_CONTENT_TEXT).text()
                if (content.isEmpty()) {
                    //유튜브 영상일때
                    "Youtube 영상"
                } else {
                    document.select(CSS_QUERY_CONTENT_TEXT).text()
                }
            }
            newsResponseTitle.contains("SBS 뉴스") -> {

                if ((document.select(CSS_QUERY_CONTENT_TEXT).text()).isEmpty()) {
                    content = document.select("div[class=text_area]").text()
                    //유튜브방송일때와 자사 방송일때
                    if (content.isEmpty()) {
                        "Youtube 영상"
                    } else {
                        content
                    }
                } else {
                    document.select(CSS_QUERY_CONTENT_TEXT).text()
                }
            }
            else -> {
                //동아일보인데 소속을 밝히지 않은 예외.
                content = document.select("div[class=article_txt]").text()
                if (content.isNotEmpty()) {
                    content
                } else {
                    document.select(CSS_QUERY_CONTENT_TEXT).text()
                }
            }
        }
    }

    //키워드 선별 알고리즘
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

        return if (getKeywordList.size == 3) {
            getKeywordList
        } else {
            emptyList()
        }

    }


    companion object {

        //본문 발췌중 겹치는 부분의 쿼리
        const val CSS_QUERY_CONTENT_TEXT = "div[itemprop=articleBody]"

        private const val ATTRIBUTE_KEY_CONTENT_DESCRIPTION = "content"
        private const val CSS_QUERY_CONTENT_DESCRIPTION = "meta[property=og:description]"
        private const val ATTRIBUTE_KEY_IMAGE = "content"
        private const val CSS_QUERY_IMAGE = "meta[property=og:image]"
    }

}