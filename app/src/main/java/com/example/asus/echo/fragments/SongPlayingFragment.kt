package com.example.asus.echo.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.asus.echo.CurrentSongHelper
import com.example.asus.echo.R
import com.example.asus.echo.Songs
import com.example.asus.echo.databases.EchoDatabase
import com.example.asus.echo.fragments.FavoriteFragment
import java.util.*
import java.util.concurrent.TimeUnit

class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null

        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var prevImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var shuffleImageButton: ImageButton? = null

        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null

        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int? = null
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisual: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fav: ImageButton? = null

        val MYPREFSNAME = "Shakefeature"


        var myprefsshuffle = "Shuffle feature"
        var myprefloop = "Loop feature"


        var favContent: EchoDatabase? = null


        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    playNext("PlayNextLikeLoop")
                    Statified.currentSongHelper?.isPlaying = true
                } else {
                    Statified.currentSongHelper?.isPlaying = true
                    playNext("PlayNextNormal")

                }
            }
            if (Statified.favContent?.checkifIdExist(Statified.currentSongHelper?.songId as Int) as Boolean) {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )

            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            Statified.songArtistView?.setText(songArtistUpdated)

            Statified.songTitleView?.setText(songTitleUpdated)

        }

        fun processInformation(mediaplayer: MediaPlayer) {
            val finalTime = mediaplayer.duration
            val startTime = mediaplayer.currentPosition

            Statified.seekbar?.max = finalTime

            Statified.startTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()  ),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()  )
                            - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()  ))
                )
            )

            Statified.endTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())
                            - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
                )
            )

            Statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", ignoreCase = true)) {
                Statified.currentSongHelper?.isLoop = false

                Statified.currentPosition = Statified.currentPosition?.plus(1)
            } else if (check.equals("PlayNextLikeNormalShuffle", ignoreCase = true)) {
                Statified.currentSongHelper?.isLoop = false

                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            } else if (check.equals("PlayNextLikeLoop", ignoreCase = true)) {


            }

            if (Statified.currentPosition == Statified.fetchSongs?.size)
                Statified.currentPosition = 0

            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition as Int)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong!!.songID

            Statified.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(
                    Statified.myActivity as Context,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (Statified.favContent?.checkifIdExist(Statified.currentSongHelper?.songId as Int) as Boolean) {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )

            }
        }

        fun playPrev() {
            Statified.currentPosition = Statified.currentPosition?.minus(1)
            if (Statified.currentPosition?.equals(-1) as Boolean)
                Statified.currentPosition = 0

            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }


            if (Statified.currentPosition == Statified.fetchSongs?.size)
                Statified.currentPosition = 0

            Statified.currentSongHelper?.isLoop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition as Int)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong!!.songID

            Statified.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(
                    Statified.myActivity as Context,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favContent?.checkifIdExist(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )

            }
        }

        var updateSongTime = object : Runnable {
            override fun run() {
                val getcurrent = Statified.mediaplayer?.currentPosition
                Statified.startTimeText?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong())
                                - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong()))
                    )
                )


                Handler().postDelayed(this, 1000)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)

        setHasOptionsMenu(true)
        activity!!.title = "Now Playing"
        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.prevImageButton = view?.findViewById(R.id.prevButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fav = view?.findViewById(R.id.favicon)
        Statified.fav?.alpha = 0.8f

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisual = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {

        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisual?.onResume()
        Statified.mSensorManager?.registerListener(
            Statified.mSensorListener,
            Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        Statified.audioVisual?.onPause()
        super.onPause()

        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)

    }

    override fun onDestroyView() {
        Statified.audioVisual?.release()
        super.onDestroyView()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccel = 0.0f
        mAccelCurr = SensorManager.GRAVITY_EARTH
        mAccelLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater!!.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favContent = EchoDatabase(Statified.myActivity)


        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Int = 0

        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments!!.getInt("songId")

            Statified.currentPosition = arguments?.getInt("songPosition")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Statified.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaplayer = FavoriteFragment.staticated.media2
        } else {
            Statified.mediaplayer = MediaPlayer()
            Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity as Context, Uri.parse(path))
                Statified.mediaplayer?.prepare()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaplayer?.start()
        }
        Statified.processInformation(Statified.mediaplayer as MediaPlayer)

        if (Statified.mediaplayer?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        Statified.mediaplayer?.setOnCompletionListener {
            Statified.onSongComplete()
        }
        clickHandler()
        val vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisual?.linkTo(vizualizerHandler)

        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(Statified.myprefsshuffle, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Statified.myprefloop, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        if (Statified.favContent?.checkifIdExist(Statified.currentSongHelper?.songId as Int) as Boolean) {
            Statified.fav?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myActivity as Context,
                    R.drawable.favorite_on
                )
            )
        } else {
            Statified.fav?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myActivity as Context,
                    R.drawable.favorite_off
                )
            )

        }
    }

    fun clickHandler() {


        Statified.fav?.setOnClickListener({
            if (Statified.favContent?.checkifIdExist(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
                Statified.favContent?.deleteFavorite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity as Context, "Removed from favourites", Toast.LENGTH_SHORT).show()
            } else {
                Statified.fav?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
                Statified.favContent?.storeAsFavorite(
                    Statified.currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath
                )
                Toast.makeText(Statified.myActivity as Context, "Added to favourites", Toast.LENGTH_SHORT).show()

            }
        })
        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(Statified.myprefsshuffle, Context.MODE_PRIVATE)?.edit()
            var editorLoop =
                Statified.myActivity?.getSharedPreferences(Statified.myprefloop, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.currentSongHelper?.isShuffle = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })

        Statified.prevImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.playNext("PlayNextLikeLoop")
            } else if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                Statified.playNext("PlayNextLikeNormalShuffle")
            } else
                Statified.playPrev()
        })
        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                Statified.playNext("PlayNextLikeNormalShuffle")

            } else if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.playNext("playNextLikeLoop")
            } else {

                Statified.playNext("PlayNextNormal")
            }
        })
        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(Statified.myprefsshuffle, Context.MODE_PRIVATE)?.edit()
            var editorLoop =
                Statified.myActivity?.getSharedPreferences(Statified.myprefloop, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })
        Statified.playPauseImageButton?.setOnClickListener({
            if (Statified.mediaplayer?.isPlaying as Boolean) {
                Statified.mediaplayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaplayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }


        })
    }

    var mAccel: Float = 0f
    var mAccelCurr: Float = 0f
    var mAccelLast = 0f
    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                val x = event!!.values[0]
                val y = event.values[1]
                val z = event.values[2]
                mAccelLast = mAccelCurr
                mAccelCurr = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = mAccelCurr - mAccelLast
                mAccel = mAccel * 0.9f + delta
                if (mAccel > 12) {
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MYPREFSNAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean)
                        Statified.onSongComplete()

                }
            }

        }
    }


}


