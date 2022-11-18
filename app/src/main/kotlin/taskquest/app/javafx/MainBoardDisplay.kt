package taskquest.app.javafx

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import org.controlsfx.control.CheckComboBox
import taskquest.utilities.controllers.SaveUtils.Companion.restoreStoreDataFromText
import taskquest.utilities.controllers.SaveUtils.Companion.restoreUserData
import taskquest.utilities.controllers.SaveUtils.Companion.saveStoreData
import taskquest.utilities.controllers.SaveUtils.Companion.saveUserData
import taskquest.utilities.controllers.FunctionClass
import taskquest.utilities.models.*
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.io.File
import javafx.beans.value.ChangeListener
import java.time.LocalDate
import java.util.*

// for outlining layout borders
val debugMode = false
val debugCss = """
            -fx-border-color: black;
            -fx-border-insets: 5;
            -fx-border-width: 1;
            -fx-border-style: dashed;
            
            """.trimIndent()

val bannerTextCss = """
            -fx-border-color: white;
            -fx-border-insets: 15;
            -fx-border-width: 0;
            -fx-border-style: dashed;
            """.trimIndent()

val dataFileName = "data.json"
val storeFileName = "default/store.json"
val globalFont = Font.font("Courier New", FontWeight.BOLD, 16.0)
val darkBlue = "#3d5a80"
val lighterBlue = "#98c1d9"
val lightestBlue = "#e0fbfc"
val darkY = "#bf9b30"
val lighterY = "#ffcf40"
val lightestY = "#ffdc73"
var base1 = darkBlue
var base2 = lighterBlue
var base3 = lightestBlue
var theme = 0
val iconSize = 20.0

class MainBoardDisplay {
    var user = User()
    var toDoVBox = VBox()
    var store = Store()
    var boardViewHBox = HBox()
    var bannerImageView = ImageView()
    var profileImgView = ImageView()
    var sortCalled = false
    var sortingMethodForBox = ""
    var groupCalled = false
    var groupingMethodForBox = ""

    fun dataChanged() {
        user.convertToString()
        saveUserData(user)
    }
    fun getTheme(): Triple<String, String, String> {
        return Triple(base1, base2, base3)
    }

    fun ImageButton(path: String, h: Double, w: Double): Button {
        var button = Button()
        val originalImage = Image(path)
        val imageView = ImageView(originalImage)
        imageView.fitWidth = h
        imageView.fitHeight = w
        imageView.isPreserveRatio = true
        button.graphic = imageView
        return button
    }

    fun start_display(mainStage: Stage?) {
        user = restoreUserData()

        val fileContent = javaClass.getResource("/default/store.json").readText()
        store = restoreStoreDataFromText(fileContent)

        if (mainStage != null) {
            // restore window dimensions and location
            mainStage.x = user.x
            mainStage.y = user.y
            mainStage.height = user.height
            mainStage.width = user.width

            // save dimensions on close
            mainStage.setOnCloseRequest {
                user.x = mainStage.x
                user.y = mainStage.y
                user.height = mainStage.height
                user.width = mainStage.width
                dataChanged()
            }
        }

        // set title for the stage
        mainStage?.title = "TaskQuest";

        val headerContainer = createHeaderContainer()

        // updates x and y of window
        mainStage?.xProperty()?.addListener { _, _, newValue -> user.x = newValue.toDouble() }
        mainStage?.yProperty()?.addListener { _, _, newValue -> user.y = newValue.toDouble() }

        //Banner
        val headerHBox = createBanner() //<--fix the sizing of banner

        //Main tasks board

        var taskList1 : TaskList

        val createTaskButton = createAddButton()

        if (user.lastUsedList != -1) {
            taskList1 = user.lists[user.lastUsedList]
            toDoVBox = createTasksVBox(createTaskButton, taskList1, taskList1.title)
        } else {
            if (user.lists.size == 0) {
                toDoVBox = createEmptyVBox("You have no lists to display. Create a list!")
            } else {
                toDoVBox = createEmptyVBox("Select or create a list!")
            }

        }
        boardViewHBox = HBox(20.0, toDoVBox)
        boardViewHBox.alignment = Pos.TOP_CENTER
        var boardViewScroll = ScrollPane()
        boardViewScroll.content = boardViewHBox
        boardViewScroll.style = """
            -fx-background-color:""" + getTheme().third + """;
            -fx-background:""" + getTheme().third + """;
        """
        boardViewScroll.isFitToWidth = true;

        var (sideBarVBox, buttonList) = createSideBarVBox() //this order is required for theme switch - need to pass scene
        var themeButton = buttonList[0]
        var profileButton = buttonList[1]
        var shopButton = buttonList[2]

        val mainTasksSection = VBox(20.0, headerContainer, boardViewScroll)
        mainTasksSection.padding = Insets(0.0, 0.0, 0.0, 0.0)
        mainTasksSection.style = """
            -fx-background-color:""" + getTheme().third + """;
        """

        val taskListVBox = createTaskListVBox(user.lists, createTaskButton)

        val mainScreenPane = BorderPane()
        mainScreenPane.right = taskListVBox
        mainScreenPane.center = mainTasksSection
        mainScreenPane.left = sideBarVBox

        mainScreenPane.right.minHeight(250.0)

        var mainScene = Scene(mainScreenPane, 900.0, 600.0)

        shopButton.setOnMouseClicked {
            mainStage?.scene = createShopScene(mainStage, mainScene) //created every time for refresh purposes
        }

        profileButton.setOnMouseClicked {
            val profileScene = showProfileScreen(mainStage, mainScene);
            mainStage?.scene = profileScene
        }

        themeButton.setOnMouseClicked {
            if (theme == 0) {
                theme = 1
                base1 = darkY
                base2 = lighterY
                base3 = lightestY
            } else if (theme == 1) {
                theme = 0
                base1 = darkBlue
                base2 = lighterBlue
                base3 = lightestBlue
            }
//            updateTheme()
            if (mainStage != null) {
                user.x = mainStage.x
                user.y = mainStage.y
                user.height = mainStage.height
                user.width = mainStage.width
                dataChanged()
                mainStage.close()
            }
            start_display(mainStage)
        }

        val createListHotkey: KeyCombination = KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN)
        val createListAction = Runnable {
            createTaskListStage(taskListVBox, createTaskButton)
        }
        mainScene.accelerators[createListHotkey] = createListAction

        val deleteListHotkey: KeyCombination = KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN)
        val deleteListAction = Runnable {
            if (user.lastUsedList != -1) {
                deleteList(user.lists[user.lastUsedList].id, taskListVBox, null)
            }
        }
        mainScene.accelerators[deleteListHotkey] = deleteListAction

        val createTaskHotkey: KeyCombination = KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN)
        val createTaskAction = Runnable {
            if (user.lastUsedList != -1) {
                createTaskStage(user.lists[user.lastUsedList], toDoVBox, createTaskButton)
            }
        }
        mainScene.accelerators[createTaskHotkey] = createTaskAction


        val deleteTaskHotkey: KeyCombination = KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN)
        val deleteTaskAction = Runnable {
            if (user.lastUsedList != -1 && user.lists[user.lastUsedList].curTask != -1) {
                val curList = user.lists[user.lastUsedList]
                val task = curList.tasks[curList.curTask]
                toDoVBox.children.removeAt(curList.curTask + 1)
                curList.deleteItemByID(task.id)
                if (curList.tasks.size == 0) {
                    val lbl = Label("You have no tasks for this list. Create some!")
                    lbl.font = globalFont
                    toDoVBox.children.add(lbl)
                }
                user.convertToString()
                curList.updateCurTask(task.id)
                dataChanged()
            }
        }
        mainScene.accelerators[deleteTaskHotkey] = deleteTaskAction

        val toShopHotkey: KeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        val toShopAction = Runnable {
            mainStage?.scene = createShopScene(mainStage, mainScene)
        }
        mainScene.accelerators[toShopHotkey] = toShopAction

        val toProfileHotkey: KeyCombination = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        val toProfileAction = Runnable {
            val profileScene = showProfileScreen(mainStage, mainScene);
            mainStage?.scene = profileScene
        }
        mainScene.accelerators[toProfileHotkey] = toProfileAction

        val switchThemeHotkey: KeyCombination = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        val switchThemeAction = Runnable {
            if (theme == 0) {
                theme = 1
                base1 = darkY
                base2 = lighterY
                base3 = lightestY
            } else if (theme == 1) {
                theme = 0
                base1 = darkBlue
                base2 = lighterBlue
                base3 = lightestBlue
            }
//            updateTheme()
            if (mainStage != null) {
                user.x = mainStage.x
                user.y = mainStage.y
                user.height = mainStage.height
                user.width = mainStage.width
                dataChanged()
                mainStage.close()
            }
            start_display(mainStage)
        }
        mainScene.accelerators[switchThemeHotkey] = switchThemeAction

        val editListHotkey: KeyCombination = KeyCodeCombination(KeyCode.PERIOD, KeyCombination.CONTROL_DOWN)
        val editListAction = Runnable {
            if (user.lastUsedList != -1) {
                editTaskListStage(user.lists[user.lastUsedList], createTaskButton, taskListVBox)
            }
        }
        mainScene.accelerators[editListHotkey] = editListAction

        val editTaskHotkey: KeyCombination = KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN)
        val editTaskAction = Runnable {
            if (user.lastUsedList != -1 && user.lists[user.lastUsedList].curTask != -1) {
                val curList = user.lists[user.lastUsedList]
                editTaskStage(curList.tasks[curList.curTask], toDoVBox, createTaskButton)
            }
        }
        mainScene.accelerators[editTaskHotkey] = editTaskAction

        mainStage?.setResizable(true)
        mainStage?.setScene(mainScene)
        mainStage?.show()


        //DEBUG
        if (debugMode) {
            toDoVBox.style = debugCss
//            headerLabel.style = debugCss
            boardViewHBox.style = debugCss
            sideBarVBox.style = debugCss
            mainTasksSection.style = debugCss
        }
    }

    fun createHeaderContainer(): BorderPane{
        //Banner
        val bannerContainer = createBanner()

        //Profile Img
        createProfilePic()

        //Coins
        val coinsLabel = Label("Current coins\n" + user.wallet)
        coinsLabel.font = globalFont

        //Header container
        val headerContainer = BorderPane()
        headerContainer.padding = Insets(10.0, 10.0, 0.0, 10.0)
        headerContainer.left = coinsLabel
        headerContainer.center = bannerContainer
        headerContainer.right = profileImgView
        return headerContainer
    }
    fun updateBanner() {
        val bannerPath = "/assets/banners/" + user.bannerRank + ".png"
        val banner = Image(bannerPath)
        bannerImageView.image = banner
    }

    fun createProfilePic() {
        //Profile Pic
        val profileImage = Image("/assets/" + user.profileImageName)
        profileImgView.image = profileImage
        profileImgView.fitWidth = 120.0
        profileImgView.fitHeight = 120.0
    }

    fun createBanner(): StackPane {
        val bannerPath = "/assets/banners/" + user.bannerRank + ".png"
        val banner = Image(bannerPath)
        bannerImageView.image = banner
//        bannerImageView.fitWidth = 250.0
        bannerImageView.fitHeight = 60.0

        var headerLabel = Label(".Welcome back, USER_NAME.") //<--when putting the actual user_name leave the dots, they help with alignment
        headerLabel.alignment = Pos.CENTER
        headerLabel.font = globalFont
        bannerImageView.fitWidthProperty().bind(headerLabel.widthProperty())
//        bannerImageView.fitHeightProperty().bind(headerLabel.heightProperty())

        var headerHBox = HBox(10.0, headerLabel)
        headerHBox.alignment = Pos.CENTER

        val container = StackPane()
        container.children.addAll(bannerImageView, headerHBox)
        container.alignment = Pos.CENTER

        return container
    }

    fun setDefaultButtonStyle(button: Button) {
        val buttonStyle = """
            -fx-background-color:""" + getTheme().first + """;
            -fx-text-fill: white;  
        """
        button.style = buttonStyle
        button.font = globalFont

        button.onMouseEntered = EventHandler<MouseEvent?> {
            button.style = """
            -fx-background-color: #383838;
            -fx-text-fill: white;   
            """.trimIndent()
        }

        button.onMouseExited = EventHandler<MouseEvent?> {
            button.style = buttonStyle
        }
    }

    fun createTaskListVBox(data: List<TaskList>, btn_create_task_to_do: Button): VBox {

        // create a VBox
        val taskListVBox = VBox(15.0)
        taskListVBox.alignment = Pos.TOP_CENTER
        taskListVBox.style = """
            -fx-background-color:""" + getTheme().second + """;
        """

        val searchBarLabel = Label("    Task Lists    ")
        searchBarLabel.font = globalFont
        searchBarLabel.style = """
            -fx-background-color:"""+ getTheme().first+ """;
            -fx-text-fill: white;
        """
        searchBarLabel.alignment = Pos.TOP_CENTER

        taskListVBox.children.add(searchBarLabel)

//        val textField = TextField()
//        textField.setPromptText("Search here!")
//        taskListVBox.children.add(textField)

        // add buttons to VBox for each list
        for (taskList in data) {
            createListHbox(taskList, taskListVBox, btn_create_task_to_do, false)
        }

        val addList = createAddButton()
        setDefaultButtonStyle(addList)
        taskListVBox.children.add(addList)
        addList.setOnMouseClicked {
            createTaskListStage(taskListVBox, btn_create_task_to_do)
        }

        taskListVBox.padding = Insets(10.0)

        return taskListVBox
    }

    fun deleteList(id : Int, taskListVBox : VBox, hbox : HBox?) {
        // delete list in the backend
        if (hbox == null) {
            taskListVBox.children.removeAt(user.lastUsedList + 1)
        } else {
            taskListVBox.children.remove(hbox)
        }
        user.deleteList(id)
        if (user.lists.size == 0) {
            user.nextId = 0
            boardViewHBox.children.clear()
            boardViewHBox.children.add(createEmptyVBox("You have no lists to display. Create a list!"))
        } else if (user.lastUsedList == -1) {
            boardViewHBox.children.clear()
            boardViewHBox.children.add(createEmptyVBox("Select or create a list!"))
        }


    }
    fun createAddButton(): Button {
        var btn = ImageButton("/assets/icons/add.png",iconSize,iconSize)
        btn.setMinSize(btn.prefWidth, btn.prefHeight)
        return btn
    }
    fun createDeleteButton(): Button {
        var btn = ImageButton("/assets/icons/delete.png",iconSize,iconSize)
        btn.setMinSize(btn.prefWidth, btn.prefHeight)
        return btn
    }
    fun createDetailsButton(): Button {
        var btn = ImageButton("/assets/icons/details.png",iconSize,iconSize)
        btn.setMinSize(btn.prefWidth, btn.prefHeight)
        return btn
    }

    fun createCopyButton(): Button {
        var btn = ImageButton("/assets/icons/copy.png",iconSize,iconSize)
        btn.setMinSize(btn.prefWidth, btn.prefHeight)
        return btn
    }

    fun createTaskHbox(task: Task, data:TaskList, tasksVBox: VBox, title: String, create_button: Button): HBox {
        val hbox = HBox(5.0)
        hbox.style = """
            -fx-border-color: """ + getTheme().second + """;
            -fx-border-width: 2;
            -fx-border-style: solid;
            -fx-border-radius: 10px;
            """.trimIndent()
        hbox.padding = Insets(7.0)
        val taskTitle = Label(task.title)
        taskTitle.font = globalFont
        val c = CheckBox()
        c.setSelected(task.complete)
        c.setOnMouseClicked {
            if (task.complete) {
                task.complete = false
            } else {
                task.complete = true
                showTaskCompletionStage(task)
            }
            dataChanged()
        }
        var btn_del = createDeleteButton()
        val spacer = Pane()
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS)
        spacer.setMinSize(10.0, 10.0)
        var btn_edit = createDetailsButton()
        var btn_copy = createCopyButton()
        setDefaultButtonStyle(btn_del)
        setDefaultButtonStyle(btn_edit)
        setDefaultButtonStyle(btn_copy)
        hbox.children.addAll(c, taskTitle, spacer, btn_del, btn_edit, btn_copy)
        hbox.setPrefSize(400.0, 50.0)
        btn_copy.onDragDetected = EventHandler<MouseEvent?> {event ->
            /* drag was detected, start a drag-and-drop gesture*/
            /* allow any transfer mode */
            val db: Dragboard = hbox.startDragAndDrop(*TransferMode.ANY)

            /* Put a string on a dragboard */
            val content = ClipboardContent()
            content.putString(task.toString())
            db.setContent(content)
            event.consume()
        }

        hbox.onDragDetected = EventHandler<MouseEvent?> {event ->
            /* drag was detected, start a drag-and-drop gesture*/
            /* allow any transfer mode */
            val db: Dragboard = hbox.startDragAndDrop(*TransferMode.ANY)

            /* Put a string on a dragboard */
            val content = ClipboardContent()
            content.putString(task.id.toString())
            db.setContent(content)
            event.consume()
        }

        hbox.onDragEntered = EventHandler<DragEvent?> { event ->
            if (event.gestureSource !== hbox) hbox.opacity = 0.5
        }

        hbox.onDragExited = EventHandler<DragEvent?> { _ ->
            hbox.opacity = 1.0
        }

        hbox.onDragOver = EventHandler<DragEvent?> {event ->
            /* data is dragged over the target */
            /* accept it only if it is not dragged from the same node and if it has a string data */
            if (event.gestureSource !== hbox &&
                event.dragboard.hasString()
            ) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            }
            event.consume()
        }
        hbox.onDragDropped = EventHandler<DragEvent?> {event ->
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            val db = event.dragboard
            var success = data.moveItem(db.string.toInt(), task.id)
            if(success) {
                var currList = user.lists[0]
                //Update backend-needed if user switches back and forth between lists
                for((index, list) in user.lists.withIndex()){
                    if(list.id == data.id){
                        currList = list
                        user.lists.remove(list)
                        user.lists.add(index,data)
                        break
                    }
                }
                //Update frontend
                tasksVBox.children.clear()
                addVBoxNonTasks(create_button, data, title, tasksVBox)
                for(currTask in currList.tasks){
                    val child = createTaskHbox(currTask, currList, tasksVBox, title, create_button)
                    child.alignment = Pos.CENTER_LEFT
                    tasksVBox.children.add(child)
                }
            }
            /* let the source know whether the string was successfully transferred and used */
            event.isDropCompleted = success
            event.consume()
        }

        hbox.onMouseClicked = EventHandler<MouseEvent?> {
            if (data.curTask != -1) {
                tasksVBox.children[data.curTask + 1].style = """
                    -fx-border-color: """ + getTheme().second + """;
                    -fx-border-width: 2;
                    -fx-border-style: solid;
                    -fx-border-radius: 10px;
                    """.trimIndent()
            }
            val prevTask = data.curTask
            data.updateCurTask(task.id)
            if (prevTask == data.curTask) {
                hbox.style = """
                    -fx-border-color: """ + getTheme().second + """;
                    -fx-border-width: 2;
                    -fx-border-style: solid;
                    -fx-border-radius: 10px;
                    """.trimIndent()
                data.curTask = -1
            } else {
                hbox.style += "\n" + """
                    -fx-background-color: """ + getTheme().second + """;
                    -fx-background-radius: 10px;
                    """.trimIndent()
            }
        }

        btn_del.setOnMouseClicked {
            data.deleteItemByID(task.id)
            tasksVBox.children.remove(hbox)
            if (data.tasks.size == 0) {
                val lbl = Label("You have no tasks for this list. Create some!")
                lbl.font = globalFont
                tasksVBox.children.add(lbl)
            }
            user.convertToString()
            data.updateCurTask(task.id)
            dataChanged()
        }

        btn_edit.setOnMouseClicked {
            if (data.curTask != -1) {
                tasksVBox.children[data.curTask + 1].style = """
                    -fx-border-color: """ + getTheme().second + """;
                    -fx-border-width: 2;
                    -fx-border-style: solid;
                    -fx-border-radius: 10px;
                    """.trimIndent()
            }
            data.updateCurTask(task.id)
            hbox.style += "\n" + """
                -fx-background-color: """ + getTheme().second + """;
                -fx-background-radius: 10px;
                """.trimIndent()
            editTaskStage(task, tasksVBox, create_button)
        }
        hbox.alignment = Pos.CENTER
        return hbox
    }

    fun addVBoxNonTasks(create_button: Button, data: TaskList, title: String, tasksVBox: VBox) {
        val hbox = HBox(10.0)
       // val vbox = VBox(10.0)
        val childLabel = Label(title)
        childLabel.font = Font.font("Courier New", FontWeight.BOLD, 22.0)

//        val textField = TextField()
//        textField.promptText = "Search here!"

        val methods = FXCollections.observableArrayList("Default", "Priority Asc", "Priority Desc",
            "Title Asc", "Title Desc", "Due Date Asc", "Due Date Desc", "Date Created Asc", "Date Created Desc", "Difficulty Asc",
            "Difficulty Desc", "Completion")
        val sortBox = ComboBox(methods)
        sortBox.minWidth = 130.0

        val group = FXCollections.observableArrayList("High Priority", "Medium Priority", "Low Priority", "None", "Reset")
        val groupBox = ComboBox(group)
        groupBox.minWidth = 130.0


        if (!sortCalled) {
            sortBox.promptText = "Sorting Method"
        } else {
            sortBox.promptText = sortingMethodForBox
        }

        sortBox.setOnAction {

            groupBox.promptText = "Filter By"
            groupCalled = false
            if (sortBox.value == "Default") {
                sortCalled = false
                FunctionClass.sortTasksBy("default", data)
            } else {
                sortCalled = true
                sortingMethodForBox = sortBox.value
                var sortMethod = sortBox.value.filter { !it.isWhitespace() }
                FunctionClass.sortTasksBy("by${sortMethod}", data)
            }

            val updateVbox = createTasksVBox(create_button, data, title)
            boardViewHBox.children.clear()
            boardViewHBox.children.add(updateVbox)
            dataChanged()
        }

        if (!groupCalled) {
            groupBox.promptText = "Filter By"
        } else {
            groupBox.promptText = groupingMethodForBox
        }

        groupBox.setOnAction {
            sortBox.promptText = "Sorting Method"
            sortCalled = false
            if (groupBox.value == "Reset") {
                groupCalled = false
                val resetVBox = createTasksVBox(create_button, data, title)
                boardViewHBox.children.clear()
                boardViewHBox.children.add(resetVBox)
            } else {
                groupCalled = true
                groupingMethodForBox = groupBox.value
                val prio = when (groupBox.value) {
                    "High Priority" -> Priority.High
                    "Medium Priority" -> Priority.Medium
                    "Low Priority" -> Priority.Low
                    "None" -> null
                    else -> null
                }
                val filteredVBox = createFilteredTasksVBox(create_button, data, title, prio)
                boardViewHBox.children.clear()
                boardViewHBox.children.add(filteredVBox)
            }

        }

        hbox.children.addAll(childLabel, create_button, sortBox, groupBox)
        hbox.alignment = Pos.CENTER
//        vbox.children.addAll(hbox, textField)
//        vbox.alignment = Pos.CENTER

        //textField.maxWidth = 200.0

        tasksVBox.children.add(hbox)
    }

    fun createFilteredTasksVBox(create_button: Button, data : TaskList, title: String = "To do", priority: Priority?): VBox {

        // create a VBox
        var tasksVBox = VBox(25.0)
        addVBoxNonTasks(create_button, data, title, tasksVBox)

        // add tasks to VBox
        for (task in data.tasks) {
            if (task.priority == priority) {
                val hbox = createTaskHbox(task, data, tasksVBox, title, create_button)
                hbox.alignment = Pos.CENTER_LEFT
                tasksVBox.children.add(hbox)
            }
        }

        if (tasksVBox.children.size == 1) {
            val lbl = Label("No tasks met your criteria.")
            lbl.font = globalFont
            tasksVBox.children.add(lbl)
        }

        setDefaultButtonStyle(create_button)
        //Map create button to current tasklist
        create_button.setOnMouseClicked {
            createTaskStage(data, tasksVBox, create_button)
        }

        tasksVBox.padding = Insets(25.0)
        tasksVBox.style = """
            -fx-border-color: """ + getTheme().first + """;
            -fx-border-insets: 5;
            -fx-border-width: 2;
            -fx-border-style: solid;
            """.trimIndent()
        tasksVBox.alignment = Pos.CENTER
        return tasksVBox

    }

    fun createTasksVBox(create_button: Button, data : TaskList, title: String = "To do"): VBox {

        // create a VBox
        var tasksVBox = VBox(25.0)
        addVBoxNonTasks(create_button, data, title, tasksVBox)

        // add tasks to VBox
        for (task in data.tasks) {
            val hbox = createTaskHbox(task, data, tasksVBox, title, create_button)
            hbox.alignment = Pos.CENTER_LEFT
            tasksVBox.children.add(hbox)
        }

        if (data.curTask != -1) {
            tasksVBox.children[data.curTask + 1].style += "\n" + """
                -fx-background-color: """ + getTheme().second + """;
                -fx-background-radius: 10px;
            """.trimIndent()
        }

        if (data.tasks.size == 0) {
            val lbl = Label("You have no tasks for this list. Create some!")
            lbl.font = globalFont
            tasksVBox.children.add(lbl)
        }

        setDefaultButtonStyle(create_button)
        //Map create button to current tasklist
        create_button.setOnMouseClicked {
            createTaskStage(data, tasksVBox, create_button)
        }

        tasksVBox.padding = Insets(25.0)
        tasksVBox.style = """
            -fx-border-color: """ + getTheme().first + """;
            -fx-border-insets: 5;
            -fx-border-width: 2;
            -fx-border-style: solid;
            """.trimIndent()
        return tasksVBox
    }

    fun createEmptyVBox(msg : String): VBox {

        var emptyVBox = VBox(10.0)

        val label = Label(msg)
        label.font = globalFont

        emptyVBox.children.add(label)

        return emptyVBox

    }

    fun createSideBarVBox(): Pair<VBox, List<Button>>{
        //val icons = listOf("Profile")
        val sideBar = VBox(10.0)
        sideBar.style = """
            -fx-background-color:"""+ getTheme().second+ """;
        """
        sideBar.prefWidth = 80.0
        sideBar.alignment = Pos.TOP_CENTER
        sideBar.padding = Insets(10.0)
        val themeButton = ImageButton("/assets/icons/theme.png",30.0,30.0)
        val profileButton = ImageButton("/assets/icons/profile.png",30.0,30.0)
        val shopButton = ImageButton("/assets/icons/shop.png",30.0,30.0)
        setDefaultButtonStyle(themeButton)
        setDefaultButtonStyle(profileButton)
        setDefaultButtonStyle(shopButton)
        sideBar.children.addAll(themeButton, profileButton, shopButton)
        return sideBar to listOf(themeButton, profileButton, shopButton)
    }

    fun errorStage(errMsg : String) {
        val invalidDiffStage = Stage()
        invalidDiffStage.title = "Error"
        //label
        val errorMessage = Label(errMsg)
        errorMessage.font = globalFont
        errorMessage.isWrapText = true
        //button
        val exitDiffStageButton = Button("Exit")
        setDefaultButtonStyle(exitDiffStageButton)
        exitDiffStageButton.setOnMouseClicked {
            invalidDiffStage.hide()
        }
        //container
        val prioSceneContainer = BorderPane()
        prioSceneContainer.center = errorMessage
        prioSceneContainer.bottom = exitDiffStageButton
        prioSceneContainer.style = """
                    -fx-background-color:""" + getTheme().third + """;
                """
        //scene
        val invalidPriorityScene = Scene(prioSceneContainer,500.0, 300.0)

        invalidDiffStage.scene = invalidPriorityScene
        invalidDiffStage.x = user.x
        invalidDiffStage.y = user.y
        invalidDiffStage.show()
    }

    fun createTaskStage(data: TaskList, tasksVBox: VBox, create_button: Button) {
        val create_task_stage = Stage()
        create_task_stage.setTitle("Create Task")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)
        val instruction = Label("Only 'Title' is mandatory when creating a task.")
        instruction.font = globalFont

        val mainVBox = VBox(20.0)
        val mainHBox = HBox(5.0)
        val leftvbox = VBox(21.0)
        val rightvbox = VBox(16.0)
        leftvbox.alignment = Pos.CENTER_RIGHT
        rightvbox.alignment = Pos.CENTER_LEFT

        val label_title = Label("Title:")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.promptText = "Enter Title here"
        leftvbox.children.add(label_title)
        rightvbox.children.add(text_title)

        val label_desc = Label("Description:")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        leftvbox.children.add(label_desc)
        rightvbox.children.add(text_desc)

        val label_due = Label("Due Date:")
        label_due.font = globalFont
        val due_date = DatePicker()
        due_date.isEditable = false
        leftvbox.children.add(label_due)
        rightvbox.children.add(due_date)

        val label_prio = Label("Priority:")
        label_prio.font = globalFont
        val priority = FXCollections.observableArrayList("Low", "Medium", "High")
        val select_prio = ComboBox(priority)
        leftvbox.children.add(label_prio)
        rightvbox.children.add(select_prio)

        val label_diff = Label("Difficulty:")
        label_diff.font = globalFont
        val difficulty = FXCollections.observableArrayList("Easy", "Medium", "Hard")
        val select_diff = ComboBox(difficulty)
        leftvbox.children.add(label_diff)
        rightvbox.children.add(select_diff)

        val label_tags = Label("Tags:")
        label_tags.font = globalFont

        val strings: ObservableList<String> = FXCollections.observableArrayList()
        for (tag in user.tags) {
            strings.add(tag)
        }

//        for (i in 1..50) {
//            strings.add("Item $i")
//        }

        val labelAddTags = Label("Add/Delete Tags:")
        labelAddTags.font = globalFont
        val newTag = TextField()
        newTag.promptText = "Add/Delete tags here"

        var selected_tags = CheckComboBox(strings)
        selected_tags.title = "\t\t\tTags for this task"

        val addTagBtn = createAddButton()
        setDefaultButtonStyle(addTagBtn)
        val delTagBtn = createDeleteButton()
        setDefaultButtonStyle(delTagBtn)

        addTagBtn.setOnMouseClicked {
            if (newTag.text.trim() != "") {
                if (strings.size == 0) {
                    leftvbox.children.removeAt(6)
                    rightvbox.children.removeAt(6)
                }
                if (!strings.contains(newTag.text.trim())) {
                    strings.add(newTag.text.trim())
                }
                user.tags.add(newTag.text.trim())
            }
            newTag.clear()
        }

        val noTagsMsg1 = Label("You have no tags")
        val noTagsMsg2 = Label("to add. Create some below!")
        noTagsMsg1.font = globalFont
        noTagsMsg2.font = globalFont

        delTagBtn.setOnMouseClicked {
            if (newTag.text.trim() != "") {
                if (strings.size == 1 && strings.contains(newTag.text.trim())) {
                    leftvbox.children.add(6, noTagsMsg1)
                    rightvbox.children.add(6, noTagsMsg2)
                    selected_tags.checkModel.clearCheck(0)
                }

                strings.remove(newTag.text.trim())
                user.tags.remove(newTag.text.trim())
                for (item in selected_tags.checkModel.checkedItems) {
                    selected_tags.checkModel.check(item)
                }
                selected_tags.title = "\t\t\tTags for this task"
            }
            newTag.clear()
        }

        val hboxAddTags = HBox(5.0)
        hboxAddTags.children.addAll(newTag, addTagBtn, delTagBtn)

        leftvbox.children.add(label_tags)
        rightvbox.children.add(selected_tags)
        leftvbox.children.add(labelAddTags)
        rightvbox.children.add(hboxAddTags)


        mainHBox.children.addAll(leftvbox, rightvbox)
        mainHBox.alignment = Pos.TOP_CENTER
        mainVBox.children.addAll(instruction, mainHBox, btn)
        mainVBox.padding = Insets(5.0)
        mainVBox.alignment = Pos.CENTER


        if (strings.size == 0) {
            leftvbox.children.add(6, noTagsMsg1)
            rightvbox.children.add(6, noTagsMsg2)
        }

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of task can not be empty.")
            } else {
                val taskTags = mutableSetOf<String>()
                val addTags = selected_tags.checkModel.checkedItems
                for (tag in addTags) {
                    if (tag != null) {
                        taskTags.add(tag)
                    }
                }

                if (data.tasks.size == 0) {
                    tasksVBox.children.removeAt(1)
                }

                if (due_date.value == null) {
                    data.addItem(text_title.text.trim(), text_desc.text.trim(), "",
                        strToPrio(select_prio.value), strToDiff(select_diff.value), taskTags)
                } else {
                    data.addItem(text_title.text.trim(), text_desc.text.trim(), due_date.value.toString(),
                        strToPrio(select_prio.value), strToDiff(select_diff.value), taskTags)
                }

                val curTask = data.tasks[data.tasks.size - 1]

                var hbox = createTaskHbox(curTask, data, tasksVBox, data.title, create_button)
                tasksVBox.children.add(hbox)
                create_task_stage.close()
                dataChanged()
            }
        }

        mainVBox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(mainVBox, 700.0, 450.0)
        create_task_stage.scene = scene
        create_task_stage.x = user.x
        create_task_stage.y = user.y
        create_task_stage.show()

        val spacer = Region()
        spacer.prefWidth = mainHBox.width - leftvbox.width - rightvbox.width - 200.0
        mainHBox.children.add(spacer)

        selected_tags.minWidth = 240.0
        text_title.maxWidth = 240.0
        text_desc.maxWidth = 240.0
        selected_tags.maxWidth = 240.0
        newTag.maxWidth = 240.0
    }

    fun createTaskListStage(taskListVBox : VBox, btn_create_task_to_do: Button) {
        val tasklist_stage = Stage()
        tasklist_stage.setTitle("Create List")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)
        val instruction = Label("Only 'Title' is mandatory when creating a list.")
        instruction.font = globalFont

        val mainVBox = VBox(20.0)
        val mainHBox = HBox(5.0)
        val leftvbox = VBox(21.0)
        val rightvbox = VBox(16.0)
        leftvbox.alignment = Pos.CENTER_RIGHT
        rightvbox.alignment = Pos.CENTER_LEFT

        val label_title = Label("Title: ")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.promptText = "Enter Title here"
        leftvbox.children.add(label_title)
        rightvbox.children.add(text_title)

        val label_desc = Label("Description: ")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        leftvbox.children.add(label_desc)
        rightvbox.children.add(text_desc)

        mainHBox.children.addAll(leftvbox, rightvbox)
        mainVBox.children.addAll(instruction, mainHBox, btn)
        mainVBox.padding = Insets(10.0)
        mainHBox.alignment = Pos.TOP_CENTER
        mainVBox.alignment = Pos.CENTER

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of new list can not be empty.")
            } else {
                user.addList(text_title.text, text_desc.text)

                var curTaskList = user.lists[user.lists.size - 1]

                createListHbox(curTaskList, taskListVBox, btn_create_task_to_do, false)

                if (user.lastUsedList == -1) {
                    user.lastUsedList = 0

                    // set it here
                    toDoVBox = createTasksVBox(btn_create_task_to_do, curTaskList, curTaskList.title)
                    boardViewHBox.children.clear()
                    boardViewHBox.children.add(toDoVBox)

                }

                dataChanged()
                text_desc.clear()
                text_title.clear()
                tasklist_stage.close()
            }
        }

        mainVBox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(mainVBox, 500.0, 300.0)
        tasklist_stage.scene = scene
        tasklist_stage.x = user.x
        tasklist_stage.y = user.y
        tasklist_stage.show()

        val spacer = Region()
        spacer.prefWidth = mainHBox.width - leftvbox.width - rightvbox.width - 150.0
        mainHBox.children.add(spacer)


    }

    // function for deselecting, weird crutch with javafx
    fun deselect(textfield : TextField) {
        Platform.runLater {
            if (textfield.text.length > 0 &&
                textfield.selectionProperty().get().end == 0
            ) {
                deselect(textfield)
            } else {
                textfield.selectEnd()
                textfield.deselect()
            }
        }
    }

    fun editTaskListStage(curTaskList: TaskList, create_button: Button, taskListVBox : VBox) {
        val tasklist_stage = Stage()
        tasklist_stage.setTitle("Edit List")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)

        val mainVBox = VBox(20.0)
        val mainHBox = HBox(5.0)
        val leftvbox = VBox(21.0)
        val rightvbox = VBox(16.0)
        leftvbox.alignment = Pos.CENTER_RIGHT
        rightvbox.alignment = Pos.CENTER_LEFT

        val label_title = Label("Title: ")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.promptText = "Enter Title here"
        text_title.text = curTaskList.title
        leftvbox.children.add(label_title)
        rightvbox.children.add(text_title)

        val label_desc = Label("Description: ")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        if (curTaskList.desc != "") {
            text_desc.text = curTaskList.desc
        }
        leftvbox.children.add(label_desc)
        rightvbox.children.add(text_desc)

        mainHBox.children.addAll(leftvbox, rightvbox)
        mainVBox.children.addAll(mainHBox, btn)
        mainVBox.padding = Insets(10.0)
        mainHBox.alignment = Pos.TOP_CENTER
        mainVBox.alignment = Pos.CENTER

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of a list can not be empty.")
            } else {
                curTaskList.title = text_title.text.trim()
                curTaskList.desc = text_desc.text.trim()

                createListHbox(curTaskList, taskListVBox, create_button, true)

                // update title of list in middle pane if user is editing active list
                if (user.lastUsedList == user.findIdx(curTaskList.id)) {
                    toDoVBox = createTasksVBox(create_button, curTaskList, curTaskList.title)
                    boardViewHBox.children.clear()
                    boardViewHBox.children.add(toDoVBox)
                }

                dataChanged()
                text_desc.clear()
                text_title.clear()
                tasklist_stage.close()
            }
        }

        mainVBox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(mainVBox, 500.0, 300.0)
        tasklist_stage.scene = scene
        tasklist_stage.x = user.x
        tasklist_stage.y = user.y
        tasklist_stage.show()

        deselect(text_title)

        val spacer = Region()
        spacer.prefWidth = mainHBox.width - leftvbox.width - rightvbox.width - 150.0
        mainHBox.children.add(spacer)

    }

    fun createListHbox(curTaskList: TaskList, taskListVBox: VBox, btn_create_task_to_do: Button, edit : Boolean) {

        val title = Button(curTaskList.title)
        setDefaultButtonStyle(title)
        title.minWidth = 100.0
        val delBtn = createDeleteButton()
        setDefaultButtonStyle(delBtn)
        val editBtn = createDetailsButton()
        setDefaultButtonStyle(editBtn)

        val spacer = Pane()
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS)
        spacer.setMinSize(10.0, 10.0)

        val hbox = HBox(5.0, title, spacer, delBtn, editBtn)
        hbox.alignment = Pos.CENTER
        hbox.padding = Insets(5.0)

        if (edit) {
            val idx = user.findIdx(curTaskList.id)
            taskListVBox.children.removeAt(idx + 1)
            taskListVBox.children.add(idx + 1, hbox)
        } else {
            if (user.lists.size + 1 == taskListVBox.children.size) {
                taskListVBox.children.add(taskListVBox.children.size - 1, hbox)
            } else {
                taskListVBox.children.add(taskListVBox.children.size, hbox)
            }
        }

        title.setOnMouseClicked {
            toDoVBox = createTasksVBox(btn_create_task_to_do, curTaskList, curTaskList.title)
            boardViewHBox.children.clear()
            boardViewHBox.children.add(toDoVBox)
            user.updateActiveList(curTaskList.id)
        }

        hbox.onDragEntered = EventHandler<DragEvent?> { event ->
            if (event.gestureSource !== hbox) hbox.opacity = 0.5
        }

        hbox.onDragExited = EventHandler<DragEvent?> { _ ->
            hbox.opacity = 1.0
        }

        hbox.onDragOver = EventHandler<DragEvent?> {event ->
            /* data is dragged over the target */
            /* accept it only if it is not dragged from the same node and if it has a string data */
            if (event.dragboard.hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            }
            event.consume()
        }
        hbox.onDragDropped = EventHandler<DragEvent?> {event ->
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            val db = event.dragboard
            val props = db.string.split(",").toTypedArray()
            curTaskList.addItem(
            title=props[0], desc = props[1], dueDate = props[2],
            priority = strToPrio(props[3]), difficulty = strToDiff(props[4])
            )

            if (user.lastUsedList == user.findIdx(curTaskList.id)) {
                toDoVBox = createTasksVBox(btn_create_task_to_do, curTaskList, curTaskList.title)
                boardViewHBox.children.clear()
                boardViewHBox.children.add(toDoVBox)
            }

            /* let the source know whether the string was successfully transferred and used */
            event.consume()
        }

        delBtn.setOnMouseClicked {
            deleteList(curTaskList.id, taskListVBox, hbox)
            dataChanged()
        }

        editBtn.setOnMouseClicked {
            editTaskListStage(curTaskList, btn_create_task_to_do, taskListVBox)
        }

    }

    fun strToDiff(s: String?): Difficulty? =
        when(s) {
            "Hard" -> Difficulty.Hard
            "Medium" -> Difficulty.Medium
            "Easy" -> Difficulty.Easy
            else -> null
        }



    fun strToPrio(s: String?): Priority? =
        when(s) {
            "High" -> Priority.High
            "Medium" -> Priority.Medium
            "Low" -> Priority.Low
            else -> null
        }

    fun editTaskStage(task: Task, tasksVBox: VBox, create_button: Button) {
        val taskEditStage = Stage()
        taskEditStage.setTitle("Edit Task")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)

        val mainVBox = VBox(10.0)
        val mainHBox = HBox(5.0)
        val leftvbox = VBox(21.0)
        val rightvbox = VBox(16.0)
        leftvbox.alignment = Pos.CENTER_RIGHT
        rightvbox.alignment = Pos.CENTER_LEFT

        val label_title = Label("Title:")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.text = task.title
        text_title.promptText = "Enter Title here"
        leftvbox.children.add(label_title)
        rightvbox.children.add(text_title)

        val label_desc = Label("Description:")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        if (task.desc != "") {
            text_desc.text = task.desc
        }
        leftvbox.children.add(label_desc)
        rightvbox.children.add(text_desc)

        val label_due = Label("Due Date:")
        label_due.font = globalFont
        val due_date = DatePicker()
        if (task.dueDate != "") {
            val (yr, month, day) = task.dueDate.split("-")
            due_date.value = LocalDate.of(yr.toInt(), month.toInt(), day.toInt())
        }
        due_date.isEditable = false
        leftvbox.children.add(label_due)
        rightvbox.children.add(due_date)

        val label_date_created = Label("Date Created (yyyy-mm-dd):")
        label_date_created.font = globalFont
        val date_created = TextField(task.dateCreated)
        date_created.isEditable = false
        leftvbox.children.add(label_date_created)
        rightvbox.children.add(date_created)

        val label_prio = Label("Priority:")
        label_prio.font = globalFont
        val priority = FXCollections.observableArrayList("Low", "Medium", "High")
        val select_prio = ComboBox(priority)
        if (task.priority != null) {
            when (task.priority) {
                Priority.Low -> select_prio.selectionModel.select(0)
                Priority.Medium -> select_prio.selectionModel.select(1)
                Priority.High -> select_prio.selectionModel.select(2)
                else -> {}
            }
        }
        leftvbox.children.add(label_prio)
        rightvbox.children.add(select_prio)

        val label_diff = Label("Difficulty:")
        label_diff.font = globalFont
        val difficulty = FXCollections.observableArrayList("Easy", "Medium", "Hard")
        val select_diff = ComboBox(difficulty)
        if (task.difficulty != null) {
            when (task.difficulty) {
                Difficulty.Easy -> select_diff.selectionModel.select(0)
                Difficulty.Medium -> select_diff.selectionModel.select(1)
                Difficulty.Hard -> select_diff.selectionModel.select(2)
                else -> {}
            }
        }
        leftvbox.children.add(label_diff)
        rightvbox.children.add(select_diff)

        val label_tags = Label("Tags:")
        label_tags.font = globalFont

        val strings: ObservableList<String> = FXCollections.observableArrayList()
        for (tag in user.tags) {
            strings.add(tag)
        }

        val labelAddTags = Label("Add/Delete Tags:")
        labelAddTags.font = globalFont
        val newTag = TextField()
        newTag.promptText = "Add/Delete tags here"

        var selected_tags = CheckComboBox(strings)
        selected_tags.title = "\t\t\tTags for this task"

        for (tag in strings) {
            if (task.tags.contains(tag)) {
                selected_tags.checkModel.check(tag)
            }
        }

        val addTagBtn = createAddButton()
        setDefaultButtonStyle(addTagBtn)
        val delTagBtn = createDeleteButton()
        setDefaultButtonStyle(delTagBtn)

        addTagBtn.setOnMouseClicked {
            if (newTag.text.trim() != "") {
                if (strings.size == 0) {
                    leftvbox.children.removeAt(7)
                    rightvbox.children.removeAt(7)
                }
                if (!strings.contains(newTag.text.trim())) {
                    strings.add(newTag.text.trim())
                }
                user.tags.add(newTag.text.trim())
            }
            newTag.clear()
        }

        val noTagsMsg1 = Label("You have no tags")
        val noTagsMsg2 = Label("to add. Create some below!")
        noTagsMsg1.font = globalFont
        noTagsMsg2.font = globalFont

        delTagBtn.setOnMouseClicked {
            if (newTag.text.trim() != "") {
                if (strings.size == 1 && strings.contains(newTag.text.trim())) {
                    leftvbox.children.add(7, noTagsMsg1)
                    rightvbox.children.add(7, noTagsMsg2)
                    selected_tags.checkModel.clearCheck(0)
                }

                strings.remove(newTag.text.trim())
                user.tags.remove(newTag.text.trim())
                for (item in selected_tags.checkModel.checkedItems) {
                    selected_tags.checkModel.check(item)
                }
                selected_tags.title = "\t\t\tTags for this task"
            }
            newTag.clear()
        }

        val hboxAddTags = HBox(5.0)
        hboxAddTags.children.addAll(newTag, addTagBtn, delTagBtn)

        leftvbox.children.add(label_tags)
        rightvbox.children.add(selected_tags)
        leftvbox.children.add(labelAddTags)
        rightvbox.children.add(hboxAddTags)


        mainHBox.children.addAll(leftvbox, rightvbox)
        mainHBox.alignment = Pos.TOP_CENTER
        mainVBox.children.addAll(mainHBox, btn)
        mainVBox.padding = Insets(5.0)
        mainVBox.alignment = Pos.CENTER

        if (strings.size == 0) {
            leftvbox.children.add(7, noTagsMsg1)
            rightvbox.children.add(7, noTagsMsg2)
        }

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of task can not be empty.")
            } else {
                val addTags = selected_tags.checkModel.checkedItems

                task.tags.clear()
                for (tag in addTags) {
                    if (tag != null) {
                        task.tags.add(tag)
                    }
                }

                task.title = text_title.text.trim()
                task.desc = text_desc.text.trim()
                if (due_date.value == null) {
                    task.dueDate = ""
                } else {
                    task.dueDate = due_date.value.toString()
                }

                task.priority = strToPrio(select_prio.value)
                task.difficulty = strToDiff(select_diff.value)
                task.calcCoinValue() // update reward coins

                val listOfTask = user.lists[user.lastUsedList]

                tasksVBox.children.removeAt(listOfTask.curTask + 1)
                var hbox = createTaskHbox(task, listOfTask, tasksVBox, listOfTask.title, create_button)
                hbox.style += "\n" + """
                    -fx-background-color: """ + getTheme().second + """;
                    -fx-background-radius: 10px;
                    """.trimIndent()
                tasksVBox.children.add(listOfTask.curTask + 1, hbox)
                taskEditStage.close()
                dataChanged()
            }
        }

        mainVBox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(mainVBox, 700.0, 450.0)
        taskEditStage.scene = scene
        taskEditStage.x = user.x
        taskEditStage.y = user.y
        taskEditStage.show()

        val spacer = Region()
        spacer.prefWidth = mainHBox.width - leftvbox.width - rightvbox.width - 125.0
        mainHBox.children.add(spacer)

        deselect(text_title)

        selected_tags.minWidth = 240.0
        text_title.maxWidth = 240.0
        date_created.maxWidth = 92.0
        text_desc.maxWidth = 240.0
        selected_tags.maxWidth = 240.0
        newTag.maxWidth = 240.0


    }

    fun showProfileScreen(homeStage: Stage?, homeScene: Scene): Scene {
//        var user = restoreUserData(dataFileName)
        var profileVBox = VBox(10.0)
        var scene = Scene(profileVBox, 600.0, 600.0)

        var bannerCopy = ImageView()
        val bannerPath = "/assets/banners/" + user.bannerRank + ".png"
        val banner = Image(bannerPath)
        bannerCopy.image = banner
        bannerCopy.fitWidth = 125.0
        bannerCopy.fitHeight =160.0

        val path = "/assets/" + user.profileImageName
        val profileImage = Image(path)
        val profileImageView = ImageView()
        profileImageView.image = profileImage
        profileImageView.fitWidth = 120.0
        profileImageView.fitHeight = 120.0

        val profileStackPane = StackPane()
        profileStackPane.children.addAll(profileImageView, bannerCopy)

        val userInfoLabel = Label("User Information")
        userInfoLabel.font = globalFont
        var statisticsHBox = HBox(10.0)
        val titles = listOf("Current coins", "Longest Streak", "Tasks Done Today", "Rank")
        val fields = listOf(user.wallet, user.longestStreak, user.tasksDoneToday, user.bannerRank)

        for (i in 0..titles.size - 1) {
            val title = Label(titles[i])
            title.font = globalFont
            val field = Label(fields[i].toString())
            val statVBox = VBox(5.0)
            statVBox.children.addAll(title, field)
            statisticsHBox.children.add(statVBox)
        }
        statisticsHBox.alignment = Pos.CENTER

        val unlockablesLabel = Label("Unlockables (" + user.purchasedItems.size + ") - Click to select")
        unlockablesLabel.font = globalFont

        var unlockablesHBox = FlowPane(Orientation.HORIZONTAL)
        unlockablesHBox.hgap = 10.0
        unlockablesHBox.vgap = 10.0

        for (item in user.purchasedItems) {
            val childHBox = createShopItemVBox(item, 100.0)
            childHBox.onMouseEntered = EventHandler<MouseEvent?> {
                childHBox.opacity = 0.5
            }

            childHBox.onMouseExited = EventHandler<MouseEvent?> {
                childHBox.opacity = 1.0
            }

            childHBox.onMouseClicked = EventHandler<MouseEvent?> {
                user.profileImageName = item.name+".png"
                createProfilePic()
                homeStage?.scene = homeScene
            }

            unlockablesHBox.children.add(childHBox)

        }
        unlockablesHBox.alignment = Pos.CENTER

        val backButton = Button("Back")
        setDefaultButtonStyle(backButton)
        backButton.onMouseClicked = EventHandler<MouseEvent?> {
            homeStage?.scene = homeScene
        }

        profileVBox.children.addAll(profileStackPane, userInfoLabel, statisticsHBox, unlockablesLabel, unlockablesHBox, backButton)
        profileVBox.alignment = Pos.TOP_CENTER

        profileVBox.style = """
            -fx-background-color:""" + base2 + """;
        """

        // set flowpane to same width as window
        unlockablesHBox.prefWidthProperty().bind(scene.widthProperty())

        return scene
    }

    fun createShopItemVBox(item: Item, size: Double): VBox {
        val vBox = VBox(10.0)
        //Image
        val path = "/assets/" + item.name + ".png"
        val image = Image(path)
        val imageView = ImageView()
        imageView.image = image
        imageView.fitWidth = size
        imageView.fitHeight = size
        //Title
        val label = Label(item.name)
        label.font = globalFont
        val titleBox = HBox(10.0, label)

        vBox.children.addAll(imageView, titleBox)
        return vBox
    }
    fun showTaskCompletionStage(task: Task) {
        user.completeTask(task) // count new task completed
        updateBanner() // update banner displayed

        val taskCompletionStage = Stage()
        taskCompletionStage.setTitle("Task Completed!")
        val btn = Button("Exit")
        setDefaultButtonStyle(btn)

        val hbox_title = HBox(20.0)
        val label_title = Label("Congrats on getting " + task.title + " done!")
        label_title.font = globalFont
        hbox_title.alignment = Pos.CENTER
        hbox_title.children.addAll(label_title)

        val hbox_desc = HBox(20.0)
        var coinValue = (task.rewardCoins * user.multiplier).toInt()
        val label_desc = Label("Here's " + coinValue + " TaskCoins as a reward!")
        label_desc.font = globalFont
        hbox_desc.alignment = Pos.CENTER
        hbox_desc.children.addAll(label_desc)

        val hbox_reward = HBox(20.0)
        val label_reward = Label("+10 ")
        label_reward.setFont(Font.font("Courier New", 32.0));
        hbox_reward.alignment = Pos.CENTER
        hbox_reward.children.addAll(label_desc)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, label_desc, btn)

        vbox.alignment = Pos.CENTER

        btn.setOnMouseClicked {
            taskCompletionStage.close()
        }
        vbox.style = """
            -fx-background-color:""" + getTheme().second + """;
        """
        val scene = Scene(vbox, 400.0, 150.0)
        taskCompletionStage.scene = scene
        taskCompletionStage.show()

        // close completion automatically after 3 seconds
        val timer = Timer();
        val countdown: TimerTask = object : TimerTask() {
            var counter = 3
            override fun run() {
                Platform.runLater(Runnable {
                    // close task after
                    if (counter == 0) {
                        taskCompletionStage.close()
                    } else {
                        btn.text = "Exit ($counter)"
                        counter--
                    }
                })
            }
        }
        timer.scheduleAtFixedRate(countdown,0,1000L)
    }

    fun createShopScene(homeStage: Stage?, homeScene: Scene): Scene {
        val borderPane = BorderPane()
        val shopScene = Scene(borderPane, 900.0, 600.0)

        //HEADER
        val labelHeader = Label("My Shop")
        labelHeader.font = Font.font("Courier New", FontWeight.BOLD, 36.0)
        val hboxHeader = HBox()
        hboxHeader.alignment = Pos.CENTER
        hboxHeader.padding = Insets(20.0, 0.0, 0.0, 0.0)
        hboxHeader.children.addAll(labelHeader)
        hboxHeader.style = """
            -fx-background-color:""" + getTheme().second + """;
        """
        borderPane.top = hboxHeader
        //End Header

        //FOOTER
        val backButton = Button("Back")
        backButton.setOnMouseClicked {
            homeStage?.scene = homeScene
        }
        setDefaultButtonStyle(backButton)

        val footerHbox = HBox()
        footerHbox.children.add(backButton)
        footerHbox.padding = Insets(0.0, 0.0, 20.0, 20.0)
        footerHbox.style = """
            -fx-background-color:""" + getTheme().second + """;
        """
        borderPane.bottom = footerHbox
        //END FOOTER

        //Main
        val flowPane = FlowPane()
        val scrollPane = ScrollPane()
        flowPane.padding = Insets(30.0, 20.0, 30.0, 60.0)
        flowPane.vgap = 10.0
        flowPane.hgap = 30.0
        flowPane.orientation = Orientation.VERTICAL
        scrollPane.content = flowPane
        flowPane.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        flowPane.prefHeightProperty().bind(scrollPane.heightProperty())
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty())
        for (child in store.items){
            val (childBox, purchaseBtn) = createShopItem(child)
            purchaseBtn.setOnMouseClicked {
                store.buyItem(child.id, user)
                saveStoreData(store, storeFileName)
                saveUserData(user)
                flowPane.children.remove(childBox)
                homeStage?.scene = createShopScene(homeStage, homeScene)
            }
            setDefaultButtonStyle(purchaseBtn)
            if(user.purchasedItems.filter{it.id == child.id}.isNullOrEmpty()) {
                flowPane.children.add(childBox)
            }
        }
        borderPane.center = scrollPane

        if (debugMode) {
            borderPane.style = debugCss
            flowPane.style = debugCss
            scrollPane.style = debugCss
            hboxHeader.style = debugCss
        }
        //End Main
        return shopScene
    }

    fun createShopItem(item: Item): Pair<VBox, Button> {
        val vBox = createShopItemVBox(item, 120.0)
        // Purchase
        val text = Text(item.price.toString() + " C")
        text.font = globalFont
        val purchaseBtn = Button("Buy")
        setDefaultButtonStyle(purchaseBtn)

        val purchaseBox = HBox(20.0, text, purchaseBtn)

        vBox.children.addAll(purchaseBox)
        return vBox to purchaseBtn
    }

}
