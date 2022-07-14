package `fun`.vari.gracephone.ui.main

import `fun`.vari.gracephone.R
import `fun`.vari.gracephone.ui.page.home.HomePage
import `fun`.vari.gracephone.ui.page.note.NotePage
import `fun`.vari.gracephone.ui.page.poem.PoemPage
import `fun`.vari.gracephone.ui.page.search.SearchPage
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

enum class CourseTabs(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    HOME_PAGE(R.string.home, Icons.Filled.Home),
    POEM_PAGE(R.string.poem, Icons.Filled.LibraryBooks),
    NOTE_PAGE(R.string.note, Icons.Filled.StickyNote2),
    SEARCH_PAGE(R.string.search, Icons.Filled.Search)
}

//@ExperimentalPagingApi
@Composable
fun MainPage(
    actions: GracePhoneActions,
    viewModel: MainPageViewModel = viewModel(),
    context: Context
) {
    val position by viewModel.position.observeAsState()
    val tabs = CourseTabs.values()

    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        bottomBar = {
            BottomNavigation {
                tabs.forEachIndexed { index, tab ->
                    BottomNavigationItem(
                        modifier =
                        if (tab == position) Modifier.background(MaterialTheme.colors.primary)
                        else Modifier.background(MaterialTheme.colors.primaryVariant),
                        icon = {
                            if (tab == position) Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colors.secondary
                            )
                            else Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colors.surface
                            )
                        },
                        label = { Text(text = stringResource(id = tab.title).toUpperCase(Locale.ROOT)) },
                        selected = tab == position,
                        onClick = { viewModel.onPositionChanged(tab) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        Crossfade(targetState = position) { screen ->
            when (screen) {
                CourseTabs.HOME_PAGE -> {
                    HomePage(actions = actions, modifier = modifier)
                }
                CourseTabs.POEM_PAGE -> {
                    PoemPage(actions = actions, modifier = modifier)
                }
                CourseTabs.NOTE_PAGE -> {
                    NotePage(actions = actions, modifier = modifier)
                }
                CourseTabs.SEARCH_PAGE -> {
                    SearchPage(actions = actions, modifier = modifier)
                }
            }
        }
    }
}