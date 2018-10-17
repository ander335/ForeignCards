package inc.aminkinen.foreigncards

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    private fun goToMain() {
        Log.i("SettingsActivity", "Switch to main activity")
        val i = Intent(this@SettingsActivity, MainActivity::class.java)
        startActivity(i)
    }
}
