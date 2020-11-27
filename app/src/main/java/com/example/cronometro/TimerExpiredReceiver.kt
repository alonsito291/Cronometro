package com.example.cronometro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        TODO("TimerExpiredReceiver.onReceive() is not implemented")
        PrefUtil.setTimerState(MainActivity.TimerState.Stopped,context)

        PrefUtil.setAlarmSetTime(0,context)
        fun Context.toast(message:CharSequence)= Toast.makeText(this, "SE TERMINO", Toast.LENGTH_SHORT).show()
    }
}
