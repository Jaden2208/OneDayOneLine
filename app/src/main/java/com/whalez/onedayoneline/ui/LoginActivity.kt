package com.whalez.onedayoneline.ui

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.example.indyproject2.UserSessionManager
import com.google.firebase.auth.FirebaseAuth
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register

class LoginActivity : AppCompatActivity() {
    private val TAG = "KKK"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        // 로그인 버튼 클릭
        btn_login.setOnClickListener {
            val userId = txt_id.text.toString()
            val userPassword = txt_password.text.toString()

            val userSessionManager = UserSessionManager(applicationContext)

            auth.signInWithEmailAndPassword(userId, userPassword)
                .addOnCompleteListener { task ->
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@LoginActivity,
                            R.style.Theme_AppCompat_Light_Dialog
                        )
                    )
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
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        builder.setMessage("존재하지 않는 아이디입니다.")
                            .setPositiveButton("확인") { _, _ ->
                                txt_id.text.clear()
                                txt_password.text.clear()
                            }
                            .show()
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
}
