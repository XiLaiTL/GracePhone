package `fun`.vari.gracephone

import `fun`.vari.gracephone.logic.model.database.NoteDataDAO
import `fun`.vari.gracephone.logic.model.database.NoteDataModel
import `fun`.vari.gracephone.logic.model.database.NoteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDataBaseTest {
    private lateinit var noteDao: NoteDataDAO
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        noteDao = db.noteDataDAO()
    }

    @After
    @Throws(IOException::class)
    fun deleteDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() = runBlocking {
        val note=NoteDataModel(noteFile = "shi/tang/40/440",noteContent = "1234")
        noteDao.insert(note)
        val oneItem = noteDao.getByFile("shi/tang/40/440")
        Assert.assertEquals(oneItem.value?.get(0),"1234")
    }
}