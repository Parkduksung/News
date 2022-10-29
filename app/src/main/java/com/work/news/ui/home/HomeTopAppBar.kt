package com.work.news.ui.home


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.work.news.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.enterAlwaysScrollBehavior(
        topAppBarState
    )
) {

    val title = stringResource(id = R.string.app_name)

    CenterAlignedTopAppBar(
        title = {

        },
        navigationIcon = {

        },
        actions = {

        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}