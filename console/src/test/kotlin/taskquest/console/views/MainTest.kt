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
        main(arrayOf("add"))
    }

    @Test
    fun mainWithEdit() {
        main(arrayOf("edit", "2"))
    }

    @Test
    fun mainWithEditWrong() {
        main(arrayOf("edit"))
    }

    @Test
    fun mainWithDel() {
        main(arrayOf("del", "1"))
    }

    @Test
    fun mainWithSort() {
        main(arrayOf("sort", "byPriorityAsc"))
    }

    @Test
    fun mainWithSortWrong() {
        main(arrayOf("sort"))
    }

    @Test
    fun mainWithListSelect() {
        main(arrayOf("listselect", "1"))
    }

    @Test
    fun mainWithListSelectWrong() {
        main(arrayOf("listselect"))
    }
    @Test
    fun mainWithListShow() {
        main(arrayOf("listshow"))
    }

//    @Test
//    fun mainWithListEdit() {
//        main(arrayOf("listedit", "1"))
//    }

    @Test
    fun mainWithListDelete() {
        main(arrayOf("listdel", "1"))
    }

    @Test
    fun mainWithHelp() {
        main(arrayOf("help"))
    }
    @Test
    fun mainWithHelpNothing() {
        main(arrayOf(""))
    }

    @Test
    fun mainWithWallet() {
        main(arrayOf("wallet"))
    }

    @Test
    fun mainWithShowTags() {
        main(arrayOf("showtags"))
    }

    @Test
    fun mainWithAddTags() {
        main(arrayOf("addtags", "2", "tag1", "tag2"))
        main(arrayOf("deltag", "tag1"))
        main(arrayOf("deltag", "tag2"))
    }

    @Test
    fun mainWithDelTag() {
        main(arrayOf("deltag", "tag1"))
        main(arrayOf("deltag", "tag2"))
    }

}