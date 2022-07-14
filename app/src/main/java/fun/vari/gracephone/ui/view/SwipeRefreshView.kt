package `fun`.vari.gracephone.ui.view

import `fun`.vari.gracephone.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**下拉加载
 * collectAsLazyPagingItems: LazyPagingItems<T>：LazyPagingItems包装的请求结果存储在ViewModel,从ViewModel获取
 * listContent: LazyListScope.() -> Unit：列表内容 listContent 外部传入需要携带上下文LazyListScope，可复用
 * Usage:
SwipeRefreshList(collectAsLazyPagingItems){
items(collectAsLazyPagingItems) {it?.let {
Column {
Text(text = it.title, style = MaterialTheme.typography.h6)
Text(text = it.content)
Divider()
}
}}
or
itemsIndexed(collectAsLazyPagingIDataList) { index, data ->// 列表Item,对应的实体数据是data
Column {
xxx
}
}
}
 * */
@Composable
fun <T : Any> SwipeRefreshListView(
    collectAsLazyPagingItems: LazyPagingItems<T>,
    listContent: LazyListScope.() -> Unit,
) {
    val rememberSwipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    SwipeRefresh(
        state = rememberSwipeRefreshState,
        onRefresh = { collectAsLazyPagingItems.refresh() }
    ) {
        rememberSwipeRefreshState.isRefreshing =
            collectAsLazyPagingItems.loadState.refresh is LoadState.Loading
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            listContent()
            collectAsLazyPagingItems.apply {
                when {
                    loadState.append is LoadState.Loading -> { //加载更多，底部显示loading
                        item { LoadingItem() }
                    }
                    loadState.append is LoadState.Error -> { //加载更多时出错，底部显示异常
                        item { ErrorRetryItem() { collectAsLazyPagingItems.retry() } }
                    }
                    loadState.refresh is LoadState.Error -> {
                        if (collectAsLazyPagingItems.itemCount <= 0) { //刷新的时候，如果itemCount小于0，说明是第一次进来，出错了显示一个大的错误内容
                            item { ErrorContent() { collectAsLazyPagingItems.retry() } }
                        } else {
                            item { ErrorRetryItem() { collectAsLazyPagingItems.retry() } }
                        }
                    }
                    loadState.refresh is LoadState.Loading -> { // 第一次加载且正在加载中
                        if (collectAsLazyPagingItems.itemCount == 0) {
                        }
                    }
                }
            }

        }
    }
}

/**页面加载失败处理*/
@Composable
fun ErrorRetryItem(retry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier.size(35.dp),
            onClick = { retry() }
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colors.surface
                )
            }
        }
}

/**底部加载更多失败处理*/
@Composable
fun ErrorContent(retry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "请求出错啦",
            fontSize = 35.sp
            )
        IconButton(
            modifier = Modifier.size(35.dp),
            onClick = { retry() }
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colors.surface
                )
            }
        }
}

/**底部加载更多,正在加载中...*/
@Composable
fun LoadingItem() {
    CircularProgressIndicator(modifier = Modifier.padding(10.dp))
}

