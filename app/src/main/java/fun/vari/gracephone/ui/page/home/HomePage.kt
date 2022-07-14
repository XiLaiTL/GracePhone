package `fun`.vari.gracephone.ui.page.home

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getPoemBy
import `fun`.vari.gracephone.logic.utils.*
import `fun`.vari.gracephone.ui.main.GracePhoneActions
import `fun`.vari.gracephone.ui.page.poem.PoemSinglePage
import `fun`.vari.gracephone.ui.theme.KaiTiFamily
import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.flow
import org.jsoup.nodes.Document

@Composable
fun HomePage(
    actions: GracePhoneActions,
    modifier: Modifier
) {
    val context: Context = LocalContext.current
    val doc: Document by getHtmlDocumentAfterJsLoadFlow("https://shici.store/poetry-calendar/").collectAsState(
        Document("https://shici.store/poetry-calendar/")
    )
      val poemModelWithNum = flow {
          val authorAndDynasty = doc.getElementById("pauthor")?.text()?.split("·")
          val dynasty = if (authorAndDynasty?.get(0) == "唐") "tang"
                        else if (authorAndDynasty?.get(0) == "宋") "song"
                        else null
          val type = if(doc.getElementById("ptitle")?.text()?.contains("·") == true) "ci" else "poet"
          val title = doc.getElementById("ptitle")?.text()?.split("·")?.get(0)?.replace(" ","")
        if(title!=null){
            val poemList = context.getPoemBy(
                type = type,
                title = title,
                author = authorAndDynasty?.get(1),
                dynasty = dynasty,
            )
            Log.d("poem",poemList[0].toJson())
            poemList.add(PoemModelWithNum.none)
            emit(poemList[0])
        }
    }.collectAsState(PoemModelWithNum.none)


    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    )
    {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            model = "https://shici.store/poetry-calendar/${
                doc.getElementById("imageCover")?.getImageFromDiv()
            }",
            contentDescription = ""
            )
        Column(
            modifier = Modifier
                .clickable {
                    actions.enterWeb(
                        "https://www.fanhuangli.com/tungshing/t/${getDateForCalendar()}.html",
                        false
                    )
                }
                .size(380.dp, 175.dp)
                //.offset(y = -50.dp)
                .border(10.dp, Color.Red, MaterialTheme.shapes.medium)
                .padding(20.dp, 25.dp)
                .align(Alignment.CenterHorizontally),//the column self
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally //child

        ) {
            Row(
                //horizontalArrangement= Arrangement.Center,
            ) {
                Text(
                    text = "${getDateZone().year.toString()}年",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(0.dp, 5.dp),
                    color = Color.Red
                    )
                Text(
                    text = "${getDateZone().month.value.toString()}月",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(50.dp, 5.dp),
                    color = Color.Red
                    )
                }
            Row(
                //horizontalArrangement= Arrangement.Center,
                //verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getDateZone().dayOfMonth.toString(),
                    fontFamily = FontFamily.Default,
                    fontSize = 75.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Red
                    )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally //child

                ) {
                    Text(
                        text = stringResource(id = getWeek()),
                        fontFamily = KaiTiFamily,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(15.dp, 5.dp),
                        color = Color.Red
                        )
                    Text(
                        text = doc.getElementById("todayLunar")?.text() ?: getDate(),
                        fontFamily = KaiTiFamily,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(15.dp, 5.dp),
                        color = Color.Red
                        )
                    }
                }
            }
        PoemSinglePage(
            actions = actions,
            modifier = modifier.clickable {
                actions.enterPoem(poemModelWithNum.value)
            },
            mPoemModelWithNum = poemModelWithNum.value,
            )
    }

}

