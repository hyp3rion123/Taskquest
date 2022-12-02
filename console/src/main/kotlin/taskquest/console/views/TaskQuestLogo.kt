package taskquest.console.views

object TaskQuestLogo {
    object FirstConfig {
        private const val line1 = " /\$\$\$\$\$\$\$\$                  /\$\$        /\$\$\$\$\$\$                                  /\$\$    "
        private const val line2 = "|__  \$\$__/                 | \$\$       /\$\$__  \$\$                                | \$\$    "
        private const val line3 = "   | \$\$  /\$\$\$\$\$\$   /\$\$\$\$\$\$\$| \$\$   /\$\$| \$\$  \\ \$\$ /\$\$   /\$\$  /\$\$\$\$\$\$   /\$\$\$\$\$\$\$ /\$\$\$\$\$\$  "
        private const val line4 = "   | \$\$ |____  \$\$ /\$\$_____/| \$\$  /\$\$/| \$\$  | \$\$| \$\$  | \$\$ /\$\$__  \$\$ /\$\$_____/|_  \$\$_/  "
        private const val line5 = "   | \$\$  /\$\$\$\$\$\$\$|  \$\$\$\$\$\$ | \$\$\$\$\$\$/ | \$\$  | \$\$| \$\$  | \$\$| \$\$\$\$\$\$\$\$|  \$\$\$\$\$\$   | \$\$    "
        private const val line6 = "   | \$\$ /\$\$__  \$\$ \\____  \$\$| \$\$_  \$\$ | \$\$/\$\$ \$\$| \$\$  | \$\$| \$\$_____/ \\____  \$\$  | \$\$ /\$\$"
        private const val line7 = "   | \$\$|  \$\$\$\$\$\$\$ /\$\$\$\$\$\$\$/| \$\$ \\  \$\$|  \$\$\$\$\$\$/|  \$\$\$\$\$\$/|  \$\$\$\$\$\$\$ /\$\$\$\$\$\$\$/  |  \$\$\$\$/"
        private const val line8 = "   |__/ \\_______/|_______/ |__/  \\__/ \\____ \$\$\$ \\______/  \\_______/|_______/    \\___/  "
        private const val line9 = "                                           \\__/                                        "
        private val logoList = listOf(line1, line2, line3, line4, line5, line6, line7, line8, line9)

        fun printLogo() {
            for (line in logoList) {
                println(line)
            }
        }
    }

    object SecondConfig {
        private const val line1 = " ________                    __         ______                                   __     "
        private const val line2 = "|        \\                  |  \\       /      \\                                 |  \\    "
        private const val line3 = " \\\$\$\$\$\$\$\$\$______    _______ | \$\$   __ |  \$\$\$\$\$\$\\ __    __   ______    _______  _| \$\$_   "
        private const val line4 = "   | \$\$  |      \\  /       \\| \$\$  /  \\| \$\$  | \$\$|  \\  |  \\ /      \\  /       \\|   \$\$ \\  "
        private const val line5 = "   | \$\$   \\\$\$\$\$\$\$\\|  \$\$\$\$\$\$\$| \$\$_/  \$\$| \$\$  | \$\$| \$\$  | \$\$|  \$\$\$\$\$\$\\|  \$\$\$\$\$\$\$ \\\$\$\$\$\$\$  "
        private const val line6 = "   | \$\$  /      \$\$ \\\$\$    \\ | \$\$   \$\$ | \$\$ _| \$\$| \$\$  | \$\$| \$\$    \$\$ \\\$\$    \\   | \$\$ __ "
        private const val line7 = "   | \$\$ |  \$\$\$\$\$\$\$ _\\\$\$\$\$\$\$\\| \$\$\$\$\$\$\\ | \$\$/ \\ \$\$| \$\$__/ \$\$| \$\$\$\$\$\$\$\$ _\\\$\$\$\$\$\$\\  | \$\$|  \\"
        private const val line8 = "   | \$\$  \\\$\$    \$\$|       \$\$| \$\$  \\\$\$\\ \\\$\$ \$\$ \$\$ \\\$\$    \$\$ \\\$\$     \\|       \$\$   \\\$\$  \$\$"
        private const val line9 = "    \\\$\$   \\\$\$\$\$\$\$\$ \\\$\$\$\$\$\$\$  \\\$\$   \\\$\$  \\\$\$\$\$\$\$\\  \\\$\$\$\$\$\$   \\\$\$\$\$\$\$\$ \\\$\$\$\$\$\$\$     \\\$\$\$\$ "
        private const val line10 = "                                            \\\$\$\$                                        "
        private val logoList = listOf(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10)

        fun printLogo() {
            for (line in logoList) {
                println(line)
            }
        }
    }
}