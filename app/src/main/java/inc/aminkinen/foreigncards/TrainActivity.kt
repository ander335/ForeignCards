package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
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

    private val _word = findViewById<EditText>(R.id.text_word)
    private val _transl = findViewById<EditText>(R.id.text_transl)
    private val _transc = findViewById<EditText>(R.id.text_transc)
    private val _group = findViewById<EditText>(R.id.text_group)

    private var _wasShowed : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        _cards.setup()
        setupMoving()
        setupShowing()
        setupUpdating()

        val close = findViewById<Button>(R.id.at_button_close)
        close.setOnClickListener {
            Log.info("Close button was clicked")
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
            Log.info("Move button was clicked, move to $group")
            _currCard.GroupId = group
            _db.updateCard(_currCard)

            _cards.next()
        }

        move1.setOnClickListener { moveFunc(group1) }
        move2.setOnClickListener { moveFunc(group2) }
    }

    private fun setupUpdating() {
        val update = findViewById<Button>(R.id.at_button_update)
        update.setOnClickListener {
            _currCard.Word = _word.text.toString()
            if (_wasShowed) {
                _currCard.Transl = _transl.text.toString()
                _currCard.Transc = _transc.text.toString()
            }
            if (!_group.text.toString().isEmpty())
                _currCard.GroupId = Integer.parseInt(_group.text.toString())

            Log.info("Update card: $_currCard")
            _db.updateCard(_currCard)
        }
    }

    private fun setupShowing() {
        val show = findViewById<Button>(R.id.at_button_show)
        show.setOnClickListener {
            Log.info("Show card: $_currCard")

            _transl.setText(_currCard.Transl)
            _transc.setText(_currCard.Transc)
            _wasShowed = true
        }
    }

    private fun onCurrCardChanged() {
        Log.info("Show new card: $_currCard")

        _word.setText(_currCard.Word)
        _group.setText(_currCard.GroupId.toString())
        _wasShowed = false
    }

    private fun goToMain() {
        Log.info("Switch to main activity")
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
            _seekBar.max = _cardsCount - 1

            _textMin.text = "1"
            _textMax.text = _cards.size.toString()

            _currIdx = 0
            syncUI()

            _buttonNext.setOnClickListener { next() }
            _buttonPrev.setOnClickListener { prev() }

            _seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(bar: SeekBar?, pos: Int, fromUser: Boolean) {
                    if (!fromUser)
                        return

                    Log.info("Seek bar was moved")
                    setPos(pos)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) { }
                override fun onStopTrackingTouch(p0: SeekBar?) { }
            })

            _ctx.onCurrCardChanged()
        }

        fun next() {
            Log.info("Move next")
            setPos(_currIdx + 1)
        }

        private fun prev() {
            Log.info("Move prev")
            setPos(_currIdx - 1)
        }

        private fun setPos(index : Int) {
            if (index < 0 || index >= _cardsCount - 1)
                return

            Log.info("New position: $index")
            _currIdx = index
            syncUI()
            _ctx.onCurrCardChanged()
        }

        private fun syncUI() {
            _seekBar.progress = _currIdx
            _textCurr.text = (_currIdx + 1).toString()
        }
    }

    companion object {
        private val Log : Logger = Logger("AddActivity")
    }
}
