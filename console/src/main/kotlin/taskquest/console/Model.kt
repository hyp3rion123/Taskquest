package taskquest.console

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

// Individual TO-DO items
@Serializable
data class Item(
    val id: Int, var text: String
)

// Collection of TO-DO items
fun MutableList<Item>.add(text: String) {
    this.add(Item(this.size, text))
}

fun MutableList<Item>.add(id: Int, text: String) {
    this.add(Item(id, text))
}

fun MutableList<Item>.save(filename: String) {
    val jsonList = Json.encodeToString(this.toList())
    File(filename).writeText(jsonList)
}

fun MutableList<Item>.restore(filename: String) {
    try {
        val jsonList = File(filename).readText()
        this.addAll(Json.decodeFromString(jsonList))
    } catch (_: Exception) {
    }
}