package `fun`.vari.gracephone.ui.page.author

import `fun`.vari.gracephone.logic.model.AuthorModel
import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getAuthorByNameFlow
import `fun`.vari.gracephone.ui.main.GracePhoneActions
import `fun`.vari.gracephone.ui.theme.HeiTiFamily
import `fun`.vari.gracephone.ui.theme.KaiTiFamily
import `fun`.vari.gracephone.ui.view.SwipeRefreshListView
import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed

@Composable
fun PoemAuthorPage(
    author: String,
    actions: GracePhoneActions
) {
    val context = LocalContext.current
    val authorModel = context.getAuthorByNameFlow(author).collectAsState(initial = AuthorModel.init)
    Column() {
        Column(
            modifier = Modifier
                .height(260.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = authorModel.value.name,
                    fontSize = 30.sp
                )
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = authorModel.value.dynasty,
                    fontSize = 30.sp
                )
                }
            Text(text = authorModel.value.desc)
            }
        PoemAuthorListView(author = author, actions = actions)
        }

}


@Composable
fun PoemAuthorListView(
    author: String,
    actions: GracePhoneActions
) {
    val viewModel: PoemAuthorPagingViewModel = viewModel(
        factory = PoemAuthorPagingViewModelFactory(
            author = author,
            application = LocalContext.current.applicationContext as Application
        )
    )
    val collectAsLazyPagingItems = viewModel.datas.collectAsLazyPagingItems()
    SwipeRefreshListView(collectAsLazyPagingItems) {
        itemsIndexed(collectAsLazyPagingItems) { index, value ->
            val title = if (value?.dynasty == "tang") value?.poemModel?.title
            else value?.poemModel?.rhythmic
            Column(
                modifier = Modifier.clickable {
                    actions.enterPoem(value ?: PoemModelWithNum.init)
                }
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = title ?: "",
                    fontSize = 25.sp,
                    fontFamily = HeiTiFamily
                    )
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = value?.poemModel?.paragraphs?.get(0) ?: "",
                    fontSize = 20.sp,
                    fontFamily = KaiTiFamily
                    )
                Divider()
            }
        }
    }
}
