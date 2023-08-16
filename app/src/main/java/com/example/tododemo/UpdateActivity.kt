package com.example.tododemo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.tododemo.databinding.ActivityUpdateBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding

    lateinit var btnUpdate: Button
    lateinit var tvUpdate_Title: EditText
    lateinit var tvUpdate_Priority: ChipGroup
    lateinit var btnDelete: Button
    lateinit var tvScheduleDate: TextView
    lateinit var tvScheduleTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnUpdate = findViewById(R.id.update_button)
        tvUpdate_Title = findViewById(R.id.create_title)
        tvUpdate_Priority = findViewById(R.id.create_priority)
        btnDelete = findViewById(R.id.delete_button)
        tvScheduleDate = findViewById(R.id.tvSchedule)
        tvScheduleTime = findViewById(R.id.tvClock)
        var tvCalendar = findViewById<CalendarView>(R.id.tvCalendar)
        var tvTime = findViewById<TextView>(R.id.tv_time)

        tvCalendar.visibility = View.GONE
        tvScheduleDate.setOnClickListener {
            tvCalendar.visibility = View.VISIBLE
        }
        tvScheduleTime.setOnClickListener {
            tvCalendar.visibility = View.VISIBLE
        }

        tvCalendar.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            val date = (dayOfMonth.toString() + "/" + (month + 1) + "/" + year)
            tvScheduleDate.setText("Schedule: " + date)
            tvCalendar.visibility = View.GONE

            val clock = Calendar.getInstance()
            val hour = clock.get(Calendar.HOUR_OF_DAY)
            val minute = clock.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    tvScheduleTime.setText("$hourOfDay:$minute")
                },
                hour, minute, false
            )
            timePickerDialog.show()
        })

        val task = intent.getStringExtra("task")
        val priority = intent.getStringExtra("priority")
        val taskId = intent.getIntExtra("id", -1)
        val time = intent.getStringExtra("time")
        var scheduleDate = intent.getStringExtra("scheduleDate")
        var sheduleTime = intent.getStringExtra("scheduleTime")


        tvUpdate_Title.setText(task)
        tvTime.setText(time)
        tvScheduleDate.setText(scheduleDate)
        tvScheduleTime.setText(sheduleTime)

        btnUpdate.setOnClickListener {
            val dbHandler = DatabaseHelper(this, null)

            val selectedChipId = tvUpdate_Priority.checkedChipId
            val selectedChip = findViewById<Chip>(selectedChipId)

            val priority = selectedChip?.text.toString() ?: ""


            val sharedPreferences = getSharedPreferences("tododemo", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("userId", -1)
            val user = Task(
                taskId, tvUpdate_Title.text.toString(), priority,
                tvTime.text.toString(), tvScheduleDate.text.toString(), tvScheduleTime.text.toString(), userId
            )
            dbHandler.updateTask(user)
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            tvUpdate_Title.text.clear()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnDelete.setOnClickListener {
            val dbHandler = DatabaseHelper(this, null)
            dbHandler.deleteTask(taskId)
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            tvUpdate_Title.text.clear()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        binding.notify.setOnClickListener { scheduleNotification() }
    }

    private fun scheduleNotification() {

        val selectedChipId = tvUpdate_Priority.checkedChipId
        val selectedChip = findViewById<Chip>(selectedChipId)

        val title = tvUpdate_Title.text.toString()
        val message = selectedChip?.text.toString() ?: ""

        val extras = PersistableBundle()
        extras.putString(NOTIFICATION_TITLE, title)
        extras.putString(NOTIFICATION_MESSAGE, message)

        val scheduledTimeMillis = calculateScheduledTimeMillis()
        val currentTimeMillis = System.currentTimeMillis()
        val delayMillis = scheduledTimeMillis - currentTimeMillis

        val jobScheduler = applicationContext.getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(JOB_ID, ComponentName(this, MyJobScheduler::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setMinimumLatency(delayMillis)
            .setExtras(extras)
            .build()

        jobScheduler.schedule(jobInfo)

        AlertDialog.Builder(this)
            .setTitle("ToDo")
            .setMessage("You will be notified on scheduled time.")
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun calculateScheduledTimeMillis(): Long {
        val minute = tvScheduleTime.text.toString().split(":")[1].toInt()
        val hour = tvScheduleTime.text.toString().split(":")[0].toInt()
        val selectedDateMillis = binding.tvCalendar.date

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "ToDo"
        val message = "Priority"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANEL_ID, name, importance)
        channel.description = message

        val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}