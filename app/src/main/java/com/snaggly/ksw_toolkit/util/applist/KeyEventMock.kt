package com.snaggly.ksw_toolkit.util.applist

import android.content.Context
import androidx.core.content.ContextCompat
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.util.enums.KeyCode

class KeyEventMock {
    companion object {
        fun getMockedAppList(context: Context) : ArrayList<AppInfo> {
            val result = ArrayList<AppInfo>()
            result.add(AppInfo(KeyCode.ASSIST.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_record_voice_over_36)!!))
            result.add(AppInfo(KeyCode.BACK.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_backspace_36)!!))
            result.add(AppInfo(KeyCode.CALCULATOR.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_calculate_36)!!))
            result.add(AppInfo(KeyCode.CALENDAR.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_calendar_today_36)!!))
            result.add(AppInfo(KeyCode.CONTACTS.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_contacts_36)!!))
            result.add(AppInfo(KeyCode.DPAD_CENTER.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_center_focus_weak_36)!!))
            result.add(AppInfo(KeyCode.DPAD_DOWN.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_36)!!))
            result.add(AppInfo(KeyCode.DPAD_LEFT.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_left_36)!!))
            result.add(AppInfo(KeyCode.DPAD_RIGHT.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_right_36)!!))
            result.add(AppInfo(KeyCode.DPAD_UP.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_36)!!))
            result.add(AppInfo(KeyCode.ENTER.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_filter_center_focus_24)!!))
            result.add(AppInfo(KeyCode.ENVELOPE.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_email_36)!!))
            result.add(AppInfo(KeyCode.EXPLORER.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_file_copy_36)!!))
            result.add(AppInfo(KeyCode.HOME.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_home_24)!!))
            result.add(AppInfo(KeyCode.MEDIA_NEXT.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_skip_next_36)!!))
            result.add(AppInfo(KeyCode.MEDIA_PLAY_PAUSE.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_36)!!))
            result.add(AppInfo(KeyCode.MEDIA_PREVIOUS.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_skip_previous_36)!!))
            result.add(AppInfo(KeyCode.MEDIA_STOP.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_stop_36)!!))
            result.add(AppInfo(KeyCode.MUSIC.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_library_music_36)!!))
            result.add(AppInfo(KeyCode.MUTE.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_mute_36)!!))
            result.add(AppInfo(KeyCode.SETTINGS.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_settings_24)!!))
            result.add(AppInfo(KeyCode.VOICE_ASSIST.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_record_voice_over_36)!!))
            result.add(AppInfo(KeyCode.VOLUME_DOWN.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_down_36)!!))
            result.add(AppInfo(KeyCode.VOLUME_MUTE.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_off_36)!!))
            result.add(AppInfo(KeyCode.VOLUME_UP.name, "", ContextCompat.getDrawable(context, R.drawable.ic_baseline_volume_up_36)!!))

            return result
        }
    }
}