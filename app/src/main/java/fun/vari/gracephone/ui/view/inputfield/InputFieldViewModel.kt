package `fun`.vari.gracephone.ui.view.inputfield

import `fun`.vari.gracephone.logic.model.database.NoteDataModel
import `fun`.vari.gracephone.logic.model.database.NoteDataViewModel
import `fun`.vari.gracephone.logic.utils.getDateTime
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputViewModel : ViewModel() {
    private val _note: MutableLiveData<String> = MutableLiveData("")
    val note: LiveData<String> = _note

    fun onInputChange(noteValue: String) {
        _note.value = noteValue
    }

    fun insertNote(
        note: String,
        oldNote: NoteDataModel?,
        noteFile: String,
        mNoteDataViewModel: NoteDataViewModel
    ) {
        if (note.isNotEmpty()) {
            if (oldNote != null) {
                oldNote.noteContent = note
                oldNote.noteTime = getDateTime()
                mNoteDataViewModel.update(oldNote)
            } else {
                val newNote = NoteDataModel(
                    noteFile = noteFile,
                    noteContent = note
                )
                mNoteDataViewModel.add(newNote)
            }
        } else {
            if (oldNote != null) {
                mNoteDataViewModel.delete(oldNote)
            }
        }
    }

}