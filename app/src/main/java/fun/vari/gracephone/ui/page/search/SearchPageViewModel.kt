package `fun`.vari.gracephone.ui.page.search

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.translate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** ViewModel 用于设置UI对应操作、封装状态*/
class SearchPageViewModel : ViewModel() {
    var viewStates by mutableStateOf(SearchState())
        private set
    val typeAndDynasty= listOf(
        "唐诗",
        "宋诗",
        "宋词"
    )
    fun on(
        event: SearchEvent,
        type:String?=null, dynasty:String?=null, title:String?=null, author: String?=null,
    ) {
        when (event) {
            is SearchEvent.ChangeType -> { viewStates = viewStates.copy(type = type) }
            is SearchEvent.ChangeDynasty -> { viewStates = viewStates.copy(dynasty = dynasty) }
            is SearchEvent.ChangeTitle -> { viewStates = viewStates.copy(title = title) }
            is SearchEvent.ChangeAuthor -> { viewStates = viewStates.copy(author = author) }
            is SearchEvent.BeginSearch -> {
                val title1 = if(title=="") null else title
                val author1 = if(author=="") null else author
                viewStates=viewStates.copy(type, dynasty, title1, author1)
            }
            is SearchEvent.ShowDropMenu->{viewStates= viewStates.copy(showDropMenu = !viewStates.showDropMenu)}
        }
    }
    fun typeAndDynastyChange(typeAndDynasty:String){
        when(typeAndDynasty){
            "唐诗"->{
                this.on(SearchEvent.ChangeDynasty,dynasty = "tang")
                this.on(SearchEvent.ChangeType,type = "poet")
            }
            "宋诗"->{
                this.on(SearchEvent.ChangeDynasty,dynasty = "song")
                this.on(SearchEvent.ChangeType,type = "poet")
            }
            "宋词"->{
                this.on(SearchEvent.ChangeDynasty,dynasty = "song")
                this.on(SearchEvent.ChangeType,type = "ci")
            }
        }
    }

}

/** ViewState 用于存储UI中所有的量*/
data class SearchState(
    var type:String?=null,
    var dynasty:String?=null,
    var title:String?=null,
    var author:String?=null,
    var showDropMenu:Boolean=false
)

/** ViewEvent 用于封装用户各种操作意图*/
sealed class SearchEvent {
    object ChangeType : SearchEvent()
    object ChangeDynasty : SearchEvent()
    object ChangeTitle : SearchEvent()
    object ChangeAuthor : SearchEvent()
    object BeginSearch:SearchEvent()
    object ShowDropMenu:SearchEvent()

}