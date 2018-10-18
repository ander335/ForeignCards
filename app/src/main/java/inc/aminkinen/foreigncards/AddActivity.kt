package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.util.Log
import android.widget.EditText
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.CardData


class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val word = findViewById<EditText>(R.id.text_word)
        val transl = findViewById<EditText>(R.id.text_transl)
        val transc = findViewById<EditText>(R.id.text_transc)

        val close = findViewById<View>(R.id.aa_button_close)
        close.setOnClickListener {
            Log.i("AddActivity", "Close button was clicked")
            goToMain()
        }

        val add = findViewById<View>(R.id.aa_button_add)
        add.setOnClickListener {
            Log.i("AddActivity", "Add button was clicked")
            val group = DbProvider.Instance.getSettings().GroupIdForAdding

            val c = CardData(
                    word.text.toString(),
                    transl.text.toString(),
                    transc.text.toString(),
                    "", // TODO: example
                    group)

            Log.i("AddActivity", "Add card: $c")
            DbProvider.Instance.addCard(c)
            goToMain()
        }
    }

    private fun goToMain() {
        Log.i("AddActivity", "Switch to main activity")
        val i = Intent(this@AddActivity, MainActivity::class.java)
        startActivity(i)
    }
}
