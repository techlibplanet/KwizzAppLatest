package net.rmitsolutions.mfexpert.lms.helpers

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import kwizzapp.com.kwizzapp.R

/**
 * Created by Madhu on 14-Jun-2017.
 */

object SharedPrefKeys {
    const val PLAYER_ID = "PlayerId"
    const val DISPLAY_NAME = "DisplayName"
    const val FIRST_NAME = "FirstName"
    const val LAST_NAME = "LastName"
    const val EMAIL = "Email"
    const val MOBILE_NUMBER = "MobileNumber"
    const val FCM_TOKEN_ID = "FcmTokenId"
    const val PRODUCT_RECHARGE_POINTS = "Points recharged by yourself"
    const val ACCOUNT_NUMBER = "AccountNumber"
    const val IFSC_CODE= "IfscCode"
    const val HIGH_RES_URI = "high_res_uri"
    const val ICON_IMAGE_URI = "icon_image_uri"

    const val COMPLETE_A_GAME = "CompleteAGame"
    const val WIN_THREE_GAME = "WinThreeGame"
}

val Context.defaultSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

fun Context.clearPrefs() {
    applyPref(prefEditor().clear())
}

fun <T> Context.putPref(key: String, value: T) {
    val editor = prefEditor()
    applyPref(addToPrefEditor(editor, key, value))
}

fun <T> Context.addToPrefEditor(editor: SharedPreferences.Editor, key: String, value: T): SharedPreferences.Editor {
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        else -> throw Throwable("Type is not valid.")
    }
    return editor
}


fun Context.removePref(key: String) {
    applyPref(prefEditor().remove(key))
}

fun Context.removePref(vararg keys: String) {
    val editor = prefEditor()
    for (k in keys) {
        editor.remove(k)
    }
    applyPref(editor)
}

fun <T> Context.getPref(key: String, defaultValue: T): T {
    val prefs = this.defaultSharedPreferences
    val value: Any = when (defaultValue) {
        is String -> prefs.getString(key, defaultValue)
        is Boolean -> prefs.getBoolean(key, defaultValue)
        is Float -> prefs.getFloat(key, defaultValue)
        is Int -> prefs.getInt(key, defaultValue)
        is Long -> prefs.getLong(key, defaultValue)
        else -> throw Throwable("Type is not valid.")
    }
    return value as T
}

fun Context.prefEditor(): SharedPreferences.Editor {
    return defaultSharedPreferences.edit()
}

inline fun Context.addBulkPrefs(func: (editor: SharedPreferences.Editor) -> Unit) {
    val editor = prefEditor()
    func(editor)
    applyPref(editor)
}

fun Context.applyPref(editor: SharedPreferences.Editor) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
        editor.commit()
    } else {
        editor.apply()
    }
}
