package com.whalez.onedayoneline

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setButtonDisable()
        password_check_message.visibility = View.INVISIBLE

        // 취소 버튼 클릭
        btn_cancel.setOnClickListener { finish() }

        // 외부 폰트를 적용했기 때문에 비밀번호 입력 시 폰트 변환이 필요함.
        txt_password1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswords()
                if (txt_password1.text.isNotEmpty())
                    txt_password1.typeface = Typeface.DEFAULT
                else txt_password1.typeface =
                    ResourcesCompat.getFont(applicationContext, R.font.font_cafe24_oneprettynight)
            }
        })

        // 외부 폰트를 적용했기 때문에 비밀번호 입력 시 폰트 변환이 필요함.
        txt_password2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswords()
                if (txt_password2.text.isNotEmpty())
                    txt_password2.typeface = Typeface.DEFAULT
                else txt_password2.typeface =
                    ResourcesCompat.getFont(applicationContext, R.font.font_cafe24_oneprettynight)
            }
        })
    }

    private fun checkPasswords() {
        val password1 = txt_password1.text.toString()
        val password2 = txt_password2.text.toString()
        if (password1 == "" || password2 == "") {
            password_check_message.visibility = View.INVISIBLE
            setButtonDisable()
        } else if (password1 == password2) {
            password_check_message.visibility = View.VISIBLE
            password_check_message.text = "비밀번호가 일치합니다."
            password_check_message.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimaryDark
                )
            )
            setButtonEnable()
        } else {
            password_check_message.visibility = View.VISIBLE
            password_check_message.text = "비밀번호가 일치하지 않습니다."
            password_check_message.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorAccent
                )
            )
            setButtonDisable()
        }
    }

    private fun setButtonEnable() {
        btn_register.isEnabled = true
        btn_register.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorPrimary
            )
        )
    }

    private fun setButtonDisable() {
        btn_register.isEnabled = false
        btn_register.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorDisableButton
            )
        )
    }
}
