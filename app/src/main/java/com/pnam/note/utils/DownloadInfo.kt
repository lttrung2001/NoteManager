package com.pnam.note.utils

sealed class DownloadInfo {
    data class HasInfo(
        val name: String,
        val status: String,
        val reason: String
    ) : DownloadInfo()

    object NotHasInfo : DownloadInfo() {
        override fun toString(): String {
            return "No information"
        }
    }
}
