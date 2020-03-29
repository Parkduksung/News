package com.work.news.view.news.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.work.news.R
import com.work.news.view.news.main.presenter.NewsContract


class NewsActivity : AppCompatActivity(), NewsContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_main_activity)
    }
}