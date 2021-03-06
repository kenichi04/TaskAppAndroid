package jp.techacademy.kenichi04.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val EXTRA_TASK = "jp.techacademy.kenichi04.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    // Realmデータベースに追加や削除などの変化があった場合に呼ばれるリスナー: リスト更新するため
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 新規タスクの作成
        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()     // オブジェクト取得
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this)

        // searchViewの設定
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText.equals("")) {
                    reloadListView()
                }
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("TaskApp", query)

                val results = mRealm.where(Task::class.java).equalTo("category", query).findAll().sort("date", Sort.DESCENDING)
                mTaskAdapter.taskList = mRealm.copyFromRealm(results)
                listView1.adapter = mTaskAdapter
                mTaskAdapter.notifyDataSetChanged()

                return false
            }
        })

        // ListViewタップ時の処理
        listView1.setOnItemClickListener { parent, view, position, id ->
            // 入力・編集する画面に遷移
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // 長押し時の処理
        listView1.setOnItemLongClickListener { parent, view, position, id ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示
            val builder = AlertDialog.Builder(this)
            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                // 設定したアラームを解除する（セット時と同じIntent,PendingIntentを作成し、cancelメソッドで解除）
                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("CANCEL",null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        // アプリ起動時に表示テスト用のタスクを作成
//        addTaskForTest()

        reloadListView()
    }

    private fun reloadListView() {

        // Realmデータベースから全てのデータを取得、新しい日付順に並べた結果を取得
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)
        // 上記結果をTaskListとしてセットする（取得データを直接渡すのではなく、コピーして渡す必要がある）
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)
        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter
        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }

    /*
    private fun addTaskForTest() {
        val task = Task()
        task.title = "作業"
        task.contents = "プログラムを書いてPUSHする"
        task.date = Date()
        task.id = 0
        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(task)
        mRealm.commitTransaction()
    }
    */

}