package util
import android.content.Context
import androidx.preference.PreferenceManager
//import android.preference.PreferenceManager
import com.example.cronometro.MainActivity

class PrefUtil {
    companion object{

        fun getTimerLength(context: Context):Int{
            //
            return 1;
        }

        private const val PREVIOUS_TIIMER_LENGTH_SECONDS_ID="com.example.cronometro.previous_timer_length"

        fun getPreviousTimerLengthSeconds(context: Context):Long{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIIMER_LENGTH_SECONDS_ID,0)
        }

        fun setPreviousTimerLengthSeconds(seconds:Long,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIIMER_LENGTH_SECONDS_ID,seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID="com.example.cronometro.TimerState"

        fun getTimerState(context: Context):MainActivity.TimerState{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal=preferences.getInt(TIMER_STATE_ID,0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal=state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID="com.example.cronometro.previous_timer_length"

        fun getSecondsRemaining(context: Context):Long{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID,0)
        }

        fun setSecondsRemaining(seconds:Long,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID,seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.example.cronometro.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return  preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}