package com.whalez.onedayoneline.ui.home

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.whalez.onedayoneline.data.repositories.UserRepository
import com.whalez.onedayoneline.ui.post.PostActivity
import com.whalez.onedayoneline.utils.startLoginActivity

class HomeViewModel(
    private val repository: UserRepository
): ViewModel() {

    val user by lazy {
        repository.currentUser()
    }

    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity()
    }

//    fun goToPost(view: View){
//        Intent(view.context, PostActivity::class.java).also {
//            view.context.startActivity(it)
//        }
//    }

//    fun showUserMenu(){
//
//    }
}