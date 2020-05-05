package com.work.news.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NewsItem(
    val title: String,
    val url: String,
    val content: String,
    val image: Bitmap,
    val keywordList: List<String>
) : Parcelable