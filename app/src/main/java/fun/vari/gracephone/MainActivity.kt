package `fun`.vari.gracephone

import `fun`.vari.gracephone.ui.main.NavGraph
import `fun`.vari.gracephone.ui.theme.GracePhoneTheme
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.ProvideWindowInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GracePhoneTheme {
                ProvideWindowInsets {
                    NavGraph(context = applicationContext)
                }
            }
        }
    }
}


//测试Composable组件
@Composable
fun Greeting(context: Context, name: String) {
    Column {
        val text1 = "点击吧"
        var is_click = rememberSaveable {
            mutableStateOf(false)
        }
        Text(text = "Hello $name!")
        Text(text = stringResource(id = R.string.flower))
        Button(onClick = {
            is_click.value = !is_click.value
        }) {}
        if (is_click.value) {
            Text(text = "click after")
        }
        ClickableText(
            text = AnnotatedString(text1),
            onClick = { offset ->
                Log.d("Main", "$offset")
                val text_get = text1[if (offset == text1.length) offset - 1 else offset];
                Log.d("Main", "${text_get}")
            })
        val annotatedText = buildAnnotatedString {
            append("Click")
            pushStringAnnotation(
                tag = "URL",
                annotation = "https://developer.android.com"
            )
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Url")
            }
            pop()
        }

        ClickableText(text = annotatedText, onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                Log.d("Main", annotation.item)
//                var getone: PoemListModel? =context.getJson("poet.tang.0.json").fromJson<PoemListModel>()
//                Log.d("Main", getone?.get(0)?.paragraphs .toString().toPinyin(PinyinMode.UNICODE) )
//                Log.d("Main", getone?.get(0)?.paragraphs .toString().toJian() )
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GracePhoneTheme {
        //Greeting("Android")
    }
}

