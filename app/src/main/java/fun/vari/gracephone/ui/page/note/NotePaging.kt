package `fun`.vari.gracephone.ui.page.note

import `fun`.vari.gracephone.logic.model.database.NoteDatabase
import `fun`.vari.gracephone.logic.model.database.NoteRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

/**ViewModel*/
class NotePagingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    init {
        val noteDataDAO = NoteDatabase.getDatabase(application).noteDataDAO()
        repository = NoteRepository(noteDataDAO)
    }

    val datas = Pager(PagingConfig(pageSize = 1)) {
        repository.getNoteDataSource().asPagingSourceFactory().invoke()
    }.flow.cachedIn(viewModelScope)
}

class NotePagingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(NotePagingViewModel::class.java)) {
            return NotePagingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
