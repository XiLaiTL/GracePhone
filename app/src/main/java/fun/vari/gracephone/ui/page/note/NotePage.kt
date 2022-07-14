package `fun`.vari.gracephone.ui.page.note

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getPoemByNoteFileFlow
import `fun`.vari.gracephone.ui.main.GracePhoneActions
import `fun`.vari.gracephone.ui.theme.KaiTiFamily
import `fun`.vari.gracephone.ui.view.SwipeRefreshListView
import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed

@Composable
fun NotePage(
    actions: GracePhoneActions,
    modifier: Modifier
) {
    NoteListView(modifier = modifier, actions)
}


@Composable
fun NoteListView(
    modifier: Modifier,
    actions: GracePhoneActions
) {
    val viewModel: NotePagingViewModel = viewModel(
        factory = NotePagingViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val collectAsLazyPagingItems =
        viewModel.datas.collectAsLazyPagingItems()
    val context = LocalContext.current

    SwipeRefreshListView(collectAsLazyPagingItems) {
        itemsIndexed(collectAsLazyPagingItems) { index, value ->
            val poemModelWithNum =
                context.getPoemByNoteFileFlow(value?.noteFile ?: "ci/song/9/70").collectAsState(
                    initial = PoemModelWithNum.init
                )
            Column(
                modifier = modifier
                    //.border(7.dp,MaterialTheme.colors.primaryVariant,MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .clickable {
                        actions.enterPoem(poemModelWithNum.value)
                    }
                    .fillMaxWidth(),
                //horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Divider()
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = value?.noteTime?.substring(0, 10) ?: "",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                    )
//                Text(
//                    modifier=Modifier.padding(10.dp),
//                    text = value?.noteFile?:"",
//                    fontSize = 25.sp,
//                    fontWeight = FontWeight.Normal
//                )
                if (poemModelWithNum.value.type == "poet")
                    Text(
                        modifier = Modifier.padding(10.dp, 0.dp),
                        text = poemModelWithNum.value.poemModel.title,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = KaiTiFamily
                        )
                else
                    Text(
                        modifier = Modifier.padding(10.dp, 0.dp),
                        text = poemModelWithNum.value.poemModel.rhythmic,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = KaiTiFamily
                        )
                Text(
                    modifier = Modifier.padding(10.dp, 0.dp),
                    text = poemModelWithNum.value.poemModel.author,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = KaiTiFamily
                    )
                Text(
                    modifier = Modifier.padding(10.dp, 10.dp),
                    text = value?.noteContent ?: "",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Normal
                    )
                Divider()
                }
            }
        }
}