package taskquest.app.javafx;

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Stage
import taskquest.utilities.controllers.SaveUtils.Companion.restoreUserData
import taskquest.utilities.controllers.SaveUtils.Companion.saveUserData
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Cursor
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import taskquest.utilities.models.*
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.io.File


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

val boldFont = Font.font("Courier New", FontWeight.BOLD, 20.0)

val dataFileName = "../console/data.json"
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

public class MainBoardDisplay {
    var user = User();
    var toDoVBox = VBox();
    var boardViewHBox = HBox();
    fun dataChanged() {
        user.convertToString()
        saveUserData(user, dataFileName)
    }
    fun getTheme(): Triple<String, String, String> {
        return Triple(base1, base2, base3)
    }

    fun start_display(mainStage: Stage?) {

        user = restoreUserData(dataFileName)

        // set title for the stage
        mainStage?.title = "TaskQuest";

        //Banner
        val headerHBox = createBanner() //<--fix the sizing of banner

        //Main tasks board

        var taskList1 = TaskList(-1, "Test")
        if (user.lists.size >= 1) {
            taskList1 = user.lists[0]
        }

        val createTaskButton = Button("Create task")
        setDefaultButtonStyle(createTaskButton)

        toDoVBox = createTasksVBox(createTaskButton, taskList1, taskList1.title)

        boardViewHBox = HBox(20.0, toDoVBox)
        boardViewHBox.alignment = Pos.CENTER

        var (sideBarVBox, buttonList) = createSideBarVBox() //this order is required for theme switch - need to pass scene
        var themeButton = buttonList[0]
        var profileButton = buttonList[1]
        var shopButton = buttonList[2]

        val mainTasksSection = VBox(20.0, headerHBox, boardViewHBox)
        mainTasksSection.padding = Insets(100.0, 0.0, 0.0, 0.0)
        mainTasksSection.style = """
            -fx-background-color:""" + getTheme().third + """;
        """

        val mainScreenPane = BorderPane()
        mainScreenPane.right = createTaskListVBox(user.lists, createTaskButton)
        mainScreenPane.center = mainTasksSection
        mainScreenPane.left = sideBarVBox

        var mainScene = Scene(mainScreenPane, 900.0, 600.0)

        //Create task popup scene
        val createTaskMenu = createTaskStage(taskList1, toDoVBox)

        createTaskButton.setOnMouseClicked {
            createTaskMenu.show()
        }

        shopButton.setOnMouseClicked {
            mainStage?.scene = createShopScene(mainStage, mainScene) //created every time for refresh purposes
        }

//        fun updateTheme() {
//            mainTasksSection.style = """
//                -fx-background-color:""" + getTheme().third + """;
//            """
//            sideBarVBox.style = """
//                -fx-background-color:""" + getTheme().second + """;
//            """
//            setDefaultButtonStyle(themeButton)
//            setDefaultButtonStyle(shopButton)
//            setDefaultButtonStyle(profileButton)
//            mainScreenPane.right = createTaskListVBox(user.lists, createTaskButton)
////            toDoVBox.children.clear()
////            addVBoxNonTasks(createTaskButton)
////            toDoVBox = createTasksVBox(createTaskButton, taskList1, taskList1.title)
//
////            toDoVBox.children.clear()
////            addVBoxNonTasks(createTaskButton, taskList1, taskList1.title, toDoVBox)
////            for(currTask in taskList1.tasks){
////                val child = createTaskHbox(currTask, taskList1, toDoVBox, taskList1.title, createTaskButton)
////                child.alignment = Pos.TOP_LEFT
////                tasksVBox.children.add(child)
////            }
//        }

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
            mainStage?.close()
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

    fun createBanner(): HBox {
        val image = Image("https://3.bp.blogspot.com/-Y5k2sJfG5Ro/UoFMFpmbJmI/AAAAAAAAJHw/HVKNUY1Srog/s1600/image+5.png")

        var headerLabel = Label("Welcome back, USER_NAME.")
        headerLabel.alignment = Pos.CENTER
        headerLabel.font = globalFont

        var headerHBox = HBox(10.0, headerLabel)
        headerHBox.alignment = Pos.CENTER

        val backgroundSize = BackgroundSize(
            1064.0,
            176.0,
            true,
            true,
            true,
            false
        )
        val backgroundImage = BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
        )
        headerHBox.setBackground(Background(backgroundImage))
        return headerHBox
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

        // add buttons to VBox
        for (taskList in data) {
            val title = Button(taskList.title)
            setDefaultButtonStyle(title)
            taskListVBox.children.add(title)
            title.setOnMouseClicked {
                toDoVBox = createTasksVBox(btn_create_task_to_do, taskList, taskList.title)
                boardViewHBox.children.clear()
                boardViewHBox.children.add(toDoVBox)
            }
        }
        return taskListVBox
    }

    fun createTaskHbox(task: Task, data:TaskList, tasksVBox: VBox, title: String, create_button: Button): HBox {
        val title2 = Label(task.title)
        title2.font = globalFont
        val c = CheckBox()
        c.setSelected(task.complete)
        c.setOnMouseClicked {
            if (task.complete) {
                println("Mark incomplete: " + task.title)
                task.complete = false
            } else {
                println("Mark complete: " + task.title)
                task.complete = true
                showTaskCompletionStage(task)
            }
            dataChanged()
        }
        var btn_del = Button("delete")
        var btn_info = Button("See info")
        setDefaultButtonStyle(btn_del)
        setDefaultButtonStyle(btn_info)
        val hbox = HBox(5.0, c, title2, btn_del, btn_info)
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
                //Update backend-needed if user switches back and forth between lists
                for((index, list) in user.lists.withIndex()){
                    if(list.id == data.id){
                        user.lists.remove(list)
                        user.lists.add(index,data)
                        break
                    }
                }
                saveUserData(user, dataFileName)
                //Update frontend
                val newLists = restoreUserData(dataFileName).lists
                var newList = newLists[0]
                tasksVBox.children.clear()
                addVBoxNonTasks(create_button, data, title, tasksVBox)
                for(list in newLists) {
                    if (list.id == data.id) {
                        newList = list
                    }
                }
                for(currTask in newList.tasks){
                    val child = createTaskHbox(currTask, newList, tasksVBox, title, create_button)
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
        tasksVBox.children.add(create_button)
        val childLabel = Label("$title (${data.tasks.size})")
        childLabel.font = globalFont
        tasksVBox.children.add(childLabel)

        val searchBar = Label("Tasks Search bar")
        searchBar.font = globalFont
        tasksVBox.children.add(searchBar)

        val textField = TextField()
        textField.promptText = "Search here!"
        tasksVBox.children.add(textField)
    }

    fun createTasksVBox(create_button: Button, data : TaskList, title: String = "To do"): VBox {

        // create a VBox
        var tasksVBox = VBox(10.0)
        addVBoxNonTasks(create_button, data, title, tasksVBox)

//        var tasksContainer = VBox(10.0) //used for left aligning only the tasks
        // add tasks to VBox
        for (task in data.tasks) {
            val hbox = createTaskHbox(task, data, tasksVBox, title, create_button)
            tasksVBox.children.add(hbox)
//            hbox.alignment = Pos.TOP_LEFT
//            tasksContainer.children.add(hbox)
        }
//        tasksVBox.children.add(tasksContainer)

        //Map create button to current tasklist
        create_button.setOnMouseClicked {
            val create_task_stage = createTaskStage(data, tasksVBox)
            create_task_stage.show()
        }
        return tasksVBox
    }

    fun createSideBarVBox(): Pair<VBox, List<Button>>{
        //val icons = listOf("Profile")
        val sideBar = VBox(10.0)
        sideBar.style = """
            -fx-background-color:"""+ getTheme().second+ """;
        """
        val themeButton = Button("Switch theme")
        val profileButton = Button("Profile")
        val shopButton = Button("Shop")
        setDefaultButtonStyle(themeButton)
        setDefaultButtonStyle(profileButton)
        setDefaultButtonStyle(shopButton)
        profileButton.setOnMouseClicked {
            showProfileScreen(user);
        }
        sideBar.children.addAll(themeButton, profileButton, shopButton)
        return sideBar to listOf(themeButton, profileButton, shopButton)
    }

    fun createTaskStage(data: TaskList, vBox: VBox): Stage {
        val create_task_stage = Stage()
        create_task_stage.setTitle("Create Task")
        val btn = Button("Confirm")
        setDefaultButtonStyle(btn)

        val hbox_title = HBox(20.0)
        val label_title = Label("Title")
        label_title.font = globalFont
        val text_title= TextField()
        text_title.promptText = "Enter Title here"
        hbox_title.children.addAll(label_title, text_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description")
        label_desc.font = globalFont
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        hbox_desc.children.addAll(label_desc, text_desc)

        val hbox_due = HBox(20.0)
        val label_due = Label("Due Date")
        label_due.font = globalFont
        val text_due = TextField()
        text_due.promptText = "Enter Due Date here"
        hbox_due.children.addAll(label_due, text_due)

        val hbox_prio = HBox(20.0)
        val label_prio = Label("Priority")
        label_prio.font = globalFont
        val text_prio = TextField()
        text_prio.promptText = "Enter Priority here"
        hbox_prio.children.addAll(label_prio, text_prio)

        val hbox_diff = HBox(20.0)
        val label_diff = Label("Difficulty")
        label_diff.font = globalFont
        val text_diff = TextField()
        text_diff.promptText = "Enter difficulty here"
        hbox_diff.children.addAll(label_diff, text_diff)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, hbox_due, hbox_prio, hbox_diff, btn)

        btn.setOnMouseClicked {
            if(!validatePriority(text_prio.text)){
                val invalidPriorityStage = Stage()
                invalidPriorityStage.title = "Error"
                //label
                val errorMessage = Label("Invalid Priority Entered. Please enter one of:\n High | Medium | Low")
                errorMessage.font = globalFont
                errorMessage.isWrapText = true
                //button
                val exitPrioStageButton = Button("Exit")
                setDefaultButtonStyle(exitPrioStageButton)
                exitPrioStageButton.setOnMouseClicked {
                    invalidPriorityStage.hide()
                }
                //container
                val prioSceneContainer = BorderPane()
                prioSceneContainer.center = errorMessage
                prioSceneContainer.bottom = exitPrioStageButton
                prioSceneContainer.style = """
                    -fx-background-color:""" + getTheme().third + """;
                """
                //scene
                val invalidPriorityScene = Scene(prioSceneContainer,500.0, 300.0)

                invalidPriorityStage.scene = invalidPriorityScene
                invalidPriorityStage.show()
            } else if(!validateDifficulty(text_diff.text)) {
                val invalidDiffStage = Stage()
                invalidDiffStage.title = "Error"
                //label
                val errorMessage = Label("Invalid Difficulty Entered. Please enter one of:\n Hard | Medium | Easy")
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
            } else {
                val task = Task(id=1, title=text_title.text, desc=text_desc.text, dueDate=text_due.text,
                    priority = strToPrio(text_prio.text), difficulty = strToDiff(text_diff.text))
                data.addItem(task)
                val title = Label(task.title)
                title.font = globalFont
                val c = CheckBox()
                c.setSelected(task.complete)
                var btn_delete = Button("delete")
                val btn_info = Button("See info")
                val hbox = HBox(5.0, c, title, btn_delete, btn_info)
                setDefaultButtonStyle(btn_delete)
                setDefaultButtonStyle(btn_info)
                btn_delete.setOnMouseClicked {
                    data.deleteItemByID(task.id)
                    vBox.children.remove(hbox)
                    dataChanged()
                }
                btn_info.setOnMouseClicked {
                    showTaskInfoStage(task)
                }
                vBox.children.add(hbox)
                create_task_stage.close()
                dataChanged()
            }
        }
        vbox.style = """
            -fx-background-color:""" + getTheme().third + """;
        """
        val scene = Scene(vbox, 700.0, 400.0)
        create_task_stage.scene = scene
        return create_task_stage
    }

    fun validateDifficulty(d: String): Boolean {
        if(d == "Hard" || d == "Medium" || d == "Easy") return true
        return false
    }

    fun strToDiff(s: String): Difficulty {
        var diff = Difficulty.Easy
        when(s) {
            "Hard" -> diff = Difficulty.Hard
            "Medium" -> diff = Difficulty.Medium
            "Easy" -> return diff
        }
        return diff
    }

    fun validatePriority(p: String): Boolean {
        if(p == "High" || p == "Medium" || p == "Low") return true
        return false
    }

    fun strToPrio(s: String): Priority {
        var prio = Priority.Low
        when(s) {
            "High" -> prio = Priority.High
            "Medium" -> prio = Priority.Medium
            "Low" -> return prio
        }
        return prio
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

    fun showTaskCompletionStage(task: Task) {
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
        var coinValue = task.coinValue
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
        val scene = Scene(vbox, 500.0, 200.0)
        taskCompletionStage.scene = scene
        taskCompletionStage.show()
    }

    fun showProfileScreen(user: User) {
        var user = restoreData(dataFileName)
        val profileStage = Stage()
        profileStage.setTitle("Profile Screen")

        var profileVBox = VBox(10.0)

        val path = "../assets/" + user.profileImageName
        val image = Image(File(path).toURI().toString())
        val imageView = ImageView()
        imageView.image = image
        imageView.fitWidth = 120.0
        imageView.fitHeight = 120.0

        val userInfoLabel = Label("User Information")
        userInfoLabel.font = boldFont
        var statisticsHBox = HBox(10.0)
        val titles = listOf("Current coins", "Longest Streak", "Level")
        val fields = listOf(user.wallet, user.longestStreak, user.level)

        for (i in 0..titles.size - 1) {
            val title = Label(titles[i])
            val field = Label(fields[i].toString())
            val statVBox = VBox(5.0)
            statVBox.children.addAll(title, field)
            statisticsHBox.children.add(statVBox)
        }
        statisticsHBox.alignment = Pos.CENTER

        val unlockablesLabel = Label("Unlockables")
        unlockablesLabel.font = boldFont

        var unlockablesHBox = HBox(10.0)
        print(user.store.items.size)
        for (item in user.store.items) {
            if (item.purchased) {
                unlockablesHBox.children.add(createShopItemVBox(item))
            }
        }
        unlockablesHBox.alignment = Pos.CENTER

        profileVBox.children.addAll(imageView, userInfoLabel, statisticsHBox, unlockablesLabel, unlockablesHBox)
        profileVBox.alignment = Pos.CENTER

        profileVBox.style = """
            -fx-background-color:""" + base2 + """;
        """
        val scene = Scene(profileVBox, 500.0, 500.0)
        profileStage.scene = scene
        profileStage.show()
    }

    fun createShopScene(homeStage: Stage?, homeScene: Scene): Scene {
        var user = restoreUserData(dataFileName)
        var store = user.store
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
                user.store.buyItem(child.id)
                saveUserData(user, dataFileName)
                flowPane.children.remove(childBox)
                homeStage?.scene = createShopScene(homeStage, homeScene)
            }
            setDefaultButtonStyle(purchaseBtn)
            if(!child.purchased) {
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

    fun createShopItemVBox(item: Item): VBox {
        val vBox = VBox(10.0)
        //Image
        val path = "../assets/" + item.name + ".png"
        val image = Image(File(path).toURI().toString())
        val imageView = ImageView()
        imageView.image = image
        imageView.fitWidth = 120.0
        imageView.fitHeight = 120.0
        //Title
        val label = Label(item.name)
        label.font = globalFont
        val titleBox = HBox(10.0, label)

        vBox.children.addAll(imageView, titleBox)
        return vBox
    }

    fun createShopItem(item: Item): Pair<VBox, Button> {
        val vBox = createShopItemVBox(item)
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
