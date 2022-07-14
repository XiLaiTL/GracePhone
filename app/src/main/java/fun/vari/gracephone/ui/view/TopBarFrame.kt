package `fun`.vari.gracephone.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun TopBarFrame(
    title:String?="",
    onBack:()->Unit,
    content: @Composable (PaddingValues) -> Unit
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = title?:"") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
        content = content
    )
}