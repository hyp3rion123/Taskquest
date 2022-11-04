package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.ItemType
import taskquest.utilities.views.MainUser

internal class StoreTest {
    @Test
    fun createStore() {
        val store = Store()
        val item = Item(id = 0, name = "Test Item", price = 50, type = ItemType.ProfilePicture)
        store.items.add(item)
        assert(store.items.size == 1)
    }

    @Test
    fun buyFromStoreFail() {
        val store = Store()
        val item = Item(id = 0, name = "Test Item", price = 50, type = ItemType.ProfilePicture)
        store.items.add(item)
        store.buyItem(0)
        assert(!store.items[0].purchased)
    }

    @Test
    fun buyFromStoreSuccess() {
        val store = Store()
        val item = Item(id = 0, name = "Test Item", price = 50, type = ItemType.ProfilePicture)
        store.items.add(item)
        MainUser.userInfo.wallet += 51
        store.buyItem(0)
        assert(store.items[0].purchased)
        assert(MainUser.userInfo.wallet == 1)
    }
}