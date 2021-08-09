package elnahas.com.nagwatask.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import elnahas.com.nagwatask.data.api.ApiService
import elnahas.com.nagwatask.data.model.FileResponseModel
import elnahas.com.nagwatask.data.model.FileResponseModelItem
import elnahas.com.nagwatask.util.Constants
import elnahas.com.nagwatask.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(val api: ApiService) {


    fun getFilesListFromGsonFile(): FileResponseModel {
        val gson = Gson()
        val filesList = object : TypeToken<FileResponseModel>() {}.type
        return gson.fromJson(Constants.BASE_FILE, filesList)
    }


//    suspend fun getFilesList(): Flow<Resource<FileResponseModel>> = flow {
//        emit(Resource.Loading(true))
//        try {
//            val response = getFilesListFromGsonFile()
//            val entity = FileResponseModel()
//
//            response.forEach { item ->
//                entity.add(
//                    FileResponseModelItem(
//                        item.id,
//                        item.name,
//                        item.type,
//                        item.url
//                    )
//                )
//            }
//
//            emit(Resource.Success(entity))
//        } catch (e: Exception) {
//            emit(Resource.Error(e.message!!))
//        }
//
//    }

    fun getFilesList(): Flow<Resource<FileResponseModel>> = flow {

        try {
            val searchResult = api.getFiles()
            emit(Resource.Success(searchResult))
        } catch (ex: Exception) {
            emit(Resource.Error(ex.message!!))
        }

    }.flowOn(Dispatchers.IO)



}