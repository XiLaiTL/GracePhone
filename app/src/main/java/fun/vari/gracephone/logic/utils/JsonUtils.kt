package `fun`.vari.gracephone.logic.utils

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/***
 * 获得泛型的类型 Get the type from generic type
 * Usage: val type = object : TypeLiteral<T>() {}.type
 */
open class TypeLiteral<T> {
    val type: Type
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}

fun Context.getJson(filename: String): String = this.assets.open(filename).use {
    it.bufferedReader(Charsets.UTF_8).use { it.readText() }
}
//fun Context.getJson(filename:String):String= BufferedReader(InputStreamReader(this.assets.open(filename))).use{ it.readText() }

fun Parcelable.toJson(): String = Gson().toJson(this)
inline fun <reified T> String.fromJson2List() = this.fromJson<List<T>>()

inline fun <reified T> String.fromJson(): T? = try { Gson().fromJson(this, (object : TypeToken<T>() {}.type)) } catch (e: Exception) { null }
//  try{ Gson().fromJson(this, T::class.java)} catch (e: Exception) {null}