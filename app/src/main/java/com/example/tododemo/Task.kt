package com.example.tododemo

data class Task(
    var taskId: Int = 0,
    var title: String? = null,
    var priority: String? = null,
    var time: String? = null,
    var scheduleDate: String? = null,
    var scheduleTime: String? = null,
    var userId: Int = 0,
    var fName :String? =null,
    var lName : String? =null

)