package jp.techacademy.kenichi04.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(context: Context): BaseAdapter() {
    // LayoutInflater: 他のxmlリソースのViewを取り扱う扱うための仕組み
    private val mLayoutInflater: LayoutInflater
    var taskList = mutableListOf<Task>()

    // コンストラクタ
    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    // アイテム（データ）の数
    override fun getCount(): Int {
        return taskList.size
    }

    // アイテム（データ）を返す
    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return taskList[position].id.toLong()
    }

    // Viewを返す
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // :? -> 左辺がnullの場合に右辺を返す
        // 今回はconvertViewにはnullが入る模様: simple_list_item_2 -> タイトルとサブタイトルのあるセル
        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)

        textView1.text = taskList[position].title + "（" + taskList[position].category + "）"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = taskList[position].date
        textView2.text = simpleDateFormat.format(date)

        return view
    }



}