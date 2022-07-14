package `fun`.vari.gracephone.logic.model.database

import `fun`.vari.gracephone.logic.utils.getDateTime
import android.app.Application
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Entity 数据实体
 * DAO 基于数据实体的数据库基本操作
 * Repository 封装 DAO 的操作
 * DataBase 基于Entity的数据库，提供DAO的实例
 * ViewModel 引入Repository
 * */

/*Entity 数据实体*/
@Parcelize
@Entity(tableName = "note_data")
data class NoteDataModel(

    @ColumnInfo(name  = "note_time")
    var noteTime:String = getDateTime(),

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name  = "note_file")
    val noteFile:String,

    @ColumnInfo(name  = "note_content")
    var noteContent:String
):Parcelable

/*DAO 基于数据实体的数据库基本操作*/
@Dao
interface NoteDataDAO{
    @Query("SELECT * FROM note_data ORDER BY note_time ASC")
    fun getAll(): LiveData<List<NoteDataModel>>

    @Query("SELECT * FROM note_data ORDER BY note_time COLLATE NOCASE ASC")
    fun getAllNote(): DataSource.Factory<Int, NoteDataModel>

    @Query("SELECT * from note_data where note_file = :noteFile")
    fun getByFile(noteFile:String): LiveData<NoteDataModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: NoteDataModel)

    @Update
    suspend fun update(note: NoteDataModel)

    @Delete
    suspend fun delete(note: NoteDataModel)

    @Query("DELETE FROM note_data")
    suspend fun deleteAll()

}

/*Repository 封装 DAO 的操作*/
class NoteRepository(private val noteDataDAO: NoteDataDAO){
    val getAll:LiveData<List<NoteDataModel>> = noteDataDAO.getAll()
    fun getNoteByFile(file_name:String):LiveData<NoteDataModel> = noteDataDAO.getByFile(file_name)
    fun getNoteDataSource(): DataSource.Factory<Int, NoteDataModel> = noteDataDAO.getAllNote()
    suspend fun add(note: NoteDataModel){ noteDataDAO.insert(note) }
    suspend fun update(note: NoteDataModel){ noteDataDAO.update(note) }
    suspend fun delete(note: NoteDataModel){ noteDataDAO.delete(note) }
    suspend fun deleteAll(){ noteDataDAO.deleteAll() }

}

/* DataBase 基于Entity的数据库，提供DAO的实例*/
@Database(entities = [NoteDataModel::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDataDAO(): NoteDataDAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}

class NoteDataViewModel(application: Application):AndroidViewModel(application){

    private val repository:NoteRepository
    val getAll: LiveData<List<NoteDataModel>>
    init {
        val noteDataDAO=NoteDatabase.getDatabase(application).noteDataDAO()
        repository= NoteRepository(noteDataDAO )
        getAll = repository.getAll
    }
    fun getNoteByFile(file_name:String) = repository.getNoteByFile(file_name = file_name)
    fun add(note: NoteDataModel) = viewModelScope.launch(Dispatchers.IO) { repository.add(note)  }
    fun update(note: NoteDataModel) = viewModelScope.launch(Dispatchers.IO) { repository.update(note)  }
    fun delete(note: NoteDataModel) = viewModelScope.launch(Dispatchers.IO) { repository.delete(note)  }
    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) { repository.deleteAll()  }

}
class NoteDataViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(NoteDataViewModel::class.java)){
            return NoteDataViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}