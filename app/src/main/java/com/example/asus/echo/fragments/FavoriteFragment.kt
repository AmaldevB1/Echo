package com.example.asus.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.asus.echo.R
import com.example.asus.echo.Songs
import com.example.asus.echo.adapters.FavScreenAdapter
import com.example.asus.echo.databases.EchoDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.lang.Exception


class FavoriteFragment : Fragment() {

    var myActivity: Activity? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recycler_view: RecyclerView? = null
    var noFavourites: TextView? = null
    var nowPlaying: RelativeLayout? = null
    var trackPosition: Int = 0
    var favContent: EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var getListFromDatabase: ArrayList<Songs>? = null
    var favAdapter: FavScreenAdapter? = null


    object staticated {
        var media2: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        activity!!.title = "Favorite Fragment"
        noFavourites = view?.findViewById(R.id.noFavourites)
        nowPlaying = view?.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        recycler_view = view?.findViewById(R.id.favRecView)
        return view
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favContent = EchoDatabase(myActivity)
        displayFavouritesbySearching()
        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()
    }

    fun getSongsFromPhone(): ArrayList<Songs> {

        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getInt(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified?.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified?.currentSongHelper?.songTitle)
                SongPlayingFragment.Statified.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                nowPlaying?.visibility = View.VISIBLE
            } else
                nowPlaying?.visibility = View.INVISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlaying?.setOnClickListener({

            staticated.media2 = SongPlayingFragment.Statified.mediaplayer
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified?.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified?.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified?.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.Statified?.currentSongHelper!!.songId)
            args.putInt("songPosition", SongPlayingFragment.Statified?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified?.fetchSongs)
            args.putString("FavBottomBar", "success")

            songPlayingFragment.arguments = args

            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment)
                ?.addToBackStack("SongPlayingFragmentFav")
                ?.commit()
        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaplayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaplayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }


        })
    }

    fun displayFavouritesbySearching() {

        if (favContent?.checkSize() as Int > 0) {
            refreshList = ArrayList<Songs>()
            getListFromDatabase = favContent?.queryDBList()
            var fetchListfromDevice = getSongsFromPhone()

            if (fetchListfromDevice?.size != 0) {

                for (i in 0..fetchListfromDevice?.size - 1) {
                    for (j in 0..getListFromDatabase?.size!!.minus(1)) {
                        if (getListFromDatabase?.get(j)?.songID == fetchListfromDevice?.get(i)?.songID) {
                            refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }
            }

            if (refreshList?.size == 0) {
                Toast.makeText(myActivity as Context, "favlist!=0", Toast.LENGTH_SHORT).show()
                recycler_view?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE
            } else {
                favAdapter = FavScreenAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recycler_view?.layoutManager = mLayoutManager
                recycler_view?.itemAnimator = DefaultItemAnimator()
                recycler_view?.adapter = favAdapter
                recycler_view?.setHasFixedSize(true)

            }
        } else {
            recycler_view?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }


    }
}


