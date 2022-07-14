package `fun`.vari.gracephone.logic.utils


import android.annotation.SuppressLint
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


suspend fun getHtmlDocument(url: String): Document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }
fun getHtmlDocumentFlow(url: String): Flow<Document> = flow { emit(getHtmlDocument(url)) }

/**不带缩进*/
suspend fun getHtml(url: String): String = getHtmlDocument(url).outerHtml()
fun getHtmlFlow(url: String): Flow<String> = flow { emit(getHtml(url)) }

/**带缩进*/
suspend fun getRawHtml(url: String): String = withContext(Dispatchers.IO) { Jsoup.connect(url).method(Connection.Method.GET).execute().body() }
fun getRawHtmlFlow(url: String): Flow<String> = flow { emit(getRawHtml(url)) }

fun String.codeURL(): String = this.replace('/', '|').replace(':', '~')
fun String.decodeURL(): String = this.replace('|', '/').replace('~', ':')

@SuppressLint("SetJavaScriptEnabled")
suspend fun getHtmlDocumentAfterJsLoadXml(url: String): String = withContext(Dispatchers.IO) {
    val webClient = WebClient(BrowserVersion.CHROME).apply {
        options.isJavaScriptEnabled = true
        options.isCssEnabled = false
        options.isThrowExceptionOnScriptError = false
        options.isThrowExceptionOnFailingStatusCode = false
        options.timeout = 30000
    }
    val htmlPage: HtmlPage = webClient.getPage(url)
    webClient.waitForBackgroundJavaScript(30000)

    htmlPage.asXml()
}

suspend fun getHtmlDocumentAfterJsLoad(url: String): Document = withContext(Dispatchers.IO) { Jsoup.parse(getHtmlDocumentAfterJsLoadXml(url), url) }
fun getHtmlDocumentAfterJsLoadFlow(url: String): Flow<Document> = flow { emit(getHtmlDocumentAfterJsLoad(url)) }


fun String.substring(startStr: String = "", endStr: String = "") = this.substring(
    if (startStr.isEmpty()) 0 else this.indexOf(startStr) + startStr.length,
    if (endStr.isEmpty()) this.length else this.indexOf(endStr)
)


fun Element.getImageFromDiv() = this.attr("style").substring("url('", "');")

