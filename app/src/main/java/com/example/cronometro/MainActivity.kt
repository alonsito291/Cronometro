package com.example.cronometro

import android.content.IntentSender
import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import util.PrefUtil
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    companion object{
        /////@RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setAlarm(context: Context, nowSeconds:Long, secondsRemaining:Long):Long{
            val wakeUpTime=(nowSeconds+secondsRemaining)*1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }
        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
            fun Context.toast(message:CharSequence)= Toast.makeText(this, "SE TERMINO", Toast.LENGTH_SHORT).show()
        }
        val nowSeconds: Long

            get() = Calendar.getInstance().timeInMillis / 1000
    }
    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer:CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState=TimerState.Stopped

    private var secondsRemaining= 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setIcon(R.drawable.ic_baseline_timer_24)
        supportActionBar?.title="       Timer"

        fab_star.setOnClickListener { view ->
            startTimer()
            timerState=TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener { view ->
            timer.cancel()
            timerState=TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener { view ->
            timer.cancel()
            timerState=TimerState.Stopped
            onTimerFinished()
        }
    }

    override fun onResume(){
        super.onResume()

        initTimer()
        removeAlarm(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause(){
        super.onPause()

        if(timerState==TimerState.Running){
            timer.cancel()
            val wakeUpTime= setAlarm(this, nowSeconds,secondsRemaining)
        }
        else if(timerState==TimerState.Paused){
            
        }
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining,this)
        PrefUtil.setTimerState(timerState,this)
    }

    private fun initTimer(){
        timerState=PrefUtil.getTimerState(this)

        if(timerState==TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining=if(timerState==TimerState.Running || timerState==TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime=PrefUtil.getAlarmSetTime(this)
        if(alarmSetTime>0)
            secondsRemaining-= nowSeconds-alarmSetTime
        if(secondsRemaining<=0)
            onTimerFinished()
        else if(timerState==TimerState.Running)
            startTimer()

        updateButtons()
        updateCountDownUI()
    }

    private fun onTimerFinished(){
        timerState=TimerState.Stopped

        setNewTimerLength()

        progress_countdown.progress=0

        PrefUtil.setSecondsRemaining(timerLengthSeconds,this)
        updateButtons()
        updateCountDownUI()
    }

    private fun startTimer(){
        timerState=TimerState.Running

        timer=object: CountDownTimer(secondsRemaining*1000,1000){
            override fun onFinish()=onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining=millisUntilFinished/1000
                updateCountDownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthMinutes=PrefUtil.getTimerLength(this)
        timerLengthSeconds=(lengthMinutes*60L)
        progress_countdown.max=timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds=PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max=timerLengthSeconds.toInt()
    }

    private fun updateCountDownUI(){
        val minutesUntilFinished=secondsRemaining/60
        val secondsInMinuteUntilFinished=secondsRemaining-minutesUntilFinished*60
        val secondsStr=secondsInMinuteUntilFinished.toString()
        textView_countdown.text="$minutesUntilFinished:${
        if(secondsStr.length==2)secondsStr
        else "0" + secondsStr}"
        progress_countdown.progress=(timerLengthSeconds-secondsRemaining).toInt()

    }

    private fun updateButtons(){
        when (timerState){
            TimerState.Running->{
                fab_star.isEnabled=false
                fab_pause.isEnabled=true
                fab_stop.isEnabled=true
            }

            TimerState.Stopped->{
                fab_star.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=false
            }

            TimerState.Paused->{
                fab_star.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}