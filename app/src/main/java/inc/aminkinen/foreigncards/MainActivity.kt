package inc.aminkinen.foreigncards

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.TextView
import android.widget.ListView
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.Settings


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ListView>(R.id._main_list)

        list.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                arrayOf(TrainMenuText, AddingMenuText, GroupsMenuText, SettingsMenuText))

        list.setOnItemClickListener { _: AdapterView<*>, view: View, _: Int, _: Long ->
            val textView = view as? TextView ?: return@setOnItemClickListener
            when (textView.text.toString()) {
                TrainMenuText -> goToTrain()
                AddingMenuText -> goToAdd()
                GroupsMenuText -> goToGroups()
                SettingsMenuText -> goToSettings()
            }
        }
    }

    private fun goToAdd() {
        Log.info("Go to add")

        startActivity(Intent(this@MainActivity, AddActivity::class.java))
    }

    private fun goToSettings() {
        Log.info("Go to settings")

        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
    }

    private fun goToGroups() {
        Log.info("Go to groups [TBD] not implemented!")
    }

    private fun goToTrain() {
        Log.info("Go to train")

        // TODO: optimize
        val db : DbProvider = DbProvider.Instance
        val settings : Settings = db.getSettings()
        val cardsCount = db.getCards(settings.GroupIdForTraining).size
        if (cardsCount == 0) {
            Log.info("Train deck is empty, show message!")

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_title_info)
            builder.setMessage(R.string.activity_main_train_empty_cards)
            builder.setCancelable(true)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            builder.create().show()
            return
        }

        startActivity(Intent(this@MainActivity, TrainActivity::class.java))
    }

    companion object {
        private const val TrainMenuText = "Train"
        private const val AddingMenuText = "Add"
        private const val GroupsMenuText = "Groups"
        private const val SettingsMenuText = "Settings"

        private val Log : Logger = Logger("MainActivity")
    }
}
