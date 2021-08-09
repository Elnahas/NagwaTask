package elnahas.com.nagwatask.data.model


import com.google.gson.annotations.SerializedName

data class FileResponseModelItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    var url: String,

){
    var isDownloading: Boolean = false
    var progress :Int = 0
    var isFileExist :Boolean = false
}