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
import taskquest.utilities.controllers.SaveUtils.Companion.restoreData
import taskquest.utilities.controllers.SaveUtils.Companion.saveData
import taskquest.utilities.models.Task
import taskquest.utilities.models.TaskList
import taskquest.utilities.models.User
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent


// for outlining layout borders
const val debugMode = false
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

const val dataFileName = "../console/data.json"
public class MainBoardDisplay {
    var user = User();
    var toDoVBox = VBox();
    var boardViewHBox = HBox();
    fun dataChanged() {
        user.to_string()
        saveData(user, dataFileName)
    }

    fun start_display(stage: Stage?) {

        user = restoreData(dataFileName)

        // set title for the stage
        stage?.title = "TaskQuest";

        //Task lists - Left column
        var taskLists = user.lists


        //Banner
        val image = Image("https://3.bp.blogspot.com/-Y5k2sJfG5Ro/UoFMFpmbJmI/AAAAAAAAJHw/HVKNUY1Srog/s1600/image+5.png")

        var headerLabel = Label("Welcome back, Andrei.\nBoard View")

        var insideHeaderHBox = HBox(10.0, headerLabel)
        headerLabel.style = bannerTextCss

        var headerVBox = VBox(10.0, insideHeaderHBox)

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
            BackgroundPosition.DEFAULT,
            backgroundSize
        )
        headerVBox.setBackground(Background(backgroundImage))


        //Main tasks board

        var taskList1 = user.lists[0]

        var taskList2 = taskList1

        var taskList3 = taskList1

        val btn_create_task_to_do = Button("Create task")
        val btn_create_task_in_progress = Button("Create task")
        val btn_create_task_done = Button("Create task")

        toDoVBox = createTasksVBox(btn_create_task_to_do, taskList1, taskList1.title)
        var inProgressVBox = createTasksVBox(btn_create_task_in_progress, taskList2, "In Progress")
        var doneVBox = createTasksVBox(btn_create_task_done, taskList3, "Done")

        var taskListVBox = createTaskListVBox(taskLists, btn_create_task_to_do)


        boardViewHBox = HBox(20.0, toDoVBox)
        var rightSideVBox = VBox(20.0, headerVBox, boardViewHBox)

        var sideBarVBox = createSideBarVBox()

        //Create task popup scene

        var hbox = HBox(10.0, sideBarVBox, taskListVBox, rightSideVBox)
        hbox.setAlignment(Pos.CENTER); //Center HBox
        var mainScene = Scene(hbox, 900.0, 600.0)
        val stage2 = createTaskStage(taskList1, toDoVBox)

        btn_create_task_to_do.setOnMouseClicked {
            stage2.show()
        }

        stage?.setResizable(true)
        stage?.setScene(mainScene)
        stage?.show()

        if (debugMode) {
            toDoVBox.style = debugCss
            inProgressVBox.style = debugCss
            doneVBox.style = debugCss
            headerLabel.style = debugCss
            boardViewHBox.style = debugCss
            sideBarVBox.style = debugCss
            taskListVBox.style = debugCss
            rightSideVBox.style = debugCss

        }
    }

    fun createTaskListVBox(data: List<TaskList>, btn_create_task_to_do: Button): VBox {

        // create a VBox
        val taskListVBox = VBox(10.0)

        val searchBar = Label("Task List Search bar")
        taskListVBox.children.add(searchBar)

        val textField = TextField()
        textField.setPromptText("Search here!")
        taskListVBox.children.add(textField)

        // add buttons to VBox
        for (taskList in data) {
            val title = Button(taskList.title)
            taskListVBox.children.add(title)
            title.setOnMouseClicked {
                println("Selected taskList: " + taskList.title)
                toDoVBox = createTasksVBox(btn_create_task_to_do, taskList, taskList.title)
                boardViewHBox.children.clear()
                boardViewHBox.children.add(toDoVBox)
            }
        }

        return taskListVBox
    }

    fun createTaskHbox(task: Task, data:TaskList, tasksVBox: VBox, title: String, create_button: Button): HBox {
        val title2 = Label(task.title)
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
                dataChanged()

                //Update frontend
                val newLists = restoreData(dataFileName).lists
                var newList = newLists[0]
                tasksVBox.children.clear()
                addVBoxNonTasks(create_button, data, title, tasksVBox)
                for(list in newLists) {
                    if (list.id == data.id) {
                        newList = list
                    }
                }
                for(currTask in newList.tasks){
                    tasksVBox.children.add(createTaskHbox(currTask, newList, tasksVBox, title, create_button))
                }
            }
            /* let the source know whether the string was successfully transferred and used */
            event.isDropCompleted = success
            event.consume()
        }

        btn_del.setOnMouseClicked {
            data.deleteItemByID(task.id)
            tasksVBox.children.remove(hbox)
            user.to_string()
            dataChanged()
        }
        btn_info.setOnMouseClicked {
            showTaskInfoStage(task)
        }
        return hbox
    }

    fun addVBoxNonTasks(create_button: Button, data: TaskList, title: String, tasksVBox: VBox) {
        tasksVBox.children.add(create_button)
        tasksVBox.children.add(Label("$title (${data.getLength()})"))

        val searchBar = Label("Tasks Search bar")
        tasksVBox.children.add(searchBar)

        val textField = TextField()
        textField.promptText = "Search here!"
        tasksVBox.children.add(textField)
    }

    fun createTasksVBox(create_button: Button, data : TaskList, title: String = "To do"): VBox {

        // create a VBox
        var tasksVBox = VBox(10.0)
        addVBoxNonTasks(create_button, data, title, tasksVBox)

        // add tasks to VBox
        for (task in data.tasks) {
            val hbox = createTaskHbox(task, data, tasksVBox, title, create_button)
            tasksVBox.children.add(hbox)
        }

        //Map create button to current tasklist
        create_button.setOnMouseClicked {
            val create_task_stage = createTaskStage(data, tasksVBox)
            create_task_stage.show()
        }
        return tasksVBox
    }

    fun createSideBarVBox(): VBox {
        //val icons = listOf("Profile")
        val sideBar = VBox(10.0)
        val label1 = Button("Switch theme")
        val label2 = Button("Profile")
        val label3 = Button("Shop")
        sideBar.children.addAll(label1, label2, label3)
        return sideBar
    }

    fun createTaskStage(data: TaskList, vBox: VBox): Stage {
        val create_task_stage = Stage()
        create_task_stage.setTitle("Create Task")
        val btn = Button("Confirm")

        val hbox_title = HBox(20.0)
        val label_title = Label("Title")
        val text_title= TextField()
        text_title.promptText = "Enter Title here"
        hbox_title.children.addAll(label_title, text_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description")
        val text_desc = TextField()
        text_desc.promptText = "Enter Description here"
        hbox_desc.children.addAll(label_desc, text_desc)

        val hbox_due = HBox(20.0)
        val label_due = Label("Due Date")
        val text_due = TextField()
        text_due.promptText = "Enter Due Date here"
        hbox_due.children.addAll(label_due, text_due)

        val hbox_prio = HBox(20.0)
        val label_prio = Label("Priority")
        val text_prio = TextField()
        text_prio.promptText = "Enter Priority here"
        hbox_prio.children.addAll(label_prio, text_prio)

        val hbox_diff = HBox(20.0)
        val label_diff = Label("Difficulty")
        val text_diff = TextField()
        text_diff.promptText = "Enter difficulty here"
        hbox_diff.children.addAll(label_diff, text_diff)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, hbox_due, hbox_prio, hbox_diff, btn)

        btn.setOnMouseClicked {
            val task = Task(id=1, title=text_title.text, desc=text_desc.text, dueDate=text_due.text)
            data.addItem(task)
            val title = Label(task.title)
            val c = CheckBox()
            c.setSelected(task.complete)
            var btn_delete = Button("delete")
            val btn_info = Button("See info")
            val hbox = HBox(5.0, c, title, btn_delete, btn_info)
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

        val scene = Scene(vbox, 700.0, 400.0)
        create_task_stage.scene = scene
        return create_task_stage
    }

    fun showTaskInfoStage(task: Task) {
        val taskInfoStage = Stage()
        taskInfoStage.setTitle("Task Info")
        val btn = Button("Exit")

        val hbox_title = HBox(20.0)
        val label_title = Label("Title: " + task.title)
        hbox_title.children.addAll(label_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Description: " + task.desc)
        hbox_desc.children.addAll(label_desc)

        val hbox_due = HBox(20.0)
        val label_due = Label("Due Date: " + task.dueDate)
        hbox_due.children.addAll(label_due)

        val hbox_prio = HBox(20.0)
        val label_prio = Label("Priority: " + task.priority)
        hbox_prio.children.addAll(label_prio)

        val hbox_diff = HBox(20.0)
        val label_diff = Label("Difficulty: " + task.difficulty)
        hbox_diff.children.addAll(label_diff)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, hbox_due, hbox_prio, hbox_diff, btn)

        btn.setOnMouseClicked {
            taskInfoStage.close()
        }

        val scene = Scene(vbox, 700.0, 400.0)
        taskInfoStage.scene = scene
        taskInfoStage.show()
    }

    fun showTaskCompletionStage(task: Task) {
        val taskCompletionStage = Stage()
        taskCompletionStage.setTitle("Task Completed!")
        val btn = Button("Exit")

        val hbox_title = HBox(20.0)
        val label_title = Label("Congrats on getting " + task.title + " done!")
        hbox_title.alignment = Pos.CENTER
        hbox_title.children.addAll(label_title)

        val hbox_desc = HBox(20.0)
        val label_desc = Label("Here's 10 TaskCoins as a reward!")
        hbox_desc.alignment = Pos.CENTER
        hbox_desc.children.addAll(label_desc)

        val hbox_reward = HBox(20.0)
        val label_reward = Label("+10 ")
        label_reward.setFont(Font.font("Cambria", 32.0));
        hbox_reward.alignment = Pos.CENTER
        hbox_reward.children.addAll(label_desc)

        val vbox = VBox(10.0)
        vbox.children.addAll(hbox_title, hbox_desc, label_desc, btn)

        vbox.alignment = Pos.CENTER

        btn.setOnMouseClicked {
            taskCompletionStage.close()
        }

        val scene = Scene(vbox, 500.0, 200.0)
        taskCompletionStage.scene = scene
        taskCompletionStage.show()
    }
}
