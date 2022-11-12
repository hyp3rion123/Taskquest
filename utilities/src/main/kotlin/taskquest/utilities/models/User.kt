package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.views.MainUser

@Serializable
class User() {
    var lastUsedList: Int = - 1
    var wallet: Int = 0
    val lists = mutableListOf<TaskList>()
    var store: Store = Store()
    val tags = mutableSetOf<String>()
    var profileImageName: String = "Default.png"
    var longestStreak: Int = 0
    var level: Int = 0


    fun buyItem(itemId: Int) {
        val results = store.items.filter { it.id == itemId }
        if (!results[0].purchased && results[0].price <= wallet) {
            wallet -= results[0].price
            store.items[itemId].purchased = true
        }
    }
    fun to_string() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}