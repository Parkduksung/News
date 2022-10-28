package com.work.news.ui.home

import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.work.news.R

@Composable
fun BookmarkButton(
    isBookmark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val clickLabel = stringResource(
        if (isBookmark) R.string.unbookmark else R.string.bookmark
    )

    IconToggleButton(
        checked = isBookmark,
        onCheckedChange = { onClick() },
        modifier = modifier.semantics {
            // Use a custom click label that accessibility services can communicate to the user.
            // We only want to override the label, not the actual action, so for the action we pass null.
            this.onClick(label = clickLabel, action = null)
        }
    ) {

        Icon(
            imageVector = if (isBookmark) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun previewBookmarkBookmarkedButton(){
    Surface {
        BookmarkButton(isBookmark = true, onClick = {  })
    }
}

@Preview
@Composable
fun previewBookmarkButton(){
    Surface {
        BookmarkButton(isBookmark = false, onClick = {  })
    }
}