@file:Suppress("DEPRECATION")

package com.work.news.view.news.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.view.news.details.presenter.NewsDetailsContract
import com.work.news.view.news.details.presenter.NewsDetailsPresenter
import kotlinx.android.synthetic.main.news_details_fragment.*

class NewsDetailsFragment : Fragment(), NewsDetailsContract.View, View.OnClickListener,
    OnBackPressedListener.ContainWebViewForm {


    private lateinit var presenter: NewsDetailsContract.Presenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.news_details_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = NewsDetailsPresenter(this)
        pb_news_details_loading.bringToFront()
        ib_news_details_back_pressed.setOnClickListener(this)


        init()

    }


    private fun init() {

        val getNewsItem = arguments?.getParcelable<NewsItem>(NEWS_ITEM)

        getNewsItem?.let { newsItem ->

            val splitNewsTitle = newsItem.newsItemTitle.split("-")

            if (splitNewsTitle.isNotEmpty() && splitNewsTitle.size == 2) {
                tv_news_details_title.text = splitNewsTitle[0]
            } else {
                tv_news_details_title.text = newsItem.newsItemTitle
            }

            cl_news_details_keyword.isVisible = newsItem.newsItemKeywordList.isNotEmpty()

            if (newsItem.newsItemKeywordList.isNotEmpty()) {
                tv_news_details_keyword1.text = newsItem.newsItemKeywordList[0]
                tv_news_details_keyword2.text = newsItem.newsItemKeywordList[1]
                tv_news_details_keyword3.text = newsItem.newsItemKeywordList[2]
            }

            showUrl(wb_news_details, newsItem.newsItemUrl)

        }

    }

    //뒤로가기
    override fun onBackPressed(): Int {

        return if (TOGGLE_PAGE_HISTORY) {
            wb_news_details.goBack()
            BACK_WITH_WEB_VIEW_HISTORY
        } else {
            if (fragmentManager?.backStackEntryCount != 0) {
                BACK_NO_WEB_VIEW_HISTORY_EXIST_POP_STACK
            } else {
                BACK_NO_WEB_VIEW_HISTORY_NONE_POP_STACK
            }
        }
    }

    //클릭리스너
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_news_details_back_pressed -> {
                fragmentManager?.popBackStack()
            }
        }
    }

    //웹뷰
    private fun showUrl(webView: WebView, url: String) {

        webView.loadUrl(url)
        webView.settings.apply {
            this.setGeolocationEnabled(true)
            this.javaScriptEnabled = true
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }

        }

        webView.webViewClient = object : WebViewClient() {

            //url 연결 안될때
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showConnectUrlErrorState(true)
                pb_news_details_loading?.let {
                    it.visibility = View.GONE
                }
            }

            //url 연결중
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                showConnectUrlErrorState(false)

                pb_news_details_loading?.let {
                    it.visibility = View.VISIBLE
                }

                //예외처리
                if (url != null) {

                    //전화번호 눌렀을때 처리.
                    if (url.startsWith("tel:")) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        startActivity(intent)
                        pb_news_details_loading.visibility = View.GONE
                        return true
                    }

                    //다른 앱이 실행되거나 설치 등 intent 로 시작되는건 구글마켓으로 들어가게 처리.
                    if (url.startsWith("intent:")) {

                        val schemeIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        val existPackage = Package.getPackage(schemeIntent.`package`)

                        return if (existPackage != null) {
                            val existPackageIntent = Intent(Intent.ACTION_VIEW)
                            existPackageIntent.data =
                                Uri.parse("https://play.google.com/store/apps/details?id=${existPackage.name}&hl=ko")
                            startActivity(existPackageIntent)
                            pb_news_details_loading.visibility = View.GONE
                            true
                        } else {
                            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                startActivity(this)
                            }
                            true
                        }
                    }
                }
                return false
            }


            //페이지가 켜졌을때
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                pb_news_details_loading?.let {
                    it.visibility = View.GONE
                }

                ll_news_details_title_and_keyword?.let {
                    it.isVisible = !webView.canGoBack()
                }

                TOGGLE_PAGE_HISTORY = webView.canGoBack()

            }
        }

    }

    //인터넷 연결 상태에 대한 사용자에게 보여주는 부분
    private fun showConnectUrlErrorState(state: Boolean) {
        wb_news_details?.let {
            it.isVisible = !state
        }
        tv_news_details_load_error?.let {
            it.isVisible = state
        }
    }

    companion object {

        const val NEWS_ITEM = "newsItem"

        private var TOGGLE_PAGE_HISTORY = false

        const val BACK_WITH_WEB_VIEW_HISTORY = 0
        const val BACK_NO_WEB_VIEW_HISTORY_EXIST_POP_STACK = 1
        const val BACK_NO_WEB_VIEW_HISTORY_NONE_POP_STACK = 2


        fun newInstance(
            newsItem: NewsItem
        ) = NewsDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NEWS_ITEM, newsItem)
            }
        }

    }


}