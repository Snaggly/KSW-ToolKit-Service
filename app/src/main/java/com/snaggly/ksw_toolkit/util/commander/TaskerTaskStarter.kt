package com.snaggly.ksw_toolkit.util.commander

import android.content.Context
import android.content.Intent
import net.dinglisch.android.taskerm.TaskerIntent
import net.dinglisch.android.taskerm.TaskerIntent.Status

object TaskerTaskStarter {
    fun launchTaskerTaskById(taskId: String?, context: Context) {
        val status: Status = TaskerIntent.testStatus(context)

        when (status) {
            Status.OK -> {
                val i = TaskerIntent(taskId)
                context.registerReceiver(TaskerBroadcastReceiver(), i.completionFilter)
                context.sendBroadcast(i)
            }
        }
    }
}

class TaskerBroadcastReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.unregisterReceiver(this)
    }
}