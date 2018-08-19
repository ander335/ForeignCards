package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ListView>(R.id._main_list)

        list.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, arrayOf(TrainMenuText, AddingMenuText, GroupsMenuText, SettingsMenuText))
    }

    companion object {
        private const val TrainMenuText = "Train"
        private const val AddingMenuText = "Add"
        private const val GroupsMenuText = "Groups"
        private const val SettingsMenuText = "Settings"
    }
}
