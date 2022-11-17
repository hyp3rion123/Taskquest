package taskquest.app.javafx

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.shape.Line
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import taskquest.app.main
import org.controlsfx.control.CheckComboBox
import taskquest.utilities.controllers.SaveUtils.Companion.restoreStoreData
import taskquest.utilities.controllers.SaveUtils.Companion.restoreStoreDataFromText
import taskquest.utilities.controllers.SaveUtils.Companion.restoreUserData
import taskquest.utilities.controllers.SaveUtils.Companion.saveStoreData
import taskquest.utilities.controllers.SaveUtils.Companion.saveUserData
import taskquest.utilities.models.*
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.io.File
import javafx.beans.value.ChangeListener
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
    fun dataChanged() {
        user.convertToString()
        saveUserData(user, dataFileName)
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

        if (!File(dataFileName).exists()) {
            File(dataFileName).createNewFile()
            saveUserData(user, dataFileName)
        }
        user = restoreUserData(dataFileName)

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
        profileButton.setOnMouseClicked {
            showProfileScreen(user);
        }
        var shopButton = buttonList[2]

        val mainTasksSection = VBox(20.0, headerHBox, boardViewScroll)
        mainTasksSection.padding = Insets(0.0, 0.0, 0.0, 0.0)
        mainTasksSection.style = """
            -fx-background-color:""" + getTheme().third + """;
        """

        val taskListVBox = createTaskListVBox(user.lists, createTaskButton)

        val mainScreenPane = BorderPane()
        mainScreenPane.right = taskListVBox
        mainScreenPane.center = mainTasksSection
        mainScreenPane.left = sideBarVBox

        var mainScene = Scene(mainScreenPane, 900.0, 600.0)

        shopButton.setOnMouseClicked {
            mainStage?.scene = createShopScene(mainStage, mainScene) //created every time for refresh purposes
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

    fun updateBanner() {
        val bannerPath = "../assets/banners/" + user.bannerRank + ".png"
        val banner = Image(File(bannerPath).toURI().toString())
        bannerImageView.image = banner
    }

    fun createBanner(): VBox {
        val vbox = VBox(10.0)

        val bannerPath = "../assets/banners/" + user.bannerRank + ".png"
        val banner = Image(File(bannerPath).toURI().toString())
        bannerImageView.image = banner
        bannerImageView.fitWidth = 200.0
        bannerImageView.fitHeight = 100.0

        var headerLabel = Label("Welcome back, USER_NAME.")
        headerLabel.alignment = Pos.CENTER
        headerLabel.font = globalFont

        var headerHBox = HBox(10.0, headerLabel)
        headerHBox.alignment = Pos.CENTER


        vbox.children.addAll(bannerImageView, headerHBox)
        vbox.alignment = Pos.TOP_CENTER

        return vbox
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
        val taskListVBox = VBox(10.0)
        taskListVBox.alignment = Pos.TOP_CENTER
        taskListVBox.style = """
            -fx-background-color:""" + getTheme().second + """;
        """

        val searchBarLabel = Label("Task List Search bar")
        searchBarLabel.font = globalFont
        searchBarLabel.style = """
            -fx-background-color:"""+ getTheme().first+ """;
            -fx-text-fill: white;
        """

        taskListVBox.children.add(searchBarLabel)

        val textField = TextField()
        textField.setPromptText("Search here!")
        taskListVBox.children.add(textField)

        // add buttons to VBox for each list
        for (taskList in data) {
            createListHbox(taskList, taskListVBox, btn_create_task_to_do)
        }

        val createTaskListMenu = createTaskListStage(taskListVBox, btn_create_task_to_do)

        val addList = Button("New List")
        setDefaultButtonStyle(addList)
        taskListVBox.children.add(addList)
        addList.setOnMouseClicked {
            createTaskListMenu.show()
            println("Show create a list")
        }

        return taskListVBox
    }

    fun deleteList(id : Int, taskListVBox : VBox, hbox : HBox) {
        // delete list in the backend
        user.deleteList(id)
        taskListVBox.children.remove(hbox)
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

    fun createTaskHbox(task: Task, data:TaskList, tasksVBox: VBox, title: String, create_button: Button): HBox {
        val hbox = HBox(5.0)
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
        var btn_info = createDetailsButton()
        setDefaultButtonStyle(btn_del)
        setDefaultButtonStyle(btn_info)
        hbox.children.addAll(c, taskTitle, spacer, btn_del, btn_info)
        hbox.setPrefSize(400.0, 50.0)
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
                    child.alignment = Pos.TOP_LEFT
                    tasksVBox.children.add(child)
                }
            }
            /* let the source know whether the string was successfully transferred and used */
            event.isDropCompleted = success
            event.consume()
        }

        btn_del.setOnMouseClicked {
            data.deleteItemByID(task.id)
            tasksVBox.children.remove(hbox)
            user.convertToString()
            dataChanged()
        }
        btn_info.setOnMouseClicked {
            showTaskInfoStage(task)
        }
        hbox.alignment = Pos.CENTER
        return hbox
    }

    fun addVBoxNonTasks(create_button: Button, data: TaskList, title: String, tasksVBox: VBox) {
        val hbox = HBox(10.0)
        val vbox = VBox(10.0)
        val childLabel = Label("$title (${data.tasks.size})")
        childLabel.font = globalFont

        val textField = TextField()
        textField.promptText = "Search here!"
        vbox.children.addAll(childLabel, textField)

        hbox.children.addAll(vbox, create_button)
        hbox.alignment = Pos.BOTTOM_CENTER

        tasksVBox.children.add(hbox)
    }

    fun createTasksVBox(create_button: Button, data : TaskList, title: String = "To do"): VBox {

        // create a VBox
        var tasksVBox = VBox(10.0)
        addVBoxNonTasks(create_button, data, title, tasksVBox)

        // add tasks to VBox
        for (task in data.tasks) {
            val hbox = createTaskHbox(task, data, tasksVBox, title, create_button)
            hbox.alignment = Pos.TOP_LEFT
            tasksVBox.children.add(hbox)
        }

        //Map create button to current tasklist
        create_button.setOnMouseClicked {
            println("Always prints here")
            createTaskStage(data, tasksVBox, create_button)
        }
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
        val themeButton = ImageButton("../assets/icons/theme.png",30.0,30.0)
        val profileButton = ImageButton("../assets/icons/profile.png",30.0,30.0)
        val shopButton = ImageButton("../assets/icons/shop.png",30.0,30.0)
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
        invalidDiffStage.show()
    }

    fun createTaskStage(data: TaskList, tasksVBox: VBox, create_button: Button) {
        val create_task_stage = Stage()
        create_task_stage.setTitle("Create Task")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)
        val instruction = Label("Only 'Title' is mandatory when creating a task.")

        val hbox_title = HBox(20.0)
        val label_title = Label("Title")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.promptText = "Enter Title here"
        hbox_title.children.addAll(label_title, text_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        hbox_desc.children.addAll(label_desc, text_desc)

        val hbox_due = HBox(20.0)
        val label_due = Label("Due Date: ")
        label_due.font = globalFont
        val due_date = DatePicker()
        due_date.isEditable = false
        hbox_due.children.addAll(label_due, due_date)

        val hbox_prio = HBox(20.0)
        val label_prio = Label("Priority")
        label_prio.font = globalFont
        val priority = FXCollections.observableArrayList("Low", "Medium", "High")
        val select_prio = ComboBox(priority)
        hbox_prio.children.addAll(label_prio, select_prio)

        val hbox_diff = HBox(20.0)
        val label_diff = Label("Difficulty")
        label_diff.font = globalFont
        val difficulty = FXCollections.observableArrayList("Easy", "Medium", "Hard")
        val select_diff = ComboBox(difficulty)
        hbox_diff.children.addAll(label_diff, select_diff)

        val hbox_tags = HBox(20.0)
        val label_tags = Label("Tags")
        label_tags.font = globalFont

        val strings: ObservableList<String> = FXCollections.observableArrayList()
        for (tag in user.tags) {
            strings.add(tag)
        }

//        for (i in 1..50) {
//            strings.add("Item $i")
//        }

        val selected_tags = CheckComboBox(strings)

        if (strings.size == 0) {
            selected_tags.title = "No tags to select"
        }

        hbox_tags.children.addAll(label_tags, selected_tags)

        val vbox = VBox(10.0)
        vbox.children.addAll(instruction, hbox_title, hbox_desc, hbox_due, hbox_prio, hbox_diff, hbox_tags, btn)
        vbox.padding = Insets(10.0)

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of task can not be empty.")
            } else {
                val taskTags = mutableSetOf<String>()
                val addTags = selected_tags.checkModel.checkedItems
                for (tag in addTags) {
                    taskTags.add(tag)
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
//                val title = Label(text_title.text.trim())
//                title.font = globalFont
//                val c = CheckBox()
//                c.isSelected = false
//                var btn_delete = Button("X")
//                val btn_info = Button("Info")
//                val hbox = HBox(5.0, c, title, btn_delete, btn_info)
//                setDefaultButtonStyle(btn_delete)
//                setDefaultButtonStyle(btn_info)
//                btn_delete.setOnMouseClicked {
//                    data.deleteItemByID(curTask.id)
//                    tasksVBox.children.remove(hbox)
//                    dataChanged()
//                }
//                btn_info.setOnMouseClicked {
//                    showTaskInfoStage(curTask)
//                }
//                c.setOnMouseClicked {
//                    curTask.complete = !curTask.complete
//                    dataChanged()
//                }
                tasksVBox.children.add(hbox)
                create_task_stage.close()
                dataChanged()
            }
        }
        vbox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(vbox, 700.0, 400.0)
        create_task_stage.scene = scene
        create_task_stage.show()

        create_task_stage.widthProperty().addListener(ChangeListener {
                _, _, newValue ->  selected_tags.maxWidth = newValue.toDouble() - (label_tags.width + 50)
        })

        selected_tags.maxWidth = create_task_stage.width - (label_tags.width + 50)
    }

    fun createTaskListStage(taskListVBox : VBox, btn_create_task_to_do: Button) : Stage {
        val tasklist_stage = Stage()
        tasklist_stage.setTitle("Create List")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)

        val hbox_title = HBox(20.0)
        val label_title = Label("Title")
        label_title.font = globalFont
        val text_title = TextField()
        text_title.promptText = "Enter Title here"
        hbox_title.children.addAll(label_title, text_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        hbox_desc.children.addAll(label_desc, text_desc)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, btn)

        btn.setOnMouseClicked {
            if (text_title.text.trim() == "") {
                errorStage("Title of new list can not be empty.")
            } else {
                user.addList(text_title.text, text_desc.text)

                var curTaskList = user.lists[user.lists.size - 1]

                createListHbox(curTaskList, taskListVBox, btn_create_task_to_do)

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

        vbox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(vbox, 700.0, 400.0)
        tasklist_stage.scene = scene
        return tasklist_stage
    }

    fun createListHbox(curTaskList: TaskList, taskListVBox: VBox, btn_create_task_to_do: Button) {

        val title = Button(curTaskList.title)
        setDefaultButtonStyle(title)
        val delBtn = Button("-")
        setDefaultButtonStyle(delBtn)
        val hbox = HBox(2.0, title, delBtn)
        taskListVBox.children.add(2, hbox)
        title.setOnMouseClicked {
            toDoVBox = createTasksVBox(btn_create_task_to_do, curTaskList, curTaskList.title)
            boardViewHBox.children.clear()
            boardViewHBox.children.add(toDoVBox)
            user.updateActiveList(curTaskList.id)
            println("Selected list: ${user.lastUsedList}")
        }
        delBtn.setOnMouseClicked {
            deleteList(curTaskList.id, taskListVBox, hbox)
            dataChanged()
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

    fun showTaskInfoStage(task: Task) {
        val taskInfoStage = Stage()
        taskInfoStage.setTitle("Task Info")
        val btn = Button("Exit")
        setDefaultButtonStyle(btn)

        val hbox_title = HBox(20.0)
        val label_title = Label("Title: " + task.title)
        label_title.font = globalFont
        hbox_title.children.addAll(label_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description: " + task.desc)
        label_desc.font = globalFont
        hbox_desc.children.addAll(label_desc)

        val hbox_due = HBox(20.0)
        val label_due = Label("Due Date: " + task.dueDate)
        label_due.font = globalFont
        hbox_due.children.addAll(label_due)

        val hbox_prio = HBox(20.0)
        val label_prio = Label("Priority: " + task.priority)
        label_prio.font = globalFont
        hbox_prio.children.addAll(label_prio)

        val hbox_diff = HBox(20.0)
        val label_diff = Label("Difficulty: " + task.difficulty)
        label_diff.font = globalFont
        hbox_diff.children.addAll(label_diff)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, hbox_due, hbox_prio, hbox_diff, btn)

        btn.setOnMouseClicked {
            taskInfoStage.close()
        }
        vbox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(vbox, 700.0, 400.0)
        taskInfoStage.scene = scene
        taskInfoStage.show()
    }

    fun showProfileScreen(user: User) {
        val boldFont = Font.font("Courier New", FontWeight.BOLD, 20.0)

        var user = restoreUserData(dataFileName)
        val profileStage = Stage()
        profileStage.setTitle("Profile Screen")

        var profileVBox = VBox(10.0)

        var bannerCopy = ImageView()
        val bannerPath = "../assets/banners/" + user.bannerRank + ".png"
        val banner = Image(File(bannerPath).toURI().toString())
        bannerCopy.image = banner
        bannerCopy.fitWidth = 200.0
        bannerCopy.fitHeight = 100.0

        val path = "../assets/" + user.profileImageName
        val image = Image(File(path).toURI().toString())
        val imageView = ImageView()
        imageView.image = image
        imageView.fitWidth = 120.0
        imageView.fitHeight = 120.0

        val userInfoLabel = Label("User Information")
        userInfoLabel.font = boldFont
        var statisticsHBox = HBox(10.0)
        val titles = listOf("Current coins", "Longest Streak", "Tasks Done Today", "Rank")
        val fields = listOf(user.wallet, user.longestStreak, user.tasksDoneToday, user.bannerRank)

        for (i in 0..titles.size - 1) {
            val title = Label(titles[i])
            title.font = Font.font("Courier New", FontWeight.BOLD, 15.0)
            val field = Label(fields[i].toString())
            val statVBox = VBox(5.0)
            statVBox.children.addAll(title, field)
            statisticsHBox.children.add(statVBox)
        }
        statisticsHBox.alignment = Pos.CENTER

        val unlockablesLabel = Label("Unlockables (" + user.purchasedItems.size + ")")
        unlockablesLabel.font = boldFont

        var unlockablesHBox = FlowPane(Orientation.HORIZONTAL)
        unlockablesHBox.hgap = 10.0
        unlockablesHBox.vgap = 10.0

        for (item in user.purchasedItems) {
                unlockablesHBox.children.add(createShopItemVBox(item, 100.0))

        }
        unlockablesHBox.alignment = Pos.CENTER

        profileVBox.children.addAll(bannerCopy, imageView, userInfoLabel, statisticsHBox, unlockablesLabel, unlockablesHBox)
        profileVBox.alignment = Pos.TOP_CENTER

        profileVBox.style = """
            -fx-background-color:""" + base2 + """;
        """
        val scene = Scene(profileVBox, 600.0, 600.0)

        // set flowpane to same width as window
        unlockablesHBox.prefWidthProperty().bind(scene.widthProperty())

        profileStage.scene = scene
        profileStage.show()
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
        user.taskCompleteCounter() // count new task completed
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
        var coinValue = task.rewardCoins
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
                saveUserData(user, dataFileName)
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
