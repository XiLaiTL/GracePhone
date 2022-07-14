package `fun`.vari.gracephone.ui.page.search

import `fun`.vari.gracephone.logic.model.AuthorModel
import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getAuthorByNameFlow
import `fun`.vari.gracephone.ui.main.GracePhoneActions
import `fun`.vari.gracephone.ui.theme.HeiTiFamily
import `fun`.vari.gracephone.ui.theme.KaiTiFamily
import `fun`.vari.gracephone.ui.view.SwipeRefreshListView
import `fun`.vari.gracephone.ui.view.inputfield.InputFieldView
import `fun`.vari.gracephone.ui.view.inputfield.InputViewModel
import android.app.Application
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed

@Composable
fun SearchPage(
    //type:String?, dynasty:String?, title:String?, author: String?,
    actions: GracePhoneActions,
    modifier: Modifier,

) {
    val context = LocalContext.current
    val mSearchViewModel:SearchPageViewModel= viewModel()
    val titleInputViewModel:InputViewModel= viewModel(key= "title")
    val authorInputViewModel:InputViewModel= viewModel(key= "author")
    val typeInputViewModel:InputViewModel= viewModel(key= "type")

    Column(modifier=modifier.fillMaxWidth()) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "搜索类型",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 20.sp,
                    fontFamily = HeiTiFamily
                    )
                IconButton(
                    modifier = Modifier.size(35.dp),
                    onClick = {
                        mSearchViewModel.on(SearchEvent.ShowDropMenu)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Filled.ArrowDropDownCircle,
                        contentDescription = "Search",
                        tint = MaterialTheme.colors.surface
                        )
                    }
                InputFieldView(
                    label = "搜索类型",
                    inputViewModel = typeInputViewModel
                )
                if(mSearchViewModel.viewStates.showDropMenu){
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {},
                        modifier = Modifier.width(100.dp)
                    ) {
                        mSearchViewModel.typeAndDynasty.forEach {
                            DropdownMenuItem(onClick = {
                                mSearchViewModel.typeAndDynastyChange(it)
                                mSearchViewModel.on(SearchEvent.ShowDropMenu)
                                typeInputViewModel.onInputChange(it)
                            }) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontSize = 15.sp,
                                    )
                                }
                        }
                        }
                }
                }
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = "标题/词牌名",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontFamily = HeiTiFamily
                    )

                InputFieldView(
                    label = "标题/词牌名",
                    inputViewModel = titleInputViewModel
                    )
                }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "作者",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontFamily = HeiTiFamily
                    )
                InputFieldView(
                    label = "作者" ,
                    inputViewModel =  authorInputViewModel
                    )
                }

        }
        IconButton(
            modifier = Modifier.size(35.dp),
            onClick = {
                mSearchViewModel.on(SearchEvent.BeginSearch,
                    mSearchViewModel.viewStates.type,
                    mSearchViewModel.viewStates.dynasty,
                    titleInputViewModel.note.value,
                    authorInputViewModel.note.value
                )
                actions.enterSearch(
                    mSearchViewModel.viewStates.type?:"null",
                    mSearchViewModel.viewStates.dynasty?:"null",
                    mSearchViewModel.viewStates.title?:"null",
                    mSearchViewModel.viewStates.author?:"null",
                )
            }
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colors.surface
                )
            }
        }
}


@Composable
fun SearchListView(
    type:String?,
    dynasty:String?,
    title:String?,
    author: String?,
    actions: GracePhoneActions
) {
    val viewModel: SearchPagingViewModel = viewModel(
        factory = SearchPagingViewModelFactory(
            type=type,
            dynasty=dynasty,
            title=title,
            author = author,
            application = LocalContext.current.applicationContext as Application
        )
    )
    val collectAsLazyPagingItems = viewModel.datas.collectAsLazyPagingItems()
    SwipeRefreshListView(collectAsLazyPagingItems) {
        itemsIndexed(collectAsLazyPagingItems) { index, value ->
            val title2 = if (value?.dynasty == "tang") value?.poemModel?.title
            else value?.poemModel?.rhythmic
            Column(
                modifier = Modifier.clickable {
                    actions.enterPoem(value ?: PoemModelWithNum.init)
                }
            ) {
                Row() {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = value?.poemModel?.author ?: "",
                        fontSize = 23.sp,
                        fontFamily = HeiTiFamily
                        )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = title2 ?: "",
                        fontSize = 25.sp,
                        fontFamily = HeiTiFamily
                        )
                    }
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
