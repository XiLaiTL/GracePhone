package `fun`.vari.gracephone.ui.page.author

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getPoemBy
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*

/**PagingSource*/
class PoemAuthorPagingSource(val author: String, val context: Context) :
    PagingSource<Int, PoemModelWithNum>() {
    override fun getRefreshKey(state: PagingState<Int, PoemModelWithNum>): Int? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PoemModelWithNum> = try {
        val nextPage = params.key ?: 1 //val pageSize = params.loadSize
        val datas = context.getPoemBy(author = author)
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
class PoemAuthorPagingViewModel(author: String, application: Application) :
    AndroidViewModel(application) {
    val datas = Pager(PagingConfig(pageSize = 1)) {
        PoemAuthorPagingSource(author, application.applicationContext)
    }.flow.cachedIn(viewModelScope)
}

class PoemAuthorPagingViewModelFactory(
    private val author: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(PoemAuthorPagingViewModel::class.java)) {
            return PoemAuthorPagingViewModel(author, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
