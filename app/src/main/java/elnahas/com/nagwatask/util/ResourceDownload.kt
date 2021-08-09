package elnahas.com.nagwatask.util

import elnahas.com.nagwatask.data.model.FileResponseModelItem

sealed class ResourceDownload {

    data class Success(val data : FileResponseModelItem) : ResourceDownload()

    data class Error(val data : FileResponseModelItem , val message: String) : ResourceDownload()

    data class Progress(val data : FileResponseModelItem , val progress: Int): ResourceDownload()
    object Empty: ResourceDownload()
}