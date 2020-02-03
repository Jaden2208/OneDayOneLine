package com.whalez.onedayoneline

import android.app.Application
import com.whalez.onedayoneline.data.firebase.FirebaseSource
import com.whalez.onedayoneline.data.repositories.UserRepository
import com.whalez.onedayoneline.ui.auth.AuthViewModelFactory
import com.whalez.onedayoneline.ui.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/*
 *  FirebaseApplication 은 사용되는 클래스 임!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
class FirebaseApplication : Application(), KodeinAware{

    override val kodein = Kodein.lazy {
        import(androidXModule(this@FirebaseApplication))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }

    }
}