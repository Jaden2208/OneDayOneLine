package com.whalez.onedayoneline.utils

import android.content.Context
import android.content.Intent
import com.whalez.onedayoneline.ui.auth.LoginActivity
import com.whalez.onedayoneline.ui.home.HomeActivity

fun Context.startHomeActivity() =
    Intent(this, HomeActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.checkPasswordCoincidence(p1: String, p2: String) = p1 == p2