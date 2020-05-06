package com.work.news.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NewsItem(
    val title: String,
    val url: String,
    val content: String,
    val image: String,
    val keywordList: List<String>
) : Parcelable