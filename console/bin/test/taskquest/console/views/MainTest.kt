package taskquest.console.views

import org.junit.jupiter.api.Test

internal class MainTest {

    @Test
    fun mainWithoutArgs() {
        main(arrayOf(""))
    }

    @Test
    fun mainWithShow() {
        main(arrayOf("show"))
    }

    @Test
    fun mainWithAdd() {
        main(arrayOf(""))
    }
}