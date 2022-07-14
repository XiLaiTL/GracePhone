package `fun`.vari.gracephone.ui.page.poem

import `fun`.vari.gracephone.logic.model.PoemModelWithNum
import `fun`.vari.gracephone.logic.model.getRandomPoem
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*

/**PagingSource*/
class PoemPagingSource(
    val type: String? = null,
    val dynasty: String? = null,
    val context: Context
) : PagingSource<Int, PoemModelWithNum>() {
    override fun getRefreshKey(state: PagingState<Int, PoemModelWithNum>): Int? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PoemModelWithNum> = try {
        val nextPage = params.key ?: 1 //val pageSize = params.loadSize
        val datas = listOf(context.getRandomPoem(type = type, dynasty = dynasty))
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
class PoemPagingViewModel(type: String? = null, dynasty: String? = null, application: Application) :
    AndroidViewModel(application) {
    val datas = Pager(PagingConfig(pageSize = 1)) {
        PoemPagingSource(type, dynasty, application.applicationContext)
    }.flow.cachedIn(viewModelScope)
}

class PoemPagingViewModelFactory(
    private val type: String? = null,
    private val dynasty: String? = null,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(PoemPagingViewModel::class.java)) {
            return PoemPagingViewModel(type, dynasty, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
