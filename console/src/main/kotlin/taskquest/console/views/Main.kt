package taskquest.console.views

import taskquest.console.controllers.CommandFactory
import taskquest.utilities.models.Task
import taskquest.utilities.models.restore
import taskquest.utilities.models.save

fun main(args: Array<String>) {
    // data stored in a list internally
    // but saved in a file on exit
    val list = mutableListOf<Task>()
    val filename = "data.json"

    // load previous to-do list
    list.restore(filename)

    println("Welcome to TaskQuest Console.")
    println("We support interactive mode where you can type and execute your commands one by one.")
    println("We also support argument mode where you can pass your commands via the --args flag.")


    if (args.isEmpty()) {

        var curInstr : List<String>?

        // process commands
        while (true) {
            curInstr = readLine()?.split(' ')
            if (curInstr == null || curInstr[0] == "quit") {
                break
            } else {
                val command = CommandFactory.createFromArgs(curInstr)
                command.execute(list)
            }
        }
    } else {
        val instructions : List<String> = args.toMutableList()
        val length = instructions.size
        var i = 0

        while (true) {
            i = CommandFactory.findFirstCommand(instructions, i, length)
            if (i == length) break
            val curInstr = instructions.slice(i until length)
            val command = CommandFactory.createFromArgs(curInstr)
            command.execute(list)
            i++
        }

    }

    // save to-do list (json)
    list.save(filename)
}