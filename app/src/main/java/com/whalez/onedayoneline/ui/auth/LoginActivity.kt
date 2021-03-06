package com.whalez.onedayoneline.ui.auth

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.whalez.onedayoneline.sharedpreference.UserSessionManager
import com.google.firebase.auth.FirebaseAuth
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register

class LoginActivity : AppCompatActivity() {

    private val TAG = "kkk.LoginActivity"

    private val EMAIL_FORMAT_ERROR = "The email address is badly formatted."
    private val NETWORK_ERROR = "An internal error has occurred. [ 7: ]"
    private val NO_USER_ERROR = "There is no user record corresponding to this identifier. The user may have been deleted."
    private val PASSWORD_ERROR = "The password is invalid or the user does not have a password."

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        // 로그인 버튼 클릭
        btn_login.setOnClickListener {
            showLoadingView()
            val userId = txt_id.text.toString()
            val userPassword = txt_password.text.toString()
            val userSessionManager = UserSessionManager(applicationContext)
            val builder = AlertDialog.Builder(
                ContextThemeWrapper(
                    this@LoginActivity,
                    R.style.MyAlertDialogStyle
                )
            )
            when {
                userId == "" -> {
                    builder.setMessage("아이디를 입력해주세요.")
                        .setPositiveButton("확인") { _, _ -> closeLoadingView() }
                        .show()
                }
                userPassword == "" -> {
                    builder.setMessage("비밀번호를 입력해주세요.")
                        .setPositiveButton("확인") { _, _ -> closeLoadingView() }
                        .show()
                }
                else -> {
                    auth.signInWithEmailAndPassword(userId, userPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (!user!!.isEmailVerified) {
                                    builder.setMessage("아직 본인 확인이 되지 않았습니다. 이메일을 확인해주세요.")
                                        .setPositiveButton("확인") { _, _ ->
                                            txt_password.text.clear()
                                        }
                                        .show()
                                } else {
                                    userSessionManager.createSession(userId)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "로그인되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Log.d(TAG, task.toString())
                            }
                        }.addOnFailureListener {
                            Log.d(TAG, it.message.toString())
                            val errorMessage = when (it.message) {
                                EMAIL_FORMAT_ERROR ->
                                    "이메일 형식이 올바르지 않습니다."
                                NETWORK_ERROR ->
                                    "네트워크 연결상태가 올바르지 않습니다."
                                PASSWORD_ERROR, NO_USER_ERROR ->
                                    "이메일 또는 비밀번호가 일치하지 않습니다."
                                else ->
                                    "알 수 없는 오류가 발생했습니다! 관리자에게 문의주세요."
                            }
                            builder.setMessage(errorMessage)
                                .setPositiveButton("확인") { _, _ ->
                                    txt_id.text.clear()
                                    txt_password.text.clear()
                                }
                                .show()
                            closeLoadingView()
                        }
                }
            }
        }

        // 회원가입 버튼 클릭
        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 외부 폰트를 적용했기 때문에 비밀번호 입력 시 폰트 변환이 필요함.
        txt_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (txt_password.text.isNotEmpty())
                    txt_password.typeface = Typeface.DEFAULT
                else txt_password.typeface =
                    ResourcesCompat.getFont(applicationContext,
                        R.font.font_cafe24_oneprettynight
                    )
            }
        })
    }

    private fun showLoadingView() {
        progressLayout.visibility = View.VISIBLE
        btn_register.isClickable = false
        txt_id.isEnabled = false
        txt_password.isEnabled = false
    }
    private fun closeLoadingView() {
        progressLayout.visibility = View.GONE
        btn_register.isClickable = true
        txt_id.isEnabled = true
        txt_password.isEnabled = true
    }
}
