package elnahas.com.nagwatask.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import elnahas.com.nagwatask.R

class BindingAdapters {
    companion object {


        @BindingAdapter("android:imageType")
        @JvmStatic
        fun parsePriorityColor(imageView: ImageView, type: String) {

            when (type) {
                "PDF" -> {
                    imageView.setImageResource(R.drawable.ic_file_pdf)
                }
                "VIDEO" -> {
                    imageView.setImageResource(R.drawable.ic_video)
                }

            }
        }

    }

}