package jp.techacademy.kenichi04.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.Date

// Serializableインターフェイス：生成したオブジェクトのシリアライズが可能
// -> データを丸ごとファイルに保存したり、別のActivityに渡したりできる
open class Task : RealmObject(), Serializable {    // Realmが内部的にTaskを継承したクラスを作成して利用するため、open修飾子を付ける
    var title: String = ""      // タイトル
    var contents: String = ""   // 内容
    var date: Date = Date()     // 日時

    @PrimaryKey
    var id: Int = 0
}