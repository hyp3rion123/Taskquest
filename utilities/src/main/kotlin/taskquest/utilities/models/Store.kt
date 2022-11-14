package taskquest.utilities.models

import taskquest.utilities.models.enums.ItemType

class Store {
    var items = mutableListOf<Item>()

    fun addItem(name: String, price: Int, type: ItemType) {
        items.add(Item(id=this.items.size, name=name, price=price, type=type))
    }

    fun buyItem(itemId: Int, user: User): Boolean {
        val itemToPurchase = this.items[itemId]
        return if (!user.purchasedItems.contains(itemToPurchase) && itemToPurchase.price <= user.wallet) {
            user.wallet -= itemToPurchase.price
            user.purchasedItems.add(itemToPurchase)
            true
        } else {
            false
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