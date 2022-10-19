package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.time.LocalDate

// Individual to-do items
@Serializable
class Task(
    val id: Int, var title: String, var desc: String = "", var dueDate: String = "",
    var dateCreated: String = LocalDate.now().toString(), var priority: Priority? = null,
    var difficulty: Difficulty? = null, var complete: Boolean = false
) {
    val tags: MutableSet<String> = mutableSetOf<String>()
}
