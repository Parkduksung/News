package com.work.news.view.news.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

        showDataProgress(END_DATA_LOAD)

        rv_news_main.run {
            newsAdapter.addData(item)
        }

    }

    override fun newsItemClick(newsItem: NewsItem) {
        Toast.makeText(App.instance.context(), newsItem.newsItemUrl, Toast.LENGTH_SHORT).show()
    }

    override fun showDataProgress(state: Boolean) {
        if (state) {
            newsAdapter.clearListData()
        }
        pb_news_main_loading.isVisible = state
        pb_news_main_loading.isIndeterminate = state
    }


    companion object {

        const val LOADING_DATA = true
        const val END_DATA_LOAD = false

    }


}