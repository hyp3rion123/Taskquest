package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.ItemType

internal class StoreTest {
    @Test
    fun createStore() {
        val store = Store()
        store.addItem("Test Item", 50, ItemType.ProfilePicture)
        assert(store.items.size == 1)
    }

    @Test
    fun buyFromStoreFail() {
        val store = Store()
        store.addItem("Test Item", 50, ItemType.ProfilePicture)

        val user = User(0)
        assert(!store.buyItem(0, user))
    }

    @Test
    fun buyFromStoreSuccess() {
        val store = Store()
        store.addItem("Test Item", 50, ItemType.ProfilePicture)

        val user = User(0)
        user.wallet = 51
        assert(store.buyItem(0, user))
        assert(user.wallet == 1)
    }
}