package jp.techacademy.kenichi04.taskapp

import android.app.Application
import io.realm.Realm

class TaskApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Realm初期化
        Realm.init(this)
    }
}