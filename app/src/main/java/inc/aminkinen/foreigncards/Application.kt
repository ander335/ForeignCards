package inc.aminkinen.foreigncards

import android.app.Application
import inc.aminkinen.foreigncards.database.DbProvider

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        DbProvider.init(applicationContext)
    }
}