package ru.sinura.hackaton.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.sinura.hackaton.R
import ru.sinura.hackaton.repo.Repo
import ru.sinura.hackaton.timer.NotificationWorker

class MainActivity : AppCompatActivity() {

    val repo = Repo.getInstance()
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_news,
            R.id.navigation_vakcinaciya,
            R.id.navigation_profile
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        token = intent.extras?.get("TOKEN") as String

        NotificationWorker.scheduleNotification(this)
    }
}