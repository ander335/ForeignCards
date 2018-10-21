package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.Card
import inc.aminkinen.foreigncards.entities.Settings


class TrainActivity : AppCompatActivity() {
    private val _db : DbProvider = DbProvider.Instance
    private val _settings : Settings = _db.getSettings()
    private val _cards : ArrayList<Card> = _db.getCards(_settings.GroupIdForTraining)

    private var _currIdx : Int = -1
    private val _currCard : Card
        get() = if (_currIdx >= 0 && _currIdx < _cards.size) _cards[_currIdx]
                else throw Exception("Incorrect index")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        setupMoving()
        setupNavigation()


        //val adding = findViewById<TextView>(R.id.text_group_for_adding)

        val close = findViewById<Button>(R.id.at_button_close)
        close.setOnClickListener {
            Log.i("TrainActivity", "Close button was clicked")
            goToMain()
        }
    }

    private fun setupMoving() {
        val group1 = _settings.GroupIdForMoving1
        val group2 = _settings.GroupIdForMoving2

        val move1 = findViewById<Button>(R.id.at_button_move_1)
        val move2 = findViewById<Button>(R.id.at_button_move_2)

        move1.text = String.format(resources.getString(R.string.at_button_move_to_template), group1)
        move2.text = String.format(resources.getString(R.string.at_button_move_to_template), group2)

        val moveFunc = { group : Int ->
            if (_cards.size == 0) {
                Log.i("TrainActivity", "Move button was clicked, nothing to do")
            } else {
                Log.i("TrainActivity", "Move button was clicked, move to $group")
                _currCard.GroupId = group
                _db.updateCard(_currCard)
            }
        }

        move1.setOnClickListener { moveFunc(group1) }
        move2.setOnClickListener { moveFunc(group2) }
    }

    private fun setupNavigation() {
        val textMin = findViewById<TextView>(R.id.text_num_min)
        val textMax = findViewById<TextView>(R.id.text_num_max)
        val textCurr = findViewById<TextView>(R.id.text_num_curr)

        textMin.text = "1"
        textCurr.text = "1"
        textMax.text = _cards.size.toString()

        _currIdx = 0;

        val seekBar = findViewById<SeekBar>(R.id.seek_bar_position)
        seekBar.max = _cards.size - 1
        seekBar.progress = _currIdx
    }


    private fun goToMain() {
        Log.i("TrainActivity", "Switch to main activity")
        val i = Intent(this@TrainActivity, MainActivity::class.java)
        startActivity(i)
    }
}
