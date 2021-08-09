package elnahas.com.nagwatask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import elnahas.com.nagwatask.viewmodel.MainViewModel
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import elnahas.com.nagwatask.R
import elnahas.com.nagwatask.adapters.FilesAdapter
import elnahas.com.nagwatask.databinding.ActivityMainBinding
import elnahas.com.nagwatask.util.ResourceDownload
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var filesAdapter: FilesAdapter

    @Inject
    lateinit var ktor: HttpClient
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupRecyclerView()

        setObservers()

        filesAdapter.setOnItemClickListener { data, view ->

            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), data.name)
            mainViewModel.downloadFile(ktor, data, file, data.url)

        }

    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            mainViewModel.data.collect {
                filesAdapter.differ.submitList(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.resourceDownload.collect {
                when (it) {
                    is ResourceDownload.Success -> {
                        filesAdapter.setDownloading(
                            it.data,
                            isDownloading = false,
                            isFileExist = true
                        )
                    }
                    is ResourceDownload.Error -> {
                        filesAdapter.setDownloading(
                            it.data,
                            isDownloading = false,
                            isFileExist = false
                        )
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                    is ResourceDownload.Progress -> {
                        filesAdapter.setProgress(it.data, it.progress)
                    }
                }
            }
        }


        lifecycleScope.launch {
            mainViewModel.error.collect {
                Toast.makeText(this@MainActivity, "$it", Toast.LENGTH_SHORT).show()
            }

        }

        lifecycleScope.launchWhenStarted {

            mainViewModel.isloading.collect {
                binding.progressBar.isVisible = it
            }
        }
    }

    private fun setupRecyclerView() {
        filesAdapter = FilesAdapter()

        binding.recyclerView.apply {
            adapter = filesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }


    }
}

