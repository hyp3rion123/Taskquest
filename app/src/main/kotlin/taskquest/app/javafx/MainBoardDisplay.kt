package taskquest.app.javafx;

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import taskquest.app.cssLayout
import taskquest.app.debugMode
import taskquest.utilities.models.Task
import taskquest.utilities.models.TaskList

public class MainBoardDisplay {
    fun start_display(stage: Stage?) {
        // set title for the stage
        stage?.title = "TaskQuest";

        //Task lists - Left column
        var taskLists = listOf<TaskList>()
        for (id in 1..5) {
            var taskList = TaskList(id, "Task List $id")
            taskLists += (taskList)
        }

        val taskListVBox = createTaskListVBox(taskLists)

        //Main tasks board
        var tasks = listOf<Task>()
        for (id in 1..10) {
            var task = Task(id, "Task $id", complete = (id % 2 == 0))
            tasks += (task)
        }

        var taskList1 = TaskList(1, "TaskList1", "This is a test task list")
        for (id in 1..10) {
            var task = Task(id=id,title="Task $id",desc="some desc", complete = (id % 2 == 0))
            taskList1.addItem(task)
        }
        var taskList2 = TaskList(2, "TaskList2", "This is a test task list")
        for (id in 1..5) {
            var task = Task(id=id+10,title="Task $id",desc="some desc", complete = (id % 2 == 0))
            taskList2.addItem(task)
        }
        var taskList3 = TaskList(3, "TaskList3", "This is a test task list")
        for (id in 1..7) {
            var task = Task(id=id+20,title="Task $id",desc="some desc", complete = (id % 2 == 0))
            taskList3.addItem(task)
        }
        val btn_create_task_to_do = Button("Create task")
        val btn_create_task_in_progress = Button("Create task")
        val btn_create_task_done = Button("Create task")

        var toDoVBox = createTasksVBox(btn_create_task_to_do, taskList1, "To Do")
        var inProgressVBox = createTasksVBox(btn_create_task_in_progress, taskList2, "In Progress")
        var doneVBox = createTasksVBox(btn_create_task_done, taskList3, "Done")
        var headerLabel = Label("Welcome back, Andrei. \n\n\nBoard View")

        var boardViewHBox = HBox(20.0, toDoVBox, inProgressVBox, doneVBox)
        var rightSideVBox = VBox(20.0, headerLabel, boardViewHBox)

        var sideBarVBox = createSideBarVBox()

        //Create task popup scene

        var hbox = HBox(10.0, sideBarVBox, taskListVBox, rightSideVBox)
        hbox.setAlignment(Pos.CENTER); //Center HBox
        var mainScene = Scene(hbox, 1200.0, 800.0)
        val stage2 = createTaskStage(taskList1, toDoVBox)

        btn_create_task_to_do.setOnMouseClicked {
            stage2.show()
        }

        stage?.setResizable(false)
        stage?.setScene(mainScene)
        stage?.show()

        if (debugMode) {
            toDoVBox.style = cssLayout
            inProgressVBox.style = cssLayout
            doneVBox.style = cssLayout
            headerLabel.style = cssLayout
            boardViewHBox.style = cssLayout
            sideBarVBox.style = cssLayout
            taskListVBox.style = cssLayout
            rightSideVBox.style = cssLayout
        }
    }

    fun createTaskListVBox(data : List<TaskList>): VBox {

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
        }

        return taskListVBox
    }

    fun createTasksVBox(create_button: Button, data : TaskList, title: String = "To do"): VBox {

        // create a VBox
        val tasksVBox = VBox(10.0)
        tasksVBox.children.add(create_button)
        tasksVBox.children.add(Label("$title (${data.getLength()})"))

        val searchBar = Label("Tasks Search bar")
        tasksVBox.children.add(searchBar)

        val textField = TextField()
        textField.promptText = "Search here!"
        tasksVBox.children.add(textField)

        // add buttons to VBox
        for (task in data.tasks) {
            val title = Label(task.title)
            val c = CheckBox()
            c.setSelected(task.complete)
            var btn_del = Button("delete")
            var btn_info = Button("See info")
            val hbox = HBox(5.0, c, title, btn_del, btn_info)
            btn_del.setOnMouseClicked {
                data.deleteItem(task.id)
                tasksVBox.children.remove(hbox)
            }
            btn_info.setOnMouseClicked {
                showTaskInfoStage(task)
            }
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
        val icons = listOf("Profile")
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
                data.deleteItem(task.id)
                vBox.children.remove(hbox)
            }
            btn_info.setOnMouseClicked {
                showTaskInfoStage(task)
            }
            vBox.children.add(hbox)
            create_task_stage.close()
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
}
