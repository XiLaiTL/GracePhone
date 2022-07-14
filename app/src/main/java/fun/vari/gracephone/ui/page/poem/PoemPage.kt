package `fun`.vari.gracephone.ui.page.poem

import `fun`.vari.gracephone.R
import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.database.NoteDataViewModel
import `fun`.vari.gracephone.logic.model.database.NoteDataViewModelFactory
import `fun`.vari.gracephone.ui.main.GracePhoneActions
import `fun`.vari.gracephone.ui.theme.HeiTiFamily
import `fun`.vari.gracephone.ui.theme.KaiTiFamily
import `fun`.vari.gracephone.ui.view.SwipeRefreshListView
import `fun`.vari.gracephone.ui.view.TextColumn
import `fun`.vari.gracephone.ui.view.inputfield.InputFieldView
import `fun`.vari.gracephone.ui.view.inputfield.InputViewModel
import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed

@Composable
fun PoemSinglePage(
    actions: GracePhoneActions,
    modifier: Modifier,
    mPoemModelWithNum: PoemModelWithNum? = PoemModelWithNum.init,
    mPoemPageViewModel: PoemPageViewModel = viewModel()
) {
    val mNoteDataViewModel: NoteDataViewModel = viewModel(
        factory = NoteDataViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    Column(
        modifier
            .fillMaxWidth()
            .height(1000.dp)
            .verticalScroll(rememberScrollState())

    ) {
        mPoemPageViewModel.on(
            PoemPageEvent.ChangePoem,
            mPoemModelWithNum = mPoemModelWithNum ?: PoemModelWithNum.init
        )
        PoemView(
            actions = actions,
            mPoemPageViewModel = mPoemPageViewModel,
            mNoteDataViewModel = mNoteDataViewModel
            )
        }
}

//TODO :Settings of Random
@Composable
fun PoemPage(
    actions: GracePhoneActions,
    modifier: Modifier,
    mPoemPageViewModel: PoemPageViewModel = viewModel()
) {
    val mNoteDataViewModel: NoteDataViewModel = viewModel(
        factory = NoteDataViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        PoemListView(
            mPoemPageViewModel = mPoemPageViewModel,
            mNoteDataViewModel = mNoteDataViewModel,
            actions = actions
            )
    }

}

@Composable
fun PoemListView(
    type: String? = null,
    dynasty: String? = null,
    mPoemPageViewModel: PoemPageViewModel = viewModel(),
    mNoteDataViewModel: NoteDataViewModel = viewModel(),
    actions: GracePhoneActions
) {
    val poemPagingViewModel: PoemPagingViewModel = viewModel(
        factory = PoemPagingViewModelFactory(
            type,
            dynasty,
            LocalContext.current.applicationContext as Application
        )
    )
    val collectAsLazyPagingItems = poemPagingViewModel.datas.collectAsLazyPagingItems()
    SwipeRefreshListView(collectAsLazyPagingItems) {
        itemsIndexed(collectAsLazyPagingItems) { index, value ->
            mPoemPageViewModel.on(
                PoemPageEvent.ChangePoem,
                mPoemModelWithNum = value ?: PoemModelWithNum.init
            )
            PoemView(
                mPoemPageViewModel = mPoemPageViewModel,
                mNoteDataViewModel = mNoteDataViewModel,
                actions = actions
                )

        }
        }

}


@Composable
fun PoemView(
    mPoemPageViewModel: PoemPageViewModel = viewModel(),
    mNoteDataViewModel: NoteDataViewModel = viewModel(),
    actions: GracePhoneActions
) {
    val inputViewModel: InputViewModel = viewModel()

    var poemModelWithNum = mPoemPageViewModel.viewStates.poemModelWithNum


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(20.dp, 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = Modifier.size(35.dp),
                onClick = {
                    mPoemPageViewModel.on(PoemPageEvent.ShowInputField)
                }
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.Filled.NoteAdd,
                    contentDescription = "Add note",
                    tint = MaterialTheme.colors.surface
                    )
                }
            IconButton(
                modifier = Modifier.size(35.dp),
                onClick = {
                    mPoemPageViewModel.on(PoemPageEvent.Translate)
                }
                ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.Filled.Translate,
                    contentDescription = "Translate",
                    tint = MaterialTheme.colors.surface
                    )
            }
            if (mPoemPageViewModel.viewStates.showInputField) {
                val noteFile =
                    "${poemModelWithNum.type}/${poemModelWithNum.dynasty}/${poemModelWithNum.fileNum}/${poemModelWithNum.listNum}"
                val oldNote = mNoteDataViewModel.getNoteByFile(noteFile).observeAsState().value
                val noteContent = oldNote?.noteContent ?: " "
                inputViewModel.onInputChange(noteContent)
                IconButton(
                    modifier = Modifier.size(35.dp),
                    onClick = {
                        inputViewModel.insertNote(
                            note = inputViewModel.note.value ?: noteContent,
                            oldNote = oldNote,
                            noteFile = noteFile,
                            mNoteDataViewModel = mNoteDataViewModel
                        )
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                        tint = MaterialTheme.colors.surface
                        )
                    }

            }
        }

        when (poemModelWithNum.type) {
            "ci" -> CiView(mPoemPageViewModel = mPoemPageViewModel, actions = actions)
            "poet" -> PoetView(mPoemPageViewModel = mPoemPageViewModel, actions = actions)
        }
        if (mPoemPageViewModel.viewStates.showInputField) {
            InputFieldView(
                label= stringResource(R.string.input),
                inputViewModel = inputViewModel
                )
        }
        }
}


@Composable
fun PoetView(mPoemPageViewModel: PoemPageViewModel = viewModel(), actions: GracePhoneActions) {
    var poemModelWithNum = mPoemPageViewModel.viewStates.poemModelWithNum
    Row(modifier = Modifier
        .padding(15.dp, 0.dp)
        .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.size(width = 260.dp, height = Dp.Unspecified)
        ) {
            poemModelWithNum.poemModel.paragraphs.forEach { sentence ->
                if (sentence.contains('，')) {
                    val list= sentence.split("，")
                    list.forEachIndexed { index, sentence1 ->
                        val sentence11 = if (index!=list.size-1) "$sentence1，"  else sentence1
                        ClickableText(
                            text = AnnotatedString(sentence11),
                            onClick = { offset ->
                                val text_get =
                                    sentence11[if (offset == sentence11.length) offset - 1 else offset];
                                actions.enterDict(text_get.toString())
                            },
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontFamily = KaiTiFamily
                            )
                        )
                    }
                } else {
                    ClickableText(
                        text = AnnotatedString(sentence),
                        onClick = { offset ->
                            val text_get =
                                sentence[if (offset == sentence.length) offset - 1 else offset];
                            actions.enterDict(text_get.toString())
                        },
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = KaiTiFamily
                        )
                        )
                }
            }
            }

        TextColumn(
            modifier = Modifier.clickable {
                actions.enterAuthor(poemModelWithNum.poemModel.author)
            },
            text = poemModelWithNum.poemModel.author,
            fontSize = 27.sp
            )
        TextColumn(
            text = poemModelWithNum.poemModel.title,
            fontSize = 35.sp,
            fontFamily = HeiTiFamily
            )

        }
}

@Composable
fun CiView(mPoemPageViewModel: PoemPageViewModel = viewModel(), actions: GracePhoneActions) {
    var poemModelWithNum = mPoemPageViewModel.viewStates.poemModelWithNum
    Row(modifier = Modifier
        .padding(15.dp, 0.dp)
        .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.size(width = 260.dp, height = Dp.Unspecified)
        ) {
            poemModelWithNum.poemModel.paragraphs.forEach { sentence ->
                ClickableText(
                    text = AnnotatedString(sentence),
                    onClick = { offset ->
                        val text_get =
                            sentence[if (offset == sentence.length) offset - 1 else offset];
                        actions.enterDict(text_get.toString())
                    },
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = KaiTiFamily
                    )
                    )

                }
            }
        TextColumn(
            modifier = Modifier.clickable {
                actions.enterAuthor(poemModelWithNum.poemModel.author)
            },
            text = poemModelWithNum.poemModel.author,
            fontSize = 27.sp
            )

        TextColumn(
            modifier = Modifier.clickable {
                actions.enterWeb(
                    "https://www.52shici.com/zd/pu.php?name=${poemModelWithNum.poemModel.rhythmic}",
                    false
                )
            },
            text = poemModelWithNum.poemModel.rhythmic,
            fontSize = 35.sp,
            fontFamily = HeiTiFamily
            )


        }
}



