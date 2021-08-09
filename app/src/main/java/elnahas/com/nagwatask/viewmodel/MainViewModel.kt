package elnahas.com.nagwatask.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import elnahas.com.nagwatask.data.model.FileResponseModel
import elnahas.com.nagwatask.data.model.FileResponseModelItem
import elnahas.com.nagwatask.data.repository.MainRepository
import elnahas.com.nagwatask.util.Resource
import elnahas.com.nagwatask.util.ResourceDownload
import elnahas.com.nagwatask.util.downloadFile
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel @ViewModelInject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _data: MutableStateFlow<FileResponseModel> = MutableStateFlow(FileResponseModel())
    var data: StateFlow<FileResponseModel> = _data

    private val _isloading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isloading: StateFlow<Boolean> = _isloading

    private val _error: MutableStateFlow<String> = MutableStateFlow(String())
    var error: StateFlow<String> = _error

    private val _resourceDownload = MutableStateFlow<ResourceDownload>(ResourceDownload.Empty)
    val resourceDownload: StateFlow<ResourceDownload> = _resourceDownload


    init {
        getFilesList()
    }

    fun getFilesList() = viewModelScope.launch {
        mainRepository.getFilesList().onEach {
            when (it) {
                is Resource.Success -> {
                    _isloading.value = false
                    _data.value = it.data
                }
                is Resource.Error -> {
                    _isloading.value = false
                    _error.value = it.exception
                }
                is Resource.Loading -> {
                    _isloading.value = true
                }
            }
        }.launchIn(viewModelScope)

    }

    fun downloadFile( ktor: HttpClient, data: FileResponseModelItem, file: File, url: String) {

        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(data, file, url).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is ResourceDownload.Success -> {
                            _resourceDownload.value = ResourceDownload.Success(data)
                        }
                        is ResourceDownload.Error -> {
                            _resourceDownload.value = ResourceDownload.Error(data, it.message)
                        }
                        is ResourceDownload.Progress -> {
                            _resourceDownload.value = ResourceDownload.Progress(data, it.progress)
                        }
                    }
                }
            }
        }

    }

}