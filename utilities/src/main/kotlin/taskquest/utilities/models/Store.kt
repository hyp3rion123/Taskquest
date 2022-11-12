package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.views.MainUser

@Serializable
class Store {
    var items = mutableListOf<Item>()

    fun buyItem(itemId: Int) {
        val results = items.filter { it.id == itemId }
        if (!results[0].purchased && results[0].price <= MainUser.userInfo.wallet) {
            MainUser.userInfo.wallet -= results[0].price
            results[0].purchased = true
        }
    }

    fun to_string(): String {
        var result = ""
        for (item in items) {
            result += item.name + ": " + item.purchased + ". ";
        }
        return result;
    }
}