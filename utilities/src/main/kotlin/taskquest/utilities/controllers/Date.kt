package taskquest.utilities.controllers

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat


class DateValidator (val dateFormat : String)  {

    fun validDate(datestr : String) : Boolean {
        return try {
            val df : DateFormat = SimpleDateFormat(dateFormat)
            df.isLenient = false
            df.parse(datestr)
            true
        } catch (e : ParseException) {
            false
        }
    }

}