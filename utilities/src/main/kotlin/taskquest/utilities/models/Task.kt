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
    var rewardCoins : Int = 0

    fun calcCoinValue() {
        var coinsFromDiff: Int = when(difficulty) {
            Difficulty.Hard -> 3
            Difficulty.Medium -> 2
            Difficulty.Easy -> 1
            else -> 0
        }
        var coinsFromPrio: Int = when(priority) {
            Priority.High -> 3
            Priority.Medium -> 2
            Priority.Low -> 1
            else -> 0
        }

        if (coinsFromDiff == 0 && coinsFromPrio != 0) {
            this.rewardCoins = coinsFromPrio
        } else if (coinsFromDiff != 0 && coinsFromPrio != 0) {
            this.rewardCoins = coinsFromDiff * coinsFromPrio
        } else if (coinsFromDiff == 0) {
            this.rewardCoins = 1
        } else {
            this.rewardCoins = coinsFromDiff
        }
    }

    init {
        this.calcCoinValue()
    }
}
