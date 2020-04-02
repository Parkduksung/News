package com.work.news.view.news.main.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.view.news.main.adapter.listener.NewsItemClickListener
import kotlinx.android.synthetic.main.news_item.view.*

class NewsViewHolder(
    parent: ViewGroup,
    private val newsItemClickListener: NewsItemClickListener
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
    ) {

    fun bind(item: NewsItem) {
        super.itemView.run {

            tv_news_item_title.text = item.newsItemTitle
            tv_news_item_content.text = item.newsItemContent

            checkKeywordList(item.newsItemKeywordList)

            val radiusOfImageBackground =
                context.resources.getDimensionPixelSize(R.dimen.corner_radius_size)

            Glide.with(this)
                .load(item.newsItemImage)
                .transform(CenterCrop(), RoundedCorners(radiusOfImageBackground))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_news_item_image)


            setOnClickListener {
                newsItemClickListener.newsItemClick(item)
            }
        }
    }


    private fun checkKeywordList(keywordList: List<String>) {

        super.itemView.run {

            ll_news_keyword.isVisible = keywordList.isNotEmpty()

            if (keywordList.isNotEmpty() && keywordList.size == 3) {
                tv_news_keyword1.text = keywordList[0]
                tv_news_keyword2.text = keywordList[1]
                tv_news_keyword3.text = keywordList[2]
            }
        }
    }

}