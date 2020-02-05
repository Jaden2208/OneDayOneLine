package com.whalez.onedayoneline.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.skydoves.powermenu.CircularEffect
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.createPowerMenu
import com.whalez.onedayoneline.R

class PowerMenuFactory: PowerMenu.Factory() {

    private val TAG = "kkk.PowerMenuFactory"

    override fun create(context: Context, lifecycle: LifecycleOwner): PowerMenu {
        return createPowerMenu(context) {
            addItem(PowerMenuItem("삭제하기", false))
            addItem(PowerMenuItem("개발자 정보", false))
            addItem(PowerMenuItem("문의하기", false))
            addItem(PowerMenuItem("Licenses", false))
            /* PowerMenu methods link
             * https://github.com/skydoves/PowerMenu#powermenu-methods
             */
            setAutoDismiss(true)
            setLifecycleOwner(lifecycle)
            setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
            setCircularEffect(CircularEffect.BODY)
            setMenuRadius(10f)
            setMenuShadow(10f)
            setTextColorResource(R.color.colorPrimaryDark)
            setTextSize(12)
            setTextGravity(Gravity.CENTER)
            setTextTypeface(Typeface.create("sans-serif-light", Typeface.BOLD))
            setSelectedTextColor(Color.WHITE)
            setMenuColor(Color.WHITE)
            setSelectedMenuColorResource(R.color.colorPrimary)
            setPreferenceName("HamburgerPowerMenu")
//            setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
        }
    }
}