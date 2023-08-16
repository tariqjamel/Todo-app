package com.example.tododemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: List<String>,
    private val priorityList: List<String>,
    private val tv_time: List<String>,
    private val tv_schedule: List<String>,
    private val tv_clock: List<String>,
    private val onItemClick: (position: Int) -> Unit,
    private val onItemLongClick: (Position: Int) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val tvTaskName: TextView = itemView.findViewById(R.id.task_name)
        val tvTaskPriority: TextView = itemView.findViewById(R.id.task_priority)
        val taskTime: TextView = itemView.findViewById(R.id.tv_Time)
        val taskScheduleDate: TextView = itemView.findViewById(R.id.tv_Schedule)
        val taskScheduleTime: TextView = itemView.findViewById(R.id.tv_clock)

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            onItemLongClick(adapterPosition)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.add_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskName = taskList[position]
        val taskPriority = priorityList[position]
        val time = tv_time[position]
        val schedule = tv_schedule[position]
        val clock = tv_clock[position]

        holder.tvTaskName.text = taskName
        holder.tvTaskPriority.text = taskPriority
        holder.taskTime.text = time
        holder.taskScheduleDate.text = schedule
        holder.taskScheduleTime.text = clock
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}
