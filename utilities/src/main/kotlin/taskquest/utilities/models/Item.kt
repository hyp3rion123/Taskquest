package taskquest.utilities.models

import taskquest.utilities.models.enums.ItemType

data class Item(val id: Int, val name: String, val price: Int, val type: ItemType) {
}