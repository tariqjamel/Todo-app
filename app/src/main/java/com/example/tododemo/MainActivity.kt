package com.example.tododemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), TaskAdapter.ItemClickListener {
    lateinit var fibAdd: View
    lateinit var recyclerView: RecyclerView
    var taskIds: ArrayList<Int> = ArrayList()
    var taskNames: ArrayList<String> = ArrayList()
    var taskPriorities: ArrayList<String> = ArrayList()
    var taskTime: ArrayList<String> = ArrayList()
    var taskScheduleDate: ArrayList<String> = ArrayList()
    var taskScheduleTime: ArrayList<String> = ArrayList()

    lateinit var sharedPreferences: SharedPreferences
    var userId = -1

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        sharedPreferences = getSharedPreferences("tododemo", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)

        recyclerView = findViewById(R.id.recycler_view)
        fibAdd = findViewById(R.id.add)
        taskIds = ArrayList()
        taskNames = ArrayList()
        taskPriorities = ArrayList()
        taskTime = ArrayList()
        taskScheduleDate = ArrayList()
        taskScheduleTime = ArrayList()

        val dbHandler = DatabaseHelper(this, null)
        val cursor = dbHandler.getTask(userId)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                taskIds.add(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_ID)))
                taskNames.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_TITLE)))
                taskPriorities.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_PRIORITY)))
                taskTime.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_TIME)))
                taskScheduleDate.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_SCHEDULE_DATE)))
                taskScheduleTime.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_SCHEDULE_TIME)))
            } while (cursor.moveToNext())
            cursor.close()
        }

        var fullName = findViewById<TextView>(R.id.fullName)
        val cursr = dbHandler.getName(userId)
        if (cursr != null && cursr.moveToFirst()) {
            val fName = cursr.getString(cursr.getColumnIndex(DatabaseHelper.FIRST_NAME))
            val lName = cursr.getString(cursr.getColumnIndex(DatabaseHelper.LAST_NAME))
            fullName.text = fName + " " + lName
            cursr.close()
        }

        fibAdd.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
            finish()
        }

        val adapter = TaskAdapter(taskNames, taskPriorities, taskTime, taskScheduleDate, taskScheduleTime,
            onItemClick = { position ->
                onItemClick(position)
            },
            onItemLongClick = { position ->
                onItemLongClick(position)
            })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        registerForContextMenu(recyclerView)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("id", taskIds[position])
        intent.putExtra("task", taskNames[position])
        intent.putExtra("priority", taskPriorities[position])
        intent.putExtra("time", taskTime[position])
        intent.putExtra("scheduleDate", taskScheduleDate[position])
        intent.putExtra("scheduleTime", taskScheduleTime[position])
        startActivity(intent)
        finish()
    }

    override fun onItemLongClick(position: Int) {
        val taskId = taskIds[position]

        val showMenu = androidx.appcompat.widget.PopupMenu(this, recyclerView.getChildAt(position))
        showMenu.inflate(R.menu.contxt_menu)
        showMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemDelete -> {
                    val dbHandler = DatabaseHelper(this, null)
                    dbHandler.deleteTask(taskId)
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()

                    taskIds.removeAt(position)
                    taskNames.removeAt(position)
                    taskPriorities.removeAt(position)
                    taskTime.removeAt(position)
                    taskScheduleDate.removeAt(position)
                    taskScheduleTime.removeAt(position)

                    recyclerView.adapter?.notifyItemRemoved(position)
                    true
                }
                R.id.itemInfo -> {
                    val taskTime = taskTime[position]
                    Toast.makeText(this, taskTime, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        showMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item3 -> Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            R.id.item4 -> Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            R.id.itemLogOut -> {
                sharedPreferences.edit().remove("userId").apply()

                val intent = Intent(this, loginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}