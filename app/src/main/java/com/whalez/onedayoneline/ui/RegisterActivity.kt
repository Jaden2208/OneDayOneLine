package com.whalez.onedayoneline.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val TAG = "WHALEZ_RegisterActivity"

    private val EMAIL_FORMAT_ERROR = "The email address is badly formatted."
    private val USED_EMAIL_ERROR = "The email address is already in use by another account."
//    private val PASSWORD_LENGTH_ERROR = "The given password is invalid. [ Password should be at least 6 characters ]"

    private lateinit var auth: FirebaseAuth
    private lateinit var password1: String
    private lateinit var password2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setButtonDisable()
        password_check_message.visibility = View.INVISIBLE

        // 취소 버튼 클릭
        btn_cancel.setOnClickListener { finish() }

        auth = FirebaseAuth.getInstance()
        // 회원가입 버튼 클릭
        btn_register.setOnClickListener {
            val id = txt_id.text.toString()
            auth.createUserWithEmailAndPassword(id, password1)
                .addOnCompleteListener(this) {
                    // 가입 성공
                    if (it.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        if (user != null && !user.isEmailVerified) { // 사용자가 존재하지 않고 이메일 인증이 안된 경우
                            user.sendEmailVerification()
                                .addOnCompleteListener {
                                    val builder = AlertDialog.Builder(
                                        ContextThemeWrapper(
                                            this@RegisterActivity,
                                            R.style.MyAlertDialogStyle
                                        )
                                    )
                                    builder.setMessage("본인 확인을 위한 이메일을\n[" + user.email + "] (으)로 보냈습니다.")
                                        .setPositiveButton("확인") { _, _ ->
                                            gotoLoginPage()
                                        }
                                        .show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@RegisterActivity,
                            R.style.MyAlertDialogStyle
                        )
                    )
                    when (it.message) {
                        EMAIL_FORMAT_ERROR -> {
                            builder.setMessage("이메일 형식이 잘못되었습니다.")
                                .setPositiveButton("확인") { _, _ ->
                                }
                                .show()
                        }
                        USED_EMAIL_ERROR -> {
                            builder.setMessage("이미 존재하는 이메일입니다.")
                                .setPositiveButton("확인") { _, _ ->
                                    txt_id.text.clear()
                                }
                                .show()
                        }
                        else -> {
                            builder.setMessage("알 수 없는 오류가 발견되었습니다!\n관리자에게 문의주세요.")
                                .setPositiveButton("확인") { _, _ ->
                                }
                                .show()
                        }
                    }
                    txt_password1.text.clear()
                    txt_password2.text.clear()
                }

        }

        btn_cancel.setOnClickListener { finish() }

        txt_id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (txt_id.text.isEmpty()) setButtonDisable()
                else setButtonEnable()
            }

        })

        // 외부 폰트를 적용했기 때문에 비밀번호 입력 시 폰트 변환이 필요함.
        txt_password1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswords()
                if (txt_password1.text.isNotEmpty())
                    txt_password1.typeface = Typeface.DEFAULT
                else txt_password1.typeface =
                    ResourcesCompat.getFont(
                        applicationContext,
                        R.font.font_cafe24_oneprettynight
                    )
            }
        })

        // 외부 폰트를 적용했기 때문에 비밀번호 입력 시 폰트 변환이 필요함.
        txt_password2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswords()
                if (txt_password2.text.isNotEmpty())
                    txt_password2.typeface = Typeface.DEFAULT
                else txt_password2.typeface =
                    ResourcesCompat.getFont(
                        applicationContext,
                        R.font.font_cafe24_oneprettynight
                    )
            }
        })
    }

    private fun checkPasswords() {
        password1 = txt_password1.text.toString()
        password2 = txt_password2.text.toString()
        if (password1 == "" || password2 == "") {
            password_check_message.visibility = View.INVISIBLE
            setButtonDisable()
        } else if (password1 == password2) {
            password_check_message.visibility = View.VISIBLE
            if (password1.length < 6) {
                password_check_message.text = "비밀번호는 6자 이상이어야 합니다."
                password_check_message.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
                setButtonDisable()
            } else {
                password_check_message.text = "비밀번호가 일치합니다."
                password_check_message.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimaryDark
                    )
                )
                if(txt_id.text.isNotEmpty()) setButtonEnable()
            }
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

    private fun gotoLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
