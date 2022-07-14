package `fun`.vari.gracephone.ui.page.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun rememberWebViewWithLifecycleObserve(webView: WebView): LifecycleEventObserver =
    remember(webView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_DESTROY -> webView.destroy()
                else -> {
                }
            }
        }
    }

@Composable
fun rememberWebViewWithLifecycle(): WebView {
    val context = LocalContext.current
    val webView = remember {
        WebView(context)
    }
    val lifecycleObserver = rememberWebViewWithLifecycleObserve(webView = webView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }
    return webView
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(
    url: String? = null,
    data: String? = null,
    title: String? = "",
    javaScriptEnabled: Boolean = false,
    onBack: () -> Unit? = {}
) {
    val webView = rememberWebViewWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title ?: "") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (webView.canGoBack()) webView.goBack()
                            else onBack.invoke()
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
        content = {
            AndroidView(
                factory = { webView },
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red),
                update = { webView ->
                    val webSettings = webView.settings
                    webSettings.javaScriptEnabled = javaScriptEnabled
                    if (url != null) webView.loadUrl(url)
                    else if (data != null) webView.loadData(
                        data,
                        "text/html;charset=utf-8",
                        "utf-8"
                    )
                }
            )
        }
    )
}