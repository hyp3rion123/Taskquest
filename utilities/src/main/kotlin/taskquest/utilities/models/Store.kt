package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.views.MainUser

@Serializable
class Store {
    val items = mutableListOf<Item>()

    fun buyItem(itemId: Int) {
        val results = items.filter { it.id == itemId }
        if (!results[0].purchased && results[0].price <= MainUser.userInfo.wallet) {
            MainUser.userInfo.wallet -= results[0].price
            results[0].purchased = true
        }
    }
}