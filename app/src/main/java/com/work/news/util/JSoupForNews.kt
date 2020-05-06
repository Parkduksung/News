package com.work.news.util

import com.work.news.data.model.NewsItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object JSoupForNews {

    private const val ATTRIBUTE_KEY_CONTENT_DESCRIPTION = "content"
    private const val CSS_QUERY_CONTENT_DESCRIPTION = "meta[property=og:description]"
    private const val CSS_QUERY_CONTENT_TEXT = "div[itemprop=articleBody]"
    private const val ATTRIBUTE_KEY_IMAGE = "content"
    private const val CSS_QUERY_IMAGE = "meta[property=og:image]"

    private const val EMPTY_CONTENT = ""
    private const val EMPTY_IMAGE = ""

    fun getNewsItem(title: String, url: String, callback: (NewsItem) -> Unit) {

        AppExecutors().diskIO.execute {
            try {
                val document =
                    Jsoup.connect(url).get()

                //본문에서 발췌한다 해도 안되면 description 에서 가져오기.
                val content =
                    if (getContentByNewsCompany(
                            document,
                            title
                        ).isNotEmpty()
                    ) {
                        getContentByNewsCompany(
                            document,
                            title
                        )
                    } else {
                        getContentForDescription(document)
                    }

                val keywordList = getKeyword(content)

                val image = getImageByNewsCompany(document, title)

                AppExecutors().mainThread.execute {

                    val newsItem =
                        NewsItem(
                            title,
                            url,
                            content,
                            image,
                            keywordList
                        )

                    callback(newsItem)

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
                    callback(newsItem)
                }
            }
        }
    }


    //회사별 이미지 예외처리
    private fun getImageByNewsCompany(document: Document, title: String): String {
        return when {
            title.contains("헬스조선") -> {
                document.select("img").first().attr("src")
            }
            else -> {
                document.select(CSS_QUERY_IMAGE).attr(ATTRIBUTE_KEY_IMAGE)
            }
        }
    }

    private fun getContentForDescription(document: Document): String =
        document.select(CSS_QUERY_CONTENT_DESCRIPTION)
            .attr(ATTRIBUTE_KEY_CONTENT_DESCRIPTION)

    //회사별 본문 예외처리
    private fun getContentByNewsCompany(document: Document, title: String): String {

        val content: String

        return when {
            title.contains("동아일보") -> document.select("div[class=article_txt]").text()
            title.contains("조선일보") -> document.select("div[class=par]").text()
            title.contains("지데일리") -> document.select("div[class=cnt_view news_body_area]").text()
            title.contains("MBN스타") -> document.select("div[class=article_info]").text()
            title.contains("연합뉴스") -> document.select("div[class=story-news article]").text()
            title.contains("비즈니스포스트") -> document.select("div[class=post-contents]").text()
            title.contains("부산일보") -> document.select("div[class=article_content]").text()
            title.contains("VOA Korean") -> document.select("div[class=article__body]").text()
            title.contains("국제신문") -> document.select("div[class=news_article]").text()
            title.contains("tbs뉴스") -> document.select("div[class=text]").text()
            title.contains("서울경제신문") -> document.select("div[class=article]").text()

            title.contains("매일경제") -> {
                content = document.select("div[id=Conts]").text()
                if (content.isNotEmpty()) {
                    content
                } else {
                    document.select("div[id=article_txt]").text()
                }
            }

            title.contains("연합뉴스") -> {

                content = document.select(CSS_QUERY_CONTENT_TEXT).text()
                if (content.isEmpty()) {
                    //유튜브 영상일때
                    "Youtube 영상"
                } else {
                    document.select(CSS_QUERY_CONTENT_TEXT).text()
                }
            }
            title.contains("SBS 뉴스") -> {

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


}