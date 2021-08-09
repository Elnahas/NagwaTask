package elnahas.com.nagwatask.util

import elnahas.com.nagwatask.data.model.FileResponseModelItem
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

suspend fun HttpClient.downloadFile(dataModel : FileResponseModelItem, file: File, url: String): Flow<ResourceDownload> {

    return flow {

        try {
            val response = call {
                url(url)
                method = HttpMethod.Get
            }.response

            response.contentLength()?.let {

                val data = ByteArray(response.contentLength()!!.toInt())
                var offset = 0
                do {
                    val currentRead = response.content.readAvailable(data, offset, data.size)
                    offset += currentRead
                    val progress = (offset * 100f / data.size).roundToInt()
                    emit(ResourceDownload.Progress(dataModel ,progress))
                } while (currentRead > 0)
                response.close()
                if (response.status.isSuccess()) {
                    file.writeBytes(data)
                    emit(ResourceDownload.Success(dataModel))
                } else {
                    emit(ResourceDownload.Error(dataModel ,"File not downloaded"))
                }
            }

        } catch (ex: Exception) {
            emit(ResourceDownload.Error(dataModel , "File not downloaded ${ex.message}"))
        }

    }
}