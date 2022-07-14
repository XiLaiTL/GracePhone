package `fun`.vari.gracephone.ui.page.poem

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.translate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** ViewModel 用于设置UI对应操作、封装状态*/
class PoemPageViewModel : ViewModel() {
    var viewStates by mutableStateOf(PoemPageState())
        private set
    fun on(
        event: PoemPageEvent,
        getRandomPoem: (() -> PoemModelWithNum)? = { PoemModelWithNum.init },
        mPoemModelWithNum: PoemModelWithNum = viewStates.poemModelWithNum,
    ) {
        when (event) {
            is PoemPageEvent.ShowInputField -> { viewStates = viewStates.copy(showInputField = !viewStates.showInputField) }
            is PoemPageEvent.ChangePoem -> { viewStates = viewStates.copy(poemModelWithNum = mPoemModelWithNum.translate(viewStates.translated)) }
            is PoemPageEvent.Translate -> { viewStates = viewStates.copy(translated = !viewStates.translated) }
            else -> { }
        }
    }

}

/** ViewState 用于存储UI中所有的量*/
data class PoemPageState(
    var showInputField: Boolean = false,
    var translated: Boolean = false,
    var poemModelWithNum: PoemModelWithNum = PoemModelWithNum.init
)

/** ViewEvent 用于封装用户各种操作意图*/
sealed class PoemPageEvent {
    object ShowInputField : PoemPageEvent()
    object ChangePoem : PoemPageEvent()
    object Translate : PoemPageEvent()
}