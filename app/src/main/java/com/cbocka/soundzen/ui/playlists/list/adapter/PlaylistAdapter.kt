package com.cbocka.soundzen.ui.playlist.adapter

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
import com.cbocka.soundzen.data.model.Playlist
import com.cbocka.soundzen.databinding.ItemPlaylistLayoutBinding
import com.cbocka.soundzen.utils.Locator
import java.io.File
import java.util.concurrent.TimeUnit

class PlaylistAdapter(
    private val context : Context,
    private val onClick : (Playlist) -> Unit,
    private val onLongClick : (Playlist) -> Boolean
): ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistAdapter.PLAYLIST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(ItemPlaylistLayoutBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    inner class PlaylistViewHolder(private val binding : ItemPlaylistLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist : Playlist) {

            binding.tvPlaylistName.text = playlist.name

            binding.tvPlaylistSize.text = context.getString(R.string.item_playlist_size_text) + playlist.songsIncluded.size

            setSeparatorColor(binding)

            itemView.setOnClickListener {
                onClick(playlist)
            }

            itemView.setOnLongClickListener {
                onLongClick(playlist)
            }
        }

        private fun setSeparatorColor(binding : ItemPlaylistLayoutBinding) {
            val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(context.getString(
                R.string.preference_theme_key), false)

            if (darkTheme)
                binding.vSeparator.setBackgroundColor(Color.parseColor("#ffffff"))
            else
                binding.vSeparator.setBackgroundColor(Color.parseColor("#141414"))
        }
    }

    companion object {

        private val PLAYLIST_COMPARATOR = object : DiffUtil.ItemCallback<Playlist>() {

            override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}