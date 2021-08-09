package elnahas.com.nagwatask.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import elnahas.com.nagwatask.data.model.FileResponseModelItem
import elnahas.com.nagwatask.databinding.ItemFileBinding
import kotlinx.android.synthetic.main.item_file.view.*

class FilesAdapter :
    RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    val diffCallback = object : DiffUtil.ItemCallback<FileResponseModelItem>(){
        override fun areItemsTheSame(oldItem: FileResponseModelItem, newItem: FileResponseModelItem): Boolean {

            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(
            oldItem: FileResponseModelItem,
            newItem: FileResponseModelItem
        ): Boolean {

            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = differ.currentList[position]
        holder.bind(data)



        holder.itemView.btn_download.setOnClickListener {
            onItemClickListener?.let { it(data, holder.itemView) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                (payloads.first() as Bundle).getInt("progress").also {

                    val isVisible = it < 99

                    progressBar.progress = it
                    progressBar.isVisible= isVisible

                    btn_download.isVisible = false
                    txt_progress.isVisible = it < 99
                    txt_progress.text = "$it %"

                    img_download_finished.isVisible = it == 100
                }
            }
        }
    }


     class ViewHolder(var binding:ItemFileBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(fileResponseModelItem: FileResponseModelItem){
            binding.file = fileResponseModelItem
            binding.executePendingBindings()


            binding.btnDownload.isVisible = !fileResponseModelItem.isFileExist
            binding.progressBar.isVisible = fileResponseModelItem.isDownloading
            binding.txtProgress.isVisible = fileResponseModelItem.isDownloading
            binding.imgDownloadFinished.isVisible = fileResponseModelItem.isFileExist




        }

        companion object{
            fun from(parent : ViewGroup):ViewHolder{

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFileBinding.inflate(layoutInflater  , parent , false)
                return ViewHolder(binding)

            }
        }
    }


    fun setDownloading(file: FileResponseModelItem, isDownloading: Boolean , isFileExist:Boolean) {
        file.isDownloading = isDownloading
        file.isFileExist = isFileExist
        notifyItemChanged(differ.currentList.indexOf(file))
    }

    fun setProgress(file: FileResponseModelItem, progress: Int) {
        file.progress = progress
        notifyItemChanged(differ.currentList.indexOf(file), Bundle().apply { putInt("progress", progress) })
    }

    private var onItemClickListener: ((FileResponseModelItem, View) -> Unit)? = null

    fun setOnItemClickListener(listener: (FileResponseModelItem, View) -> Unit) {
        onItemClickListener = listener
    }
}