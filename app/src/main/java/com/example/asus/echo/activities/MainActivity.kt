package com.example.asus.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.asus.echo.R
import com.example.asus.echo.adapters.NavigationDrawerAdapter
import com.example.asus.echo.fragments.MainScreenFragment
import com.example.asus.echo.fragments.SongPlayingFragment

import java.lang.Exception


class MainActivity : AppCompatActivity() {

    var trackNotifBuilder: Notification? = null
    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    var images_for_navdrawer = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notifmanager: NotificationManager? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")


        val toggle = ActionBarDrawerToggle(
            this@MainActivity, MainActivity.Statified.drawerLayout,
            toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        MainActivity.Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
            .commit()

        var naviadapter = NavigationDrawerAdapter(navigationDrawerIconsList, images_for_navdrawer, this)
        naviadapter.notifyDataSetChanged()

        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = naviadapter
        navigation_recycler_view.setHasFixedSize(true)


        val pintent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this@MainActivity, System.currentTimeMillis().toInt(),
            pintent, 0
        )


        trackNotifBuilder = Notification.Builder(this)
            .setContentTitle("A track is playing in background")
            .setSmallIcon(R.drawable.echo_icon)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notifmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }


    override fun onStart() {
        super.onStart()
        try {
            Statified.notifmanager?.cancel(1978)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                Statified.notifmanager?.notify(1978, trackNotifBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notifmanager?.cancel(1978)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


