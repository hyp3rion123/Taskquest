package taskquest.utilities.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.io.File
import java.time.LocalDate

// Individual TO-DO items
@Serializable
class Task(
    val id: Int, var title: String, var desc: String = "", var dueDate: String = "",
    var dateCreated: String = LocalDate.now().toString(), var priority: Priority? = null,
    var difficulty: Difficulty? = null, var complete: Boolean = false
    ) {
}

// Methods for interacting with Item instances in a MutableList
fun MutableList<Task>.add(title: String, priority: Priority, difficulty: Difficulty) {
    this.add(Task(id=this.size, title=title, priority=priority, difficulty=difficulty))
}

fun MutableList<Task>.add(id: Int, title: String, desc: String, dueDate: String, dateCreated: String,
                          priority: Priority, difficulty: Difficulty, complete: Boolean) {
    this.add(Task(id, title, desc, dueDate, dateCreated, priority, difficulty, complete))
}

private val formattedJson = Json {
    encodeDefaults = true
    prettyPrint = true
}

fun MutableList<Task>.save(filename: String) {
    val jsonList = formattedJson.encodeToString(this.toList())
    File(filename).writeText(jsonList)
}

fun MutableList<Task>.restore(filename: String) {
    try {
        val jsonList = File(filename).readText()
        this.addAll(formattedJson.decodeFromString(jsonList))
    } catch (_: Exception) {
    }
}