package inc.aminkinen.foreigncards.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.EditText
import inc.aminkinen.foreigncards.Logger
import inc.aminkinen.foreigncards.R
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.CardData


class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val word = findViewById<EditText>(R.id.text_word)
        val transl = findViewById<EditText>(R.id.text_transl)
        val transc = findViewById<EditText>(R.id.text_transc)
        val example = findViewById<EditText>(R.id.text_example)

        val close = findViewById<View>(R.id.aa_button_close)
        close.setOnClickListener {
            Log.info("Close button was clicked")
            goToMain()
        }

        val add = findViewById<View>(R.id.aa_button_add)
        add.setOnClickListener {
            Log.info("Add button was clicked")
            val settings = DbProvider.Instance.getSettings()

            val c = CardData(
                    word.text.toString(),
                    transl.text.toString(),
                    transc.text.toString(),
                    example.text.toString(),
                    settings.GroupIdForAdding,
                    settings.CurrentLanguage)

            Log.info("Add card: $c")
            DbProvider.Instance.addCard(c)
            goToMain()
        }
    }

    private fun goToMain() {
        Log.info("Switch to main activity")
        val i = Intent(this@AddActivity, MainActivity::class.java)
        startActivity(i)
    }

    companion object {
        private val Log : Logger = Logger("AddActivity")
    }
}
