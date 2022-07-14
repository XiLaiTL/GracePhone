package `fun`.vari.gracephone.logic.utils

import org.nlpcn.commons.lang.jianfan.JianFan
import org.nlpcn.commons.lang.pinyin.Pinyin

fun String.toFan(): String = JianFan.j2f(this)
fun String.toJian(): String = JianFan.f2j(this)

enum class PinyinMode { TONE, NONE, UNICODE }

fun String.toPinyin(mode: PinyinMode? = PinyinMode.NONE): String = when (mode) {
    PinyinMode.TONE -> Pinyin.tonePinyin(this).filterNotNull().joinToString(" ")
    PinyinMode.NONE -> Pinyin.pinyin(this).filterNotNull().joinToString(" ")
    PinyinMode.UNICODE -> Pinyin.unicodePinyin(this).filterNotNull().joinToString(" ")
    else -> Pinyin.unicodePinyin(this).filterNotNull().joinToString(" ")
}
