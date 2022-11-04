package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.models.enums.ItemType

@Serializable
data class Item(val id: Int, val name: String, val price: Int, val type: ItemType, var purchased: Boolean = false) {
}