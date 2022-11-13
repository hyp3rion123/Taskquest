package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.time.LocalDate

// Individual to-do items
@Serializable
class Task(
    var id: Int, var title: String, var desc: String = "", var dueDate: String = "",
    var dateCreated: String = LocalDate.now().toString(), var priority: Priority? = null,
    var difficulty: Difficulty? = null, var complete: Boolean = false, var completeOnce: Boolean = false
) {
    var tags: MutableSet<String> = mutableSetOf<String>()
    var coinValue: Int = when(difficulty) {
        Difficulty.Hard -> 3
        Difficulty.Medium -> 2
        Difficulty.Easy -> 1
        else -> 0
    }
    var multiplier: Int = when(priority) {
        Priority.High -> 3
        Priority.Medium -> 2
        Priority.Low -> 1
        else -> 0
    }

    fun calcCoinValue() {
        if (coinValue == 0 && multiplier != 0) {
            coinValue = multiplier
        } else if (coinValue != 0 && multiplier != 0) {
            coinValue *= multiplier
        } else if (coinValue == 0) {
            coinValue = 1
        }
    }

    init {
        calcCoinValue()
    }
}
