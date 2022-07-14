package `fun`.vari.gracephone.ui.view.inputfield

import `fun`.vari.gracephone.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel

/**输入区域小部件*/
@Composable
fun InputField(
    text: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    label: String = "input",
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onChange,
        modifier = modifier,
        singleLine = singleLine,
        label = {
            Text(text = label)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = MaterialTheme.colors.surface,
            unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
            focusedBorderColor = MaterialTheme.colors.surface,
            focusedLabelColor = MaterialTheme.colors.surface
        ),
        keyboardActions = keyboardActions,
        )
}

/**输入区域*/
@Composable
fun InputFieldView(
    label: String,
    inputViewModel: InputViewModel = viewModel(),
) {
    val focusManager = LocalFocusManager.current
    val note: String by inputViewModel.note.observeAsState("")
    Column() {
        InputField(
            text = note,
            onChange = { inputViewModel.onInputChange(it) },
            label = label,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            )
        }
}



