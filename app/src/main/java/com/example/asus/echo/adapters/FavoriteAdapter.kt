package com.example.asus.echo.adapters


import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.asus.echo.R
import com.example.asus.echo.Songs
import com.example.asus.echo.fragments.SongPlayingFragment
import java.lang.Exception


class FavScreenAdapter(_songdetails: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<FavScreenAdapter.FavViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songdetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist
        holder.contentHolder?.setOnClickListener({
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.reset()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songId", songObject!!.songID)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragment.arguments = args
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongsPlayingFragmentFav")
                .commit()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_fav_adapter, parent, false)
        return FavViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        if (songDetails == null)
            return 0
        else
            return (songDetails as ArrayList<Songs>).size
    }


    inner class FavViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.trackTitlefav) as TextView
            trackArtist = view.findViewById(R.id.trackArtistfav) as TextView
            contentHolder = view.findViewById(R.id.contentRowfav) as RelativeLayout
        }
    }
}

