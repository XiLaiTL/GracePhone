package `fun`.vari.gracephone.logic.model

import `fun`.vari.gracephone.logic.utils.*
import android.content.Context
import android.os.Parcelable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class PoemListModel : ArrayList<PoemModel>()

@Parcelize
data class PoemModel(
    val author: String,
    val id: String,
    val paragraphs: List<String>,
    val tags: List<String> = listOf(""),
    val title: String,
    val rhythmic: String
) : FileModel(), Parcelable

@Parcelize
data class PoemModelWithNum(
    val poemModel: PoemModel,
    val type: String,
    val dynasty: String,
    val fileNum: Int,
    val listNum: Int
) : Parcelable {
    companion object {
        val init = PoemModelWithNum(
            PoemModel(
                author = "陳子昂",
                id = "c244a5b4-0ed0-48fe-8694-95309acac184",
                paragraphs = listOf("前不見古人，後不見來者。", "念天地之悠悠，獨愴然而涕下。"),
                tags = listOf("唐诗三百首", "隋・唐・五代", "八年级下册(课外)", "伤怀", "初中古诗", "七言古诗"),
                title = "登幽州臺歌",
                rhythmic = ""
            ), "poet", "tang", 4, 440
        )
        val none = PoemModelWithNum(
            PoemModel(
                author = "",
                id = "",
                paragraphs = listOf(""),
                tags = listOf(""),
                title = "",
                rhythmic = ""
            ), "poet", "tang", 0, 0
        )
    }
}

fun PoemModelWithNum.translate(mode: String): PoemModelWithNum =
    when (mode) {
        "jian" -> PoemModelWithNum(
            PoemModel(
                this.poemModel.author.toJian(),
                if (this.type == "ci") "" else this.poemModel.id,
                this.poemModel.paragraphs.map { s -> s.toJian() },
                if (this.poemModel.tags == null) listOf("") else this.poemModel.tags,
                if (this.type == "poet") this.poemModel.title.toJian() else "",
                if (this.type == "ci") this.poemModel.rhythmic.toJian() else ""
            ), this.type, this.dynasty, this.fileNum, this.listNum
        )
        "fan" -> PoemModelWithNum(
            poemModel =
            PoemModel(
                this.poemModel.author.toFan(),
                if (this.type == "ci") "" else this.poemModel.id,
                this.poemModel.paragraphs.map { s -> s.toFan() },
                if (this.poemModel.tags == null) listOf("") else this.poemModel.tags,
                if (this.type == "poet") this.poemModel.title.toFan() else "",
                if (this.type == "ci") this.poemModel.rhythmic.toFan() else ""
            ), this.type, this.dynasty, this.fileNum, this.listNum
        )
        else -> PoemModelWithNum.init
    }

fun PoemModelWithNum.translate(mode: Boolean): PoemModelWithNum =
    if (mode) this.translate("jian")
    else this.translate("fan")

fun getPoemRange(type: String, dynasty: String) = when ("${type}/${dynasty}") {
    "ci/song" -> (0..21)
    "poet/tang" -> (0..57)
    "poet/song" -> (0..254)
    else -> (0..0)
}

var randomTimes = 0
suspend fun Context.getRandomPoem(type: String? = null, dynasty: String?): PoemModelWithNum =
    withContext(Dispatchers.IO) {
        if (randomTimes == 0) {//首次启动自动读取上次浏览的诗歌
            randomTimes++
            val poemNowJson =
                this@getRandomPoem.DataStore.get("poem_now", PoemModelWithNum.init.toJson()).first()
            poemNowJson.fromJson<PoemModelWithNum>() ?: PoemModelWithNum.init
        } else {
            val (type1, dynasty1) =
                if (type == null && dynasty == null) {
                    listOf(
                        listOf("ci", "song"),
                        listOf("poet", "song"),
                        listOf("poet", "tang")
                    ).random()
                } else if (type != null && dynasty != null) {
                    listOf(type, dynasty)
                } else if (type == null && dynasty != null) {
                    if (dynasty == "tang") listOf("poet", "tang")
                    else listOf(listOf("ci", "song"), listOf("poet", "song")).random()
                } else {
                    if (type == "ci") listOf("ci", "song")
                    else listOf(listOf("poet", "song"), listOf("poet", "tang")).random()
                }
            randomTimes++
            val randNum1 = getPoemRange(type1, dynasty1).random()
            val poemFile =
                this@getRandomPoem.getJson("chinese_poetry/poetry/${type1}/${dynasty1}/intent/${type1}.${dynasty1}.${randNum1 * 1000}.json")
            val poemListModel: PoemListModel = (poemFile.fromJson<PoemListModel>() ?: arrayListOf(
                PoemModelWithNum.init.poemModel
            )) as PoemListModel
            val randNum2 = (0..999).random()
            val poemNew = PoemModelWithNum(
                poemModel = poemListModel.get(randNum2),
                type = type1,
                dynasty = dynasty1,
                fileNum = randNum1,
                listNum = randNum2
            )
            this@getRandomPoem.DataStore.put("poem_now", poemNew.toJson())
            poemNew
        }
    }

suspend fun Context.getPoemByNum(
    type: String,
    dynasty: String,
    fileNum: Int,
    listNum: Int
): PoemModelWithNum =
    withContext(Dispatchers.IO) {
        val poemFile =
            this@getPoemByNum.getJson("chinese_poetry/poetry/${type}/${dynasty}/intent/${type}.${dynasty}.${fileNum * 1000}.json")
        val poemListModel: PoemListModel = (poemFile.fromJson<PoemListModel>() ?: arrayListOf(
            PoemModelWithNum.init.poemModel
        )) as PoemListModel
        PoemModelWithNum(poemListModel.get(listNum), type, dynasty, fileNum, listNum)
    }

suspend fun Context.getPoemBy(
    type: String? = null,
    dynasty: String? = null,
    author: String? = null,
    title: String? = null
): MutableList<PoemModelWithNum> =
    withContext(Dispatchers.IO) {
        var poemListResult: MutableList<PoemModelWithNum> = arrayListOf<PoemModelWithNum>()
        for ((type1, dynasty1) in listOf(
            listOf("ci", "song"),
            listOf("poet", "song"),
            listOf("poet", "tang")
        )) {
            if (type != null && type != type1) continue
            if (dynasty != null && dynasty != dynasty1) continue
            for (i: Int in getPoemRange(type1, dynasty1)) {
                val poemFile =
                    this@getPoemBy.getJson("chinese_poetry/poetry/${type1}/${dynasty1}/intent/${type1}.${dynasty1}.${i * 1000}.json")
                val poemListModel: PoemListModel = (poemFile.fromJson<PoemListModel>()
                    ?: arrayListOf(PoemModelWithNum.init.poemModel)) as PoemListModel


                poemListModel.forEachIndexed { j, poemModel ->
                    val compareTitle =
                        (type1 == "poet" && poemModel.title.toJian() == title?.toJian())
                                || (type1 == "ci" && poemModel.rhythmic.toJian() == title?.toJian())
                    val compareAuthor = poemModel.author.toJian() == author?.toJian()
                    if (author != null && title == null) {
                        if (compareAuthor)
                            poemListResult.add(PoemModelWithNum(poemModel, type1, dynasty1, i, j))
                    } else if (author == null && title != null) {
                        if (compareTitle)
                            poemListResult.add(PoemModelWithNum(poemModel, type1, dynasty1, i, j))
                    } else if (author != null && title != null) {
                        if (compareAuthor && compareTitle)
                            poemListResult.add(PoemModelWithNum(poemModel, type1, dynasty1, i, j))
                    }
                }
            }
        }
        poemListResult
    }

suspend fun Context.getPoemByNoteFile(noteFile: String) = withContext(Dispatchers.IO) {
    val (type, dynasty, fileNum, listNum) = noteFile.split("/")
    this@getPoemByNoteFile.getPoemByNum(type, dynasty, fileNum.toInt(), listNum.toInt())
}

fun Context.getPoemByNoteFileFlow(noteFile: String): Flow<PoemModelWithNum> =
    flow { emit(this@getPoemByNoteFileFlow.getPoemByNoteFile(noteFile)) }