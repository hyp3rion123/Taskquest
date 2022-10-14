package taskquest.console

import org.apache.commons.text.WordUtils
import taskquest.utilities.StringUtils

fun main() {
    val tokens = StringUtils.split(MessageUtils.getMessage())
    val result = StringUtils.join(tokens)
    println(WordUtils.capitalize(result))
}
