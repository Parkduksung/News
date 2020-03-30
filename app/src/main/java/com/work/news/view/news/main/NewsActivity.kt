package com.work.news.view.news.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.work.news.App
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.data.repository.NewsRepositoryImpl
import com.work.news.data.source.remote.NewsRemoteDataSourceImpl
import com.work.news.util.AppExecutors
import com.work.news.view.news.main.adapter.NewsAdapter
import com.work.news.view.news.main.adapter.listener.NewsItemClickListener
import com.work.news.view.news.main.presenter.NewsContract
import com.work.news.view.news.main.presenter.NewsPresenter
import kotlinx.android.synthetic.main.news_main_activity.*


class NewsActivity : AppCompatActivity(), NewsContract.View, NewsItemClickListener {

    private lateinit var presenter: NewsContract.Presenter

    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_main_activity)


        presenter = NewsPresenter(
            this, NewsRepositoryImpl.getInstance(
                NewsRemoteDataSourceImpl.getInstance(AppExecutors())
            )
        )

        rv_news_main.run {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsAdapter
        }

        presenter.getNewsData()
    }


    override fun showNewsData(item: NewsItem) {

        showEndNewsDataLoad()

        rv_news_main.run {
            newsAdapter.addData(item)
        }

    }


    override fun newsItemClick(newsItem: NewsItem) {
        Toast.makeText(App.instance.context(), newsItem.newsItemUrl, Toast.LENGTH_SHORT).show()
    }


    override fun showLoadingProgress() {
        newsAdapter.clearListData()
        pb_news_main_loading.bringToFront()
        pb_news_main_loading.visibility = View.VISIBLE
        pb_news_main_loading.isIndeterminate = true
    }

    override fun showEndNewsDataLoad() {
        pb_news_main_loading.visibility = View.GONE
        pb_news_main_loading.isIndeterminate = false
    }


}