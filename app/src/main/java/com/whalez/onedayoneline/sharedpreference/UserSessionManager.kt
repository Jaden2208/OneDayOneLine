package com.whalez.onedayoneline.sharedpreference

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.whalez.onedayoneline.ui.auth.LoginActivity
import com.whalez.onedayoneline.ui.home.HomeActivity
import java.util.*

class UserSessionManager(private var context: Context) {

    private val TAG = "kkk.UserSessionManager"

    private var sharedPreferences: SharedPreferences
    private var editor: Editor
    private var PRIVATE_MODE = 0

    companion object {
        private const val LOGIN = "IS_LOGIN"
        const val ID = "ID"
    }

    init {
        sharedPreferences =
            context.getSharedPreferences(ID, PRIVATE_MODE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun createSession(id: String?) {
        editor.putBoolean(LOGIN, true)
        editor.putString(ID, id)
        editor.apply()
    }

    private val isLogin: Boolean
        get() = sharedPreferences.getBoolean(LOGIN, false)

    fun checkLogin() {
        if (!isLogin) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as HomeActivity).finish()
        }
    }

    val userDetail: HashMap<String, String?>
        get() {
            val user = HashMap<String, String?>()
            user[ID] = sharedPreferences.getString(ID, null)
            return user
        }

    val currentID: String?
        get() = sharedPreferences.getString(ID, null)

    fun changeValue(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun logout() {
        editor.clear()
        editor.commit()
        val i = Intent(context, LoginActivity::class.java)
        context.startActivity(i)
        (context as HomeActivity).finish()
    }


}