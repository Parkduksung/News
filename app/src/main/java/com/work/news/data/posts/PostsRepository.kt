package com.work.news.data.posts

import com.work.news.model.Post
import com.work.news.model.PostsFeed
import kotlinx.coroutines.flow.Flow
import com.work.news.data.Result


/**
 * Interface to the Posts data layer.
 */
interface PostsRepository {

    /**
     * Get a specific JetNews post.
     */
    suspend fun getPost(postId: String?): Result<Post>

    /**
     * Get JetNews posts.
     */
    suspend fun getPostsFeed(): Result<PostsFeed>

    /**
     * Observe the current favorites
     */
    fun observeFavorites(): Flow<Set<String>>

    /**
     * Toggle a postId to be a favorite or not.
     */
    suspend fun toggleFavorite(postId: String)
}
