package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.Settings


class TextWatcherEx(private val action : (g : Int, settings : Settings) -> Settings) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
        val group = if (p0.toString().isEmpty()) 0 else Integer.parseInt(p0.toString())
        val settings = DbProvider.Instance.getSettings()
        val newS = action(group, settings)
        DbProvider.Instance.updateSettings(newS)
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val db = DbProvider.Instance
        val settings = db.getSettings()

        val adding = findViewById<EditText>(R.id.text_group_for_adding)
        val training = findViewById<EditText>(R.id.text_group_for_training)
        val moving1 = findViewById<EditText>(R.id.text_group_for_moving_1)
        val moving2 = findViewById<EditText>(R.id.text_group_for_moving_2)
        val count = findViewById<TextView>(R.id.text_count)

        adding.setText("${settings.GroupIdForAdding}")
        training.setText("${settings.GroupIdForTraining}")
        moving1.setText("${settings.GroupIdForMoving1}")
        moving2.setText("${settings.GroupIdForMoving2}")
        count.text = db.cardsCount().toString()

        adding.addTextChangedListener(TextWatcherEx { g : Int, s : Settings ->
            Log.info("Change adding group: $g (old: ${s.GroupIdForAdding})")
            s.GroupIdForAdding = g
            s
        })
        training.addTextChangedListener(TextWatcherEx { g : Int, s : Settings ->
            Log.info("Change training group: $g (old: ${s.GroupIdForTraining})")
            s.GroupIdForTraining = g
            s
        })
        moving1.addTextChangedListener(TextWatcherEx { g : Int, s : Settings ->
            Log.info("Change moving1 group: $g (old: ${s.GroupIdForMoving1})")
            s.GroupIdForMoving1 = g
            s
        })
        moving2.addTextChangedListener(TextWatcherEx { g : Int, s : Settings ->
            Log.info("Change moving2 group: $g (old: ${s.GroupIdForMoving2})")
            s.GroupIdForMoving2 = g
            s
        })

        val close = findViewById<Button>(R.id.sa_button_close)
        close.setOnClickListener {
            Log.info("Close button was clicked")
            goToMain()
        }
    }

    private fun goToMain() {
        Log.info("Switch to main activity")
        val i = Intent(this@SettingsActivity, MainActivity::class.java)
        startActivity(i)
    }

    companion object {
        private val Log : Logger = Logger("SettingsActivity")
    }
}
