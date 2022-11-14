package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.ItemType

internal class ItemTest {
    @Test
    fun createItem() {
        val item = Item(id = 0, name = "Test Item", price = 50, type = ItemType.ProfilePicture)
        assert(item.id == 0)
        assert(item.name == "Test Item")
        assert(item.price == 50)
        assert(item.type == ItemType.ProfilePicture)
    }
}