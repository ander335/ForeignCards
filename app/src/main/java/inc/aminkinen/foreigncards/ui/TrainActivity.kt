package inc.aminkinen.foreigncards.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import inc.aminkinen.foreigncards.Logger
import inc.aminkinen.foreigncards.R
import inc.aminkinen.foreigncards.database.DbProvider
import inc.aminkinen.foreigncards.entities.Card
import inc.aminkinen.foreigncards.entities.Settings
import inc.aminkinen.foreigncards.entities.View
import inc.aminkinen.foreigncards.entities.enums.GroupKind
import inc.aminkinen.foreigncards.entities.enums.TrainMode
import inc.aminkinen.foreigncards.logic.GroupManager
import java.text.SimpleDateFormat
import java.util.*


class TrainActivity : AppCompatActivity() {
    private val _db : DbProvider = DbProvider.Instance
    private val _settings : Settings = _db.getSettings()

    private var _wasShowed : Boolean = false
    private var _startViewingTime : Date = Date()
    private var _currView : View? = null

    private var _groupIdMoveUp : Int? = null
    private var _groupIdMoveDown : Int? = null

    private var _groupFilling : Map<Int, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        val texts = Texts(this)

        loadGroupsFilling()

        val cards = Navigator(this, texts, _db.getCards(_settings.GroupIdForTraining, _settings.CurrentLanguage))
        cards.setup()

        setupRemoving(cards)
        setupMoving(cards)
        setupShowing(cards, texts)
        setupUpdating(cards, texts)

        val close = findViewById<Button>(R.id.at_button_close)
        close.setOnClickListener {
            Log.info("Close button was clicked")
            goToMain()
        }
    }

    private fun setupRemoving(cards : Navigator) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("[TBD] Card removing")
        builder.setMessage("Are you sure? The history'll be lost!")
        builder.setPositiveButton("Yes") { dialog, arg ->
            Log.info("Remove button was clicked, remove ${cards.currCard}")

            _db.removeCard(cards.currCard.Id);
            cards.next()
        }

        builder.setNegativeButton("No") { dialog, arg ->
        }

        builder.setCancelable(true)
        builder.setOnCancelListener { dialog ->
        }

        val remove = findViewById<Button>(R.id.at_button_remove)
        remove.setOnClickListener {
            builder.show()
        }
    }

    private fun setupMoving(cards : Navigator) {
        if (GroupManager.getKind(cards.currCard.GroupId).upKind() == GroupKind.Green)
            _groupIdMoveUp = _settings.GreenReceiveGroupId
        else
            _groupIdMoveUp = GroupManager.getUpGroup(cards.currCard.GroupId, _groupFilling)

        _groupIdMoveDown = GroupManager.getDownGroup(cards.currCard.GroupId, _groupFilling)

        setupUpMoving(cards, _groupIdMoveUp)
        setupDownMoving(cards, _groupIdMoveDown)
    }

    private fun setupUpMoving(cards : Navigator, upId: Int?) {
        val move2 = findViewById<Button>(R.id.at_button_move_2)
        if (upId == null) {
            move2.text = ""
            move2.setOnClickListener { }
            return
        }

        move2.text = String.format(resources.getString(R.string.at_button_move_to_template), upId)
        move2.setOnClickListener { MoveCard(cards, upId) }
    }

    private fun setupDownMoving(cards : Navigator, downId: Int?) {
        val move1 = findViewById<Button>(R.id.at_button_move_1)
        if (downId == null) {
            move1.text = ""
            move1.setOnClickListener { }
            return
        }

        move1.text = String.format(resources.getString(R.string.at_button_move_to_template), downId)
        move1.setOnClickListener { MoveCard(cards, downId) }
    }

    private fun MoveCard(cards : Navigator, id: Int) {
        Log.info("Move button was clicked, move to $id")
        cards.currCard.GroupId = id
        _db.updateCard(cards.currCard)

        _currView!!.MovingGroupId = id

        loadGroupsFilling()

        cards.next()
    }

    private fun setupUpdating(cards : Navigator, texts: Texts) {
        val update = findViewById<Button>(R.id.at_button_update)
        update.setOnClickListener {
            val currCard = cards.currCard
            currCard.Word = texts.word.text.toString()
            if (_wasShowed) {
                currCard.Transl = texts.transl.text.toString()
                currCard.Transc = texts.transc.text.toString()
                currCard.Example = texts.example.text.toString()
            }
            if (!texts.group.text.toString().isEmpty()) {
                val newGroup = Integer.parseInt(texts.group.text.toString())
                if (currCard.GroupId != newGroup) {
                    currCard.GroupId = newGroup
                    _currView!!.MovingGroupId = newGroup
                }
            }

            Log.info("Update card: $currCard")
            _db.updateCard(currCard)

            loadGroupsFilling()

            cards.next()
        }
    }

    private fun loadGroupsFilling() {
        _groupFilling = _db.getGroupsFilling(_settings.CurrentLanguage)
        if (_settings.GreenReceiveGroupId == -1 ||
                _groupFilling.getOrElse(_settings.GreenReceiveGroupId) { 0 } >= GroupManager.getKindLimit(GroupKind.Green)) {
            _settings.GreenReceiveGroupId = GroupManager.findLowestGroup(GroupKind.Green, _groupFilling)
            _db.updateSettings(_settings)
        }
    }

    private fun setupShowing(cards : Navigator, texts: Texts) {
        val show = findViewById<Button>(R.id.at_button_show)
        show.setOnClickListener {
            val currCard = cards.currCard
            Log.info("Show card: $currCard")

            texts.word.setText(currCard.Word)
            texts.transl.setText(currCard.Transl)
            texts.transc.setText(currCard.Transc)
            texts.example.setText(currCard.Example)
            _wasShowed = true
        }
    }

    private fun onCurrCardChanged(cards : Navigator, texts: Texts) {
        val lv = _currView
        if (lv != null) {
            addView(lv)
        }

        val currCard = cards.currCard
        Log.info("Show new card: $currCard")

        texts.group.setText(currCard.GroupId.toString())
        texts.word.setText("")
        texts.transl.setText("")
        texts.transc.setText("")
        texts.example.setText("")

        if (_settings.TrainMode_ == TrainMode.FirstlyWord)
            texts.word.setText(currCard.Word)
        else
            texts.transl.setText(currCard.Transl)

        setupMoving(cards)

        _wasShowed = false
        _startViewingTime = Date()
        _currView = View(currCard.Id)
    }

    private fun addView(v: View) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val duration = Date().time - _startViewingTime.time

        v.Time = dateFormat.format(Date())
        v.Duration = duration / 1000.0

        _db.addView(v)
    }

    private fun goToMain() {
        Log.info("Switch to main activity")
        val i = Intent(this@TrainActivity, MainActivity::class.java)
        startActivity(i)
    }

    class Navigator(private val _ctx : TrainActivity, private val _texts: Texts, private val _cards : ArrayList<Card>) {
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

            _ctx.onCurrCardChanged(this, _texts)
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
            if (index < 0 || index >= _cardsCount)
                return

            Log.info("New position: $index")
            _currIdx = index
            syncUI()
            _ctx.onCurrCardChanged(this, _texts)
        }

        private fun syncUI() {
            _seekBar.progress = _currIdx
            _textCurr.text = (_currIdx + 1).toString()
        }
    }

    class Texts(ctx : TrainActivity) {
        val word = ctx.findViewById<EditText>(R.id.text_word)
        val transl = ctx.findViewById<EditText>(R.id.text_transl)
        val transc = ctx.findViewById<EditText>(R.id.text_transc)
        val group = ctx.findViewById<EditText>(R.id.text_group)
        val example = ctx.findViewById<EditText>(R.id.text_example)
    }

    companion object {
        private val Log : Logger = Logger("TrainActivity")
    }
}
