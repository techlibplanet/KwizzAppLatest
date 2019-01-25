package kwizzapp.com.kwizzapp.helper

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("android:src")
fun setImageUrl(view: ImageView, url: String) {
    Glide.with(view.context).load(url).into(view)
}

@BindingAdapter("android:src")
fun setImageResource(view: ImageView, src: Int) {
    Glide.with(view.context).load(src).into(view)
}
