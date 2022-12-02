package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority

internal class UserTest {
    @Test
    fun diminishingReturns() {
        val user = User(0)
        user.addList("Test List")
        user.lists[0].addItem(title="item 1", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 2", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 3", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 4", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 5", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 6", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 7", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 8", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 9", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 10", priority=Priority.High, difficulty=Difficulty.Hard)
        user.lists[0].addItem(title="item 11", priority=Priority.High, difficulty=Difficulty.Hard)
        user.completeTask(user.lists[0].tasks[0])
        user.completeTask(user.lists[0].tasks[1])
        user.completeTask(user.lists[0].tasks[2])
        user.completeTask(user.lists[0].tasks[3])
        user.completeTask(user.lists[0].tasks[4])
        assert(user.wallet == 45)
        user.completeTask(user.lists[0].tasks[5])
        assert(user.wallet == 51)
        user.completeTask(user.lists[0].tasks[6])
        user.completeTask(user.lists[0].tasks[7])
        user.completeTask(user.lists[0].tasks[8])
        user.completeTask(user.lists[0].tasks[9])
        assert(user.wallet == 75)
        user.completeTask(user.lists[0].tasks[10])
        assert(user.wallet == 79)
    }
}