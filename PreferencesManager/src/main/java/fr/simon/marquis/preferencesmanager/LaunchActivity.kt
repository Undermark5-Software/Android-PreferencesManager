package fr.simon.marquis.preferencesmanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import fr.simon.marquis.preferencesmanager.ui.applist.AppListActivity
import fr.simon.marquis.preferencesmanager.util.Shell
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A Splash activity to launch libsu shell request
 */
class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        lifecycleScope.launch {
            Shell.getShell()
            startActivity(Intent(this@LaunchActivity, AppListActivity::class.java))
            finish()
        }
    }
}
