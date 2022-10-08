package com.pnam.note.ui.adapters.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.models.Download

class DownloadAdapter(
    private val initProgress: (id: Long, position: Int) -> Unit,
    private val detachView: (id: Long) -> Unit
): ListAdapter<Download, DownloadAdapter.DownloadViewHolder>(DIFF) {
    companion object {
        private val DIFF: DiffUtil.ItemCallback<Download> by lazy {
            object: DiffUtil.ItemCallback<Download>() {
                override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
                    return oldItem.id == newItem.id
                }
                override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }

    class DownloadViewHolder(
        itemView: View,
        private val initProgress: (id: Long, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView
        private val status: TextView
        private val progressText: TextView
        private val progressPercent: TextView
        private val progress: ProgressBar

        init {
            name = itemView.findViewById(R.id.name)
            status = itemView.findViewById(R.id.status)
            progressText = itemView.findViewById(R.id.progress_text)
            progressPercent = itemView.findViewById(R.id.progress_percent)
            progress = itemView.findViewById(R.id.progress)
        }

        @SuppressLint("SetTextI18n")
        fun setProgress(bytesDownloaded: Long, bytesTotal: Long) {
            val progressInt = ((bytesDownloaded.toFloat() / bytesTotal) * 100).toInt()
            progress.progress = progressInt
            progressPercent.text = "$progressInt%"
            if (bytesDownloaded == (-1).toLong() && bytesTotal == (-1).toLong()) {
                progressText.text = "Done"
            } else {
                progressText.text = "$bytesDownloaded/$bytesTotal"
            }
        }

        fun setDownloadStatus(downloadStatus: String) {
            status.text = downloadStatus
        }

        fun bind(download: Download, position: Int) {
            name.text = download.name
            initProgress(download.id, position)
        }

        companion object {
            fun create(
                parent: ViewGroup,
                viewType: Int,
                initProgress: (id: Long, position: Int) -> Unit
            ): DownloadViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_download, parent, false)
                return DownloadViewHolder(view, initProgress)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder.create(parent, viewType, initProgress)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onViewDetachedFromWindow(holder: DownloadViewHolder) {
        super.onViewDetachedFromWindow(holder)
        detachView(getItem(holder.adapterPosition).id)
    }
}