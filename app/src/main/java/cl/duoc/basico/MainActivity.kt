package cl.duoc.basico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import cl.duoc.basico.model.AppDatabase
import cl.duoc.basico.ui.AppNavigation
import cl.duoc.basico.ui.theme.BasicoTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "junaeb_db"
        ).allowMainThreadQueries().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicoTheme {
                AppNavigation(db)
            }
        }
    }
}
