package com.snaggly.ksw_toolkit.util.keyevent

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.util.ListType

class KeyEvent(name: String, icon: Drawable) : ListType(name, icon) {

    companion object {
        fun getKeyEventList(context: Context) : ArrayList<KeyEvent> {
            val result = ArrayList<KeyEvent>()
            result.add(KeyEvent(KeyCode.ASSIST.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_record_voice_over_36)!!))
            result.add(KeyEvent(KeyCode.BACK.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_backspace_36)!!))
            result.add(KeyEvent(KeyCode.CALCULATOR.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_calculate_36)!!))
            result.add(KeyEvent(KeyCode.CALENDAR.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_calendar_today_36)!!))
            result.add(KeyEvent(KeyCode.CONTACTS.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_contacts_36)!!))
            result.add(KeyEvent(KeyCode.DPAD_CENTER.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_center_focus_weak_36)!!))
            result.add(KeyEvent(KeyCode.DPAD_DOWN.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_36)!!))
            result.add(KeyEvent(KeyCode.DPAD_LEFT.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_left_36)!!))
            result.add(KeyEvent(KeyCode.DPAD_RIGHT.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_right_36)!!))
            result.add(KeyEvent(KeyCode.DPAD_UP.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_36)!!))
            result.add(KeyEvent(KeyCode.ENTER.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_filter_center_focus_24)!!))
            result.add(KeyEvent(KeyCode.ENVELOPE.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_email_36)!!))
            result.add(KeyEvent(KeyCode.EXPLORER.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_file_copy_36)!!))
            result.add(KeyEvent(KeyCode.HOME.name,  ContextCompat.getDrawable(context, R.drawable.ic_baseline_home_24)!!))
            result.add(KeyEvent(KeyCode.MEDIA_NEXT.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_skip_next_36)!!))
            result.add(KeyEvent(KeyCode.MEDIA_PLAY_PAUSE.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_36)!!))
            result.add(KeyEvent(KeyCode.MEDIA_PREVIOUS.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_skip_previous_36)!!))
            result.add(KeyEvent(KeyCode.MEDIA_STOP.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_stop_36)!!))
            result.add(KeyEvent(KeyCode.MUSIC.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_library_music_36)!!))
            result.add(KeyEvent(KeyCode.MUTE.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_mute_36)!!))
            result.add(KeyEvent(KeyCode.SETTINGS.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_settings_24)!!))
            result.add(KeyEvent(KeyCode.VOICE_ASSIST.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_record_voice_over_36)!!))
            result.add(KeyEvent(KeyCode.VOLUME_DOWN.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_down_36)!!))
            result.add(KeyEvent(KeyCode.VOLUME_MUTE.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_off_36)!!))
            result.add(KeyEvent(KeyCode.VOLUME_UP.name, ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_up_36)!!))

            return result
        }
    }
}