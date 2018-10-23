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

    private val _cards : Navigator = Navigator(this, _db.getCards(_settings.GroupIdForTraining))
    private val _currCard : Card get() = _cards.currCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        _cards.setup()
        setupMoving()

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
            Log.i("TrainActivity", "Move button was clicked, move to $group")
            _currCard.GroupId = group
            _db.updateCard(_currCard)
        }

        move1.setOnClickListener { moveFunc(group1) }
        move2.setOnClickListener { moveFunc(group2) }

        // TODO: go on
    }

    private fun onCurrCardChanged() {
        // TODO: setup curr cards
    }

    private fun goToMain() {
        Log.i("TrainActivity", "Switch to main activity")
        val i = Intent(this@TrainActivity, MainActivity::class.java)
        startActivity(i)
    }

    class Navigator(private val _ctx : TrainActivity, private val _cards : ArrayList<Card>) {
        private var _currIdx : Int = -1
        private val _cardsCount : Int = _cards.size

        val currCard : Card
            get() = if (_currIdx >= 0 && _currIdx < _cards.size) _cards[_currIdx]
                    else throw Exception("Incorrect index")

        private val _textMin = _ctx.findViewById<TextView>(R.id.text_num_min)
        private val _textMax = _ctx.findViewById<TextView>(R.id.text_num_max)
        private val _textCurr = _ctx.findViewById<TextView>(R.id.text_num_curr)

        private val _seekBar = _ctx.findViewById<SeekBar>(R.id.seek_bar_position)
        private val _buttonPrev = _ctx.findViewById<Button>(R.id.at_button_prev)
        private val _buttonNext = _ctx.findViewById<Button>(R.id.at_button_next)

        fun setup() {
            _currIdx = 0
            _seekBar.max = _cardsCount - 1
            _seekBar.progress = 0

            _textMin.text = "1"
            _textCurr.text = "1"
            _textMax.text = _cards.size.toString()

            // TODO: setup events here

            _ctx.onCurrCardChanged()
        }
    }
}
