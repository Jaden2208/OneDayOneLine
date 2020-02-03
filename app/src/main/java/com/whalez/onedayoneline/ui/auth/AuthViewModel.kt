package com.whalez.onedayoneline.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.whalez.onedayoneline.data.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(
    private val repository: UserRepository
): ViewModel() {

    // 입력을 위한 이메일과 패스워드
    var email: String? = null
    var password: String? = null
    // 회원가입 시에 필요한 추가 패스워드
    var password_check: String? = null

    // auth listener
    var authListener: AuthListener? = null

    // Completable을 사용하기 위한
    private val disposables = CompositeDisposable()

    val user by lazy {
        repository.currentUser()
    }

    // 로그인 수행을 위한 함수
    fun login(){

        // 이메일 패스워드 유효성 확인
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("유효하지 않은 이메일 또는 비밀번호")
            return
        }

        // authentication 시작
        authListener?.onStarted()

        // 실제 authentication을 수행하기 위한 repository의 login 호출
        val disposable = repository.login(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // success callback 보내기
                authListener?.onSuccess()
            }, {
                // fail callback 보내기
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    // 회원가입도 마찬가지로 동작
    fun register() {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            authListener?.onFailure("모든 값을 입력하세요")
            return
        }
        authListener?.onStarted()
        val disposable = repository.register(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun goToRegister(view: View){
        Intent(view.context, RegisterActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun goToLogin(view: View) {
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }
    }
    fun finish(){
        finish()
    }

    // disposing the disposable
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}