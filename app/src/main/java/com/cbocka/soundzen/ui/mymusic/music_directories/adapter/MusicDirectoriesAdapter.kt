package com.cbocka.soundzen.ui.mymusic.music_directories.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.ItemAudioFileLayoutBinding
import com.cbocka.soundzen.databinding.ItemMusicDirectoryLayoutBinding

class MusicDirectoriesAdapter (private val context : Context, private val onClick : (MusicDirectory) -> Unit)
: ListAdapter<MusicDirectory, MusicDirectoriesAdapter.MusicDirectoryViewHolder>(DIRECTORY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicDirectoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MusicDirectoryViewHolder(ItemMusicDirectoryLayoutBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MusicDirectoryViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    inner class MusicDirectoryViewHolder(private val binding : ItemMusicDirectoryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(musicDirectory: MusicDirectory) {
            binding.tvDirectoryName.text = musicDirectory.name
            binding.tvDirectoryPath.text = musicDirectory.path

            itemView.setOnClickListener { onClick(musicDirectory) }
        }
    }

    companion object {

        private val DIRECTORY_COMPARATOR = object : DiffUtil.ItemCallback<MusicDirectory>() {

            override fun areItemsTheSame(oldItem: MusicDirectory, newItem: MusicDirectory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MusicDirectory, newItem: MusicDirectory): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}