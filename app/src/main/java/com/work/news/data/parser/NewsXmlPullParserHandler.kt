package com.work.news.data.parser

import com.work.news.network.model.NewsResponse
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class NewsXmlPullParserHandler {

    private val products = ArrayList<NewsResponse>()
    private var product: NewsResponse? = null
    private var text: String? = null

    fun parse(
        inputStream: InputStream
    ): List<NewsResponse> {
        try {
            val factory = XmlPullParserFactory.newInstance()

            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagName.equals("item", ignoreCase = true)) {
                        product = NewsResponse()
                    }
                    XmlPullParser.TEXT -> {
                        text = parser.text
                    }
                    XmlPullParser.END_TAG -> {
                        when {
                            tagName.equals("item", ignoreCase = true) -> {
                                product?.let { products.add(it) }
                            }

                            tagName.equals("title", ignoreCase = true) -> {
                                product?.title = text.orEmpty()
                            }

                            tagName.equals("link", ignoreCase = true) -> {
                                product?.url = text.orEmpty()
                            }

                        }
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return products
    }

}