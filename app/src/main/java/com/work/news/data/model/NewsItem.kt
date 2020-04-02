package com.work.news.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NewsItem(
    val newsItemTitle: String,
    val newsItemUrl: String,
    val newsItemContent: String,
    val newsItemImage: Bitmap,
    val newsItemKeywordList: List<String>
) : Parcelable