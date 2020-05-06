package com.work.news.view.news.main.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.work.news.R
import com.work.news.data.model.NewsItem
import com.work.news.view.news.main.adapter.listener.NewsItemClickListener

class NewsViewHolder(
    parent: ViewGroup,
    private val newsItemClickListener: NewsItemClickListener
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
    ) {

    private val newsTitle = itemView.findViewById<TextView>(R.id.tv_news_item_title)
    private val newsImage = itemView.findViewById<ImageView>(R.id.iv_news_item_image)
    private val newsContent = itemView.findViewById<TextView>(R.id.tv_news_item_content)
    private val newsKeyword1 = itemView.findViewById<TextView>(R.id.tv_news_keyword1)
    private val newsKeyword2 = itemView.findViewById<TextView>(R.id.tv_news_keyword2)
    private val newsKeyword3 = itemView.findViewById<TextView>(R.id.tv_news_keyword3)

    private val radiusOfImageBackground =
        itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_size)

    fun bind(item: NewsItem) {

        newsTitle.text = item.title
        newsContent.text = item.content
        isExistKeywordList(item.keywordList)

        Glide.with(itemView)
            .load(item.image)
            .transform(CenterCrop(), RoundedCorners(radiusOfImageBackground))
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.fail_to_bring_image)
            .into(newsImage)

        itemView.setOnClickListener {
            newsItemClickListener.newsItemClick(item)
        }

    }

    private fun isExistKeywordList(
        list: List<String>
    ) {
        newsKeyword1.isVisible = list.isNotEmpty()
        newsKeyword2.isVisible = list.isNotEmpty()
        newsKeyword3.isVisible = list.isNotEmpty()

        if (list.isNotEmpty()) {
            newsKeyword1.text = list[0]
            newsKeyword2.text = list[1]
            newsKeyword3.text = list[2]
        }
    }

}