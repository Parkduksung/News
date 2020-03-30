package com.work.news.view.news.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.work.news.data.model.NewsItem
import com.work.news.view.news.main.adapter.listener.NewsItemClickListener
import com.work.news.view.news.main.adapter.viewholder.NewsViewHolder

class NewsAdapter(private val newsItemClickListener: NewsItemClickListener) :
    RecyclerView.Adapter<NewsViewHolder>() {

    private var newsList = mutableListOf<NewsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(parent, newsItemClickListener)

    override fun getItemCount(): Int =
        newsList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(newsList[position])


    fun addData(newsItem: NewsItem) {
        newsList.add(newsItem)
        notifyItemInserted(newsList.lastIndex)
    }

    fun clearListData() {
        newsList.clear()
        notifyDataSetChanged()
    }

}

