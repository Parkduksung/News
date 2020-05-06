package com.work.news.view.news.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.work.news.App
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.data.repository.NewsRepositoryImpl
import com.work.news.data.source.remote.NewsRemoteDataSourceImpl
import com.work.news.util.AppExecutors
import com.work.news.view.news.details.NewsDetailsFragment
import com.work.news.view.news.details.OnBackPressedListener
import com.work.news.view.news.main.adapter.NewsAdapter
import com.work.news.view.news.main.adapter.listener.NewsItemClickListener
import com.work.news.view.news.main.presenter.NewsContract
import com.work.news.view.news.main.presenter.NewsPresenter
import kotlinx.android.synthetic.main.news_main_activity.*


class NewsActivity : AppCompatActivity(), NewsContract.View, NewsItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {


    private lateinit var presenter: NewsPresenter

    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(this) }

    private var backPressedWaitTime: Long = INIT_BACK_PRESSED_TIME


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_main_activity)

        presenter =
            NewsPresenter(
                this, NewsRepositoryImpl.getInstance(
                    NewsRemoteDataSourceImpl.getInstance(AppExecutors())
                )
            )

        swipe_news_main.setColorSchemeResources(R.color.colorPrimary)
        swipe_news_main.setOnRefreshListener(this)

        startView()

    }


    private fun startView() {
        rv_news_main.run {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsAdapter
            newsAdapter.clearListData()
        }
        presenter.getNewsData(NEWS_URL)
    }


    //뒤로가기
    override fun onBackPressed() {

        var checkBackPressed = INIT_CHECK_COUNT

        supportFragmentManager.fragments.forEach { fragment ->
            checkBackPressed =
                (fragment as OnBackPressedListener.ContainWebViewForm).onBackPressed()

            if (checkBackPressed == NewsDetailsFragment.BACK_NO_WEB_VIEW_HISTORY_EXIST_POP_STACK) {
                supportFragmentManager.popBackStack()
            }
        }

        if (checkBackPressed == NewsDetailsFragment.BACK_NO_WEB_VIEW_HISTORY_NONE_POP_STACK) {
            if (System.currentTimeMillis() - backPressedWaitTime >= OPERATE_BACK_PRESSED_TIME) {
                backPressedWaitTime = System.currentTimeMillis()
                Toast.makeText(
                    App.instance.context(),
                    getString(R.string.backPressed_exit_notification_message),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                super.onBackPressed()
            }
        }
    }


    //인터넷 연결 상태에 대한 사용자에게 보여주는 부분
    override fun showLoadDataErrorState(state: Boolean) {
        rv_news_main.isVisible = !state
        tv_news_main_load_error.isVisible = state
    }

    //item 클릭 시, 상세페이지로 화면전환
    override fun newsItemClick(newsItem: NewsItem) {

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.news_main_container,
                NewsDetailsFragment
                    .newInstance(newsItem)
            )
            .addToBackStack(null)
            .commit()
    }

    //progressBar
    override fun showDataProgress(state: Boolean) {
        if (state) {
            newsAdapter.clearListData()
        }
        pb_news_main_loading.isVisible = state
        pb_news_main_loading.isIndeterminate = state
    }


    //새로고침
    override fun onRefresh() {
        if (!pb_news_main_loading.isVisible) {
            presenter.resetNewsItemList()
            startView()
            swipe_news_main.isRefreshing = false
        } else {
            Toast.makeText(
                App.instance.context(),
                getString(R.string.loadData_not_yet_notification_message),
                Toast.LENGTH_SHORT
            )
                .show()
            swipe_news_main.isRefreshing = false
        }
    }

    override fun showNewsData(item: NewsItem) {
        newsAdapter.addData(item)
    }


    companion object {

        private const val NEWS_URL = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"

        private const val INIT_BACK_PRESSED_TIME: Long = 0
        private const val OPERATE_BACK_PRESSED_TIME: Long = 2000
        private const val INIT_CHECK_COUNT = 2

        const val LOADING_DATA = true
        const val END_DATA_LOAD = false

    }


}