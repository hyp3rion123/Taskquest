package taskquest.console.views

import org.junit.jupiter.api.Test
import java.io.File

internal class MainTest {
    private fun mainTestFunc(params: Array<String>) {
        if (File("data.json").exists()) {
            File("data.json").copyTo(File("backup.json"), true)
            File("data.json").delete()
        }
        main(params)
        if (File("backup.json").exists()) {
            File("backup.json").copyTo(File("data.json"), true)
            File("backup.json").delete()
        }
    }

    @Test
    fun mainWithoutArgs() {
        mainTestFunc(arrayOf(""))
    }

    @Test
    fun mainWithShow() {
        mainTestFunc(arrayOf("show"))
    }

    @Test
    fun mainWithAdd() {
        mainTestFunc(arrayOf("add"))
    }

    @Test
    fun mainWithEdit() {
        mainTestFunc(arrayOf("edit", "2"))
    }

    @Test
    fun mainWithEditWrong() {
        mainTestFunc(arrayOf("edit"))
    }

    @Test
    fun mainWithDel() {
        mainTestFunc(arrayOf("del", "1"))
    }

    @Test
    fun mainWithSort() {
        mainTestFunc(arrayOf("sort", "byPriorityAsc"))
    }

    @Test
    fun mainWithSortWrong() {
        mainTestFunc(arrayOf("sort"))
    }

    @Test
    fun mainWithListSelect() {
        mainTestFunc(arrayOf("listselect", "1"))
    }

    @Test
    fun mainWithListSelectWrong() {
        mainTestFunc(arrayOf("listselect"))
    }
    @Test
    fun mainWithListShow() {
        mainTestFunc(arrayOf("listshow"))
    }

    @Test
    fun mainWithListEdit() {
        mainTestFunc(arrayOf("listedit", "1"))
    }

    @Test
    fun mainWithListDelete() {
        mainTestFunc(arrayOf("listdel", "1"))
    }

    @Test
    fun mainWithHelp() {
        mainTestFunc(arrayOf("help"))
    }
    @Test
    fun mainWithHelpNothing() {
        mainTestFunc(arrayOf(""))
    }

    @Test
    fun mainWithWallet() {
        mainTestFunc(arrayOf("wallet"))
    }

    @Test
    fun mainWithShowTags() {
        mainTestFunc(arrayOf("showtags"))
    }

    @Test
    fun mainWithAddTags() {
        mainTestFunc(arrayOf("addtags", "2", "tag1", "tag2"))
        mainTestFunc(arrayOf("deltag", "tag1"))
        mainTestFunc(arrayOf("deltag", "tag2"))
    }

    @Test
    fun mainWithDelTag() {
        mainTestFunc(arrayOf("deltag", "tag1"))
        mainTestFunc(arrayOf("deltag", "tag2"))
    }

}