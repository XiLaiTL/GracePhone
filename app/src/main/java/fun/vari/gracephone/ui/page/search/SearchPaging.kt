package `fun`.vari.gracephone.ui.page.search

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getPoemBy
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*

class SearchPagingSource(val type:String?,val dynasty:String?,val title:String?,val author: String?, val context: Context) :
    PagingSource<Int, PoemModelWithNum>() {
    override fun getRefreshKey(state: PagingState<Int, PoemModelWithNum>): Int? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PoemModelWithNum> = try {
        val nextPage = params.key ?: 1 //val pageSize = params.loadSize
        val datas = context.getPoemBy(type, dynasty, author,title)
        Log.d("poem",datas.toString())
        LoadResult.Page(
            data = datas,
            prevKey = if (nextPage == 1) null else nextPage - 1,
            nextKey = if (nextPage < 0) nextPage + 1 else null //分页内容是data的流内容，每次分页结束，都会取一次data流。0页使得每次只获取一个流从而不会一直获取
        )

    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}

/**ViewModel*/
class SearchPagingViewModel( type:String?, dynasty:String?, title:String?, author: String?, application: Application) :
    AndroidViewModel(application) {
    val datas = Pager(PagingConfig(pageSize = 1)) {
        SearchPagingSource(type,dynasty,title ,author, application.applicationContext)
    }.flow.cachedIn(viewModelScope)
}

class SearchPagingViewModelFactory(
    private val type:String?,
    private val dynasty:String?,
    private val title:String?,
    private val author: String?,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SearchPagingViewModel::class.java)) {
            return SearchPagingViewModel(type,dynasty,title,author, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
