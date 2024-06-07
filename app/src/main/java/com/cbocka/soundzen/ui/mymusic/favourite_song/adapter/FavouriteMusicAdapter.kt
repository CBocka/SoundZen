package com.cbocka.soundzen.ui.mymusic.favourite_song.adapter

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.ItemAudioFileLayoutBinding
import com.cbocka.soundzen.utils.Locator
import java.io.File
import java.util.concurrent.TimeUnit

class FavouriteMusicAdapter(
    private val context : Context,
    private val onClick : (Song, List<Song>) -> Unit,
    private val onLongClick : (Song) -> Boolean)
    : ListAdapter<Song, FavouriteMusicAdapter.FavouriteMusicViewHolder>(SONG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteMusicViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FavouriteMusicViewHolder(ItemAudioFileLayoutBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: FavouriteMusicViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    inner class FavouriteMusicViewHolder(private val binding : ItemAudioFileLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song : Song) {
            if (song.duration.isNotEmpty())
                binding.tvDuration.text = song.duration
            else
                FileDuration(song, File(song.filePath), binding.tvDuration).start()

            binding.tvSongName.text = song.songName
            binding.tvArtistName.text = song.artist
            binding.tvFileName.text = song.mp3Name

            setSeparatorColor(binding)

            itemView.setOnClickListener {
                onClick(song, currentList)
            }

            itemView.setOnLongClickListener {
                onLongClick(song)
            }
        }

        private fun setSeparatorColor(binding : ItemAudioFileLayoutBinding) {
            val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(context.getString(R.string.preference_theme_key), false)

            if (darkTheme)
                binding.vSeparator.setBackgroundColor(Color.parseColor("#ffffff"))
            else
                binding.vSeparator.setBackgroundColor(Color.parseColor("#141414"))
        }
    }

    companion object {

        private val SONG_COMPARATOR = object : DiffUtil.ItemCallback<Song>() {

            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.songName == newItem.songName
            }
        }
    }

    inner class FileDuration(private var song: Song,
                             private var file: File,
                             private var textView : TextView) : Thread() {
        override fun run() {
            super.run()

            song.duration = getDuration(file)
            textView.text = song.duration
        }

        private fun getDuration(file: File): String {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.path)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            retriever.release()

            // convert duration song to mm:ss
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}