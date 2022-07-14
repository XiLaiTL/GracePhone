package `fun`.vari.gracephone.logic.utils

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("GracePhoneSettings")

//val Context.DataStore: DataStoreUtils
//    get() = run{DataStoreUtils.init(this);DataStoreUtils}

//object DataStoreUtils{
class DataStoreUtils(context: Context) {
//     lateinit var dataStore: DataStore<Preferences>
//     fun init(context: Context){this.dataStore = context.dataStore}
    var dataStore = context.dataStore
    suspend fun <T> put(key: String, value: T) {
        dataStore.edit {
            when (value) {
                is Int -> it[intPreferencesKey(key)] = value
                is Long -> it[longPreferencesKey(key)] = value
                is Double -> it[doublePreferencesKey(key)] = value
                is Float -> it[floatPreferencesKey(key)] = value
                is Boolean -> it[booleanPreferencesKey(key)] = value
                is String -> it[stringPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("DataStore Type Error")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T): Flow<T> {
        return dataStore.data.map {
            suspend fun default(): T {
                withContext(Dispatchers.IO) { put(key, defaultValue) }
                return defaultValue
            }
            when (defaultValue) {
                is Int -> it[intPreferencesKey(key)] ?: default()
                is Long -> it[longPreferencesKey(key)] ?: default()
                is Double -> it[doublePreferencesKey(key)] ?: default()
                is Float -> it[floatPreferencesKey(key)] ?: default()
                is Boolean -> it[booleanPreferencesKey(key)] ?: default()
                is String -> it[stringPreferencesKey(key)] ?: default()
                else -> throw IllegalArgumentException("DataStore Type Error")
            }
        } as Flow<T>
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> get(key: String): Flow<T> {
        return dataStore.data.map {
            when (T::class) {
                Int::class -> it[intPreferencesKey(key)] ?: 0
                Long::class -> it[longPreferencesKey(key)] ?: 0L
                Double::class -> it[doublePreferencesKey(key)] ?: 0.0
                Float::class -> it[floatPreferencesKey(key)] ?: 0f
                Boolean::class -> it[booleanPreferencesKey(key)] ?: false
                String::class -> it[stringPreferencesKey(key)] ?: ""
                else -> throw IllegalArgumentException("DataStore Type Error")
            }
        } as Flow<T>
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }


}


class DataStoreViewModel(application: Application) : AndroidViewModel(application) {
    val dataStoreUtils = DataStoreUtils(application.applicationContext)
    fun <T> put(key: String, value: T) = viewModelScope.launch(Dispatchers.IO) { dataStoreUtils.put(key, value) }
    fun <T> get(key: String, defaultValue: T): Flow<T> = dataStoreUtils.get(key, defaultValue)
    inline fun <reified T : Any> get(key: String): Flow<T> = dataStoreUtils.get(key)
    fun clearN() = viewModelScope.launch(Dispatchers.IO) { dataStoreUtils.clear() }

}

class DataStoreViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
            return DataStoreViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

val Context.DataStore: DataStoreUtils
    get() = DataStoreUtils(this)





