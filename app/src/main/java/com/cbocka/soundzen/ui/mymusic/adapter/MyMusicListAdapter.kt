package com.cbocka.soundzen.ui.mymusic.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.ItemAudioFileLayoutBinding
import com.cbocka.soundzen.utils.Locator

class MyMusicListAdapter(val context : Context) : ListAdapter<Song, MyMusicListAdapter.MyMusicViewHolder>(SONG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMusicViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyMusicViewHolder(ItemAudioFileLayoutBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyMusicViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    inner class MyMusicViewHolder(private val binding : ItemAudioFileLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song : Song) {
            binding.tvSongName.text = song.songName
            binding.tvArtistName.text = song.artist
            binding.tvDuration.text = song.duration
            binding.tvFileName.text = song.mp3Name

            setSeparatorColor(binding)
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
}