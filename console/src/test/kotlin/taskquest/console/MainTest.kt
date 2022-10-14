package taskquest.console

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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
        main(arrayOf("add", "conor"))
        main(arrayOf("show"))
        main(arrayOf("del", "2"))
        main(arrayOf("show"))
    }
}