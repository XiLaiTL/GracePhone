package `fun`.vari.gracephone.logic.model

import `fun`.vari.gracephone.logic.utils.fromJson
import `fun`.vari.gracephone.logic.utils.getJson
import `fun`.vari.gracephone.logic.utils.toJian
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class PoemAuthorListModel : ArrayList<PoemAuthorModel>()
data class PoemAuthorModel(
    val desc: String,
    val id: String,
    val name: String
)

class CiAuthorListModel : ArrayList<CiAuthorModel>()
data class CiAuthorModel(
    val description: String,
    val name: String,
    val short_description: String
)

data class AuthorModel(
    val name: String,
    val desc: String,
    val dynasty: String,
) {
    companion object {
        val init: AuthorModel = AuthorModel("无名氏", "无简介", "无")
    }
}

fun CiAuthorModel.toAuthor() = AuthorModel(this.name, this.description, "宋")
fun PoemAuthorModel.toAuthor(dynasty: String) = AuthorModel(this.name, this.desc, dynasty)

suspend fun Context.getAuthorByName(name: String): AuthorModel =
    withContext(Dispatchers.IO) {
        var authorModel: AuthorModel = AuthorModel.init
        val poemTangAuthorFile =
            this@getAuthorByName.getJson("chinese_poetry/poetry/poet/tang/intent/authors.tang.json")
        val poemTangAuthorListModel: PoemAuthorListModel =
            (poemTangAuthorFile.fromJson<PoemAuthorListModel>()
                ?: arrayListOf()) as PoemAuthorListModel
        val authorTang =
            poemTangAuthorListModel.find { p -> p.name.toJian() == name.toJian() }?.toAuthor("唐")

        val poemSong1AuthorFile =
            this@getAuthorByName.getJson("chinese_poetry/poetry/poet/song/intent/authors.song.json")
        val poemSong1AuthorListModel: PoemAuthorListModel =
            (poemSong1AuthorFile.fromJson<PoemAuthorListModel>()
                ?: arrayListOf()) as PoemAuthorListModel
        val authorSong1 =
            poemSong1AuthorListModel.find { p -> p.name.toJian() == name.toJian() }?.toAuthor("宋")

        val poemSong2AuthorFile =
            this@getAuthorByName.getJson("chinese_poetry/poetry/ci/song/intent/author.song.json")
        val poemSong2AuthorListModel: CiAuthorListModel =
            (poemSong2AuthorFile.fromJson<CiAuthorListModel>()
                ?: arrayListOf()) as CiAuthorListModel
        val authorSong2 =
            poemSong2AuthorListModel.find { p -> p.name.toJian() == name.toJian() }?.toAuthor()

        authorModel = authorTang ?: authorSong1 ?: authorSong2 ?: authorModel
        authorModel
    }

fun Context.getAuthorByNameFlow(name: String): Flow<AuthorModel> =
    flow { emit(this@getAuthorByNameFlow.getAuthorByName(name)) }