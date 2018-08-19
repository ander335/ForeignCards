package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.content.Intent
import android.widget.TextView



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ListView>(R.id._main_list)

        list.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, arrayOf(TrainMenuText, AddingMenuText, GroupsMenuText, SettingsMenuText))

        list.setOnItemClickListener { _: AdapterView<*>, view: View, _: Int, _: Long ->
            val textView = view as? TextView ?: return@setOnItemClickListener
            when (textView.text.toString()) {
                TrainMenuText -> return@setOnItemClickListener // startActivity(Intent(this, BarsikActivity::class.java))
                AddingMenuText -> return@setOnItemClickListener
                GroupsMenuText -> return@setOnItemClickListener
                SettingsMenuText -> return@setOnItemClickListener
            }
        }
    }

    companion object {
        private const val TrainMenuText = "Train"
        private const val AddingMenuText = "Add"
        private const val GroupsMenuText = "Groups"
        private const val SettingsMenuText = "Settings"
    }
}
