package `fun`.vari.gracephone.ui.main

import `fun`.vari.gracephone.logic.model.FileModel
import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.utils.codeURL
import `fun`.vari.gracephone.logic.utils.decodeURL
import `fun`.vari.gracephone.logic.utils.fromJson
import `fun`.vari.gracephone.logic.utils.toJson
import `fun`.vari.gracephone.ui.page.author.PoemAuthorPage
import `fun`.vari.gracephone.ui.page.poem.PoemSinglePage
import `fun`.vari.gracephone.ui.page.search.SearchListView
import `fun`.vari.gracephone.ui.page.webview.WebViewPage
import `fun`.vari.gracephone.ui.view.TopBarFrame
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson

//导航路线的常量
object GracePhoneDestinations {
    const val HOME_PAGE_ROUTE = "home_page_route"
    const val FILE_ROUTE = "file_route"
    const val FILE_ROUTE_URL = "file_route_url"
    const val DICT_ROUTE = "dict_route"//用于作为导航路径名
    const val DICT_ROUTE_URL = "file_route_url" //用于作为导航路径中的变量名
    const val WEB_ROUTE = "web_route"//用于作为导航路径名
    const val WEB_ROUTE_URL = "web_route_url" //用于作为导航路径中的变量名
    const val POEM_ROUTE = "poem_route"//用于作为导航路径名
    const val POEM_ROUTE_URL = "poem_route_url" //用于作为导航路径中的变量名
    const val AUTHOR_ROUTE = "author_route"//用于作为导航路径名
    const val AUTHOR_ROUTE_URL = "author_route_url" //用于作为导航路径中的变量名
    const val SEARCH_ROUTE = "search_route"//用于作为导航路径名
}

//导航换页的操作：跳转
class GracePhoneActions(navController: NavController) {
    val enterDict: (String) -> Unit = { s ->
        navController.navigate("${GracePhoneDestinations.DICT_ROUTE}/${s}")
    }

    //跳转到诗歌搜索
    val enterSearch: (String, String, String, String) -> Unit = { type, dynasty, title, author ->
        navController.navigate("${GracePhoneDestinations.SEARCH_ROUTE}/${type}/${dynasty}/${title}/${author}")
    }


    //false 跳转到链接对应网页
    val enterWeb: (String, Boolean) -> Unit = { url, b ->
        val codeUrl = if (b) url else url.codeURL()
        navController.navigate("${GracePhoneDestinations.WEB_ROUTE}/${b}/${codeUrl}")
    }

    //跳转到诗歌作者
    val enterAuthor: (String) -> Unit = { s ->
        navController.navigate("${GracePhoneDestinations.AUTHOR_ROUTE}/${s}")
    }

    //跳转到诗歌
    val enterPoem: (PoemModelWithNum) -> Unit = { pm ->
        navController.navigate("${GracePhoneDestinations.POEM_ROUTE}/${pm.toJson()}")
    }

    //根据传入对象进行跳转页面
    val enterFile: (FileModel) -> Unit = { file ->
        val getJson = Gson().toJson(file).trim()
        //val getResult = URLEncoder.encode(getJson,"utf-8")
        //navController.navigate("${GracePhoneDestinations.FILE_ROUTE}/${getResult}")
    }

    //回退
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}

//@ExperimentalPagingApi
@Composable
fun NavGraph(
    startDestination: String = GracePhoneDestinations.HOME_PAGE_ROUTE,
    context: Context
) {
    val navController = rememberNavController()
    val actions = remember(navController) { GracePhoneActions(navController = navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(GracePhoneDestinations.HOME_PAGE_ROUTE) {
            MainPage(actions, context = context)
            }
//字典页面
        composable(
            "${GracePhoneDestinations.DICT_ROUTE}/{${GracePhoneDestinations.DICT_ROUTE_URL}}",
            arguments = listOf( navArgument(GracePhoneDestinations.DICT_ROUTE_URL) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val parcelable =
                arguments.getString(GracePhoneDestinations.DICT_ROUTE_URL)//从字符串中截取变量片段的字符串
            WebViewPage(
                "https://www.zdic.net/hans/${parcelable}",
                title = parcelable,
                onBack = actions.upPress
            )
            }
//网页页面
        composable(
            "${GracePhoneDestinations.WEB_ROUTE}/{data}/{${GracePhoneDestinations.WEB_ROUTE_URL}}",
            arguments = listOf(
                navArgument("data") { type = NavType.BoolType },
                navArgument(GracePhoneDestinations.WEB_ROUTE_URL) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val data = arguments.getBoolean("data")

            val parcelable =
                arguments.getString(GracePhoneDestinations.WEB_ROUTE_URL)//从字符串中截取变量片段的字符串
            val decodeParcelable = if (data) parcelable else (parcelable ?: "").decodeURL()
            if (data)
                WebViewPage(data = decodeParcelable, onBack = actions.upPress)
            else
                WebViewPage(decodeParcelable, onBack = actions.upPress)
            }
//诗歌页面
        composable(
            "${GracePhoneDestinations.POEM_ROUTE}/{${GracePhoneDestinations.POEM_ROUTE_URL}}",
            arguments = listOf( navArgument(GracePhoneDestinations.POEM_ROUTE_URL) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val parcelable =
                arguments.getString(GracePhoneDestinations.POEM_ROUTE_URL)//从字符串中截取变量片段的字符串
            val poemModelWithNum = parcelable?.fromJson<PoemModelWithNum>()
            val title =
                when (poemModelWithNum?.type) {
                    "ci" -> "${poemModelWithNum.poemModel.rhythmic} ${poemModelWithNum.poemModel.author}"
                    "poet" -> "${poemModelWithNum.poemModel.title} ${poemModelWithNum.poemModel.author}"
                    else -> ""
                }
            TopBarFrame(title = title, onBack = actions.upPress) {
                    PoemSinglePage(  actions = actions, modifier = Modifier.fillMaxWidth(),mPoemModelWithNum = poemModelWithNum?:PoemModelWithNum.init)
                }
            }
//作者页面
        composable(
            "${GracePhoneDestinations.AUTHOR_ROUTE}/{${GracePhoneDestinations.AUTHOR_ROUTE_URL}}",
            arguments = listOf( navArgument(GracePhoneDestinations.AUTHOR_ROUTE_URL) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val parcelable =
                arguments.getString(GracePhoneDestinations.AUTHOR_ROUTE_URL)//从字符串中截取变量片段的字符串
            TopBarFrame(title = parcelable ?: "", onBack = actions.upPress) {
                PoemAuthorPage(author = parcelable ?: "", actions = actions)
                }
            }
//搜索页面
        composable(
            "${GracePhoneDestinations.SEARCH_ROUTE}/{type}/{dynasty}/{title}/{author}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("dynasty") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("author") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val type = arguments.getString("type")//从字符串中截取变量片段的字符串
            val dynasty = arguments.getString("dynasty")//从字符串中截取变量片段的字符串
            val title = arguments.getString("title")//从字符串中截取变量片段的字符串
            val author = arguments.getString("author")//从字符串中截取变量片段的字符串

            TopBarFrame(title = "搜索结果", onBack = actions.upPress) {
                SearchListView(
                    type = type,
                    dynasty = dynasty,
                    title = if(title=="null") null else title,
                    author = if(author=="null") null else author,
                    actions = actions
                )
            }
        }
//未来的文件页面
        composable(
            "${GracePhoneDestinations.FILE_ROUTE}/{${GracePhoneDestinations.FILE_ROUTE_URL}}",
            arguments = listOf(navArgument(GracePhoneDestinations.FILE_ROUTE_URL) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val parcelable = arguments.getString(GracePhoneDestinations.FILE_ROUTE_URL)
//            val fromJson = Gson().fromJson(parcelable,FileModel::class.java)
//            FilePage(
//                file=fromJson,
//                onBack=actions.upPress
//            )
            }
    }
}