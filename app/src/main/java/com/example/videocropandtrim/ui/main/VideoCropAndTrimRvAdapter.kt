package com.example.videocropandtrim.ui.main

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager
import com.example.videocropandtrim.R
import com.example.videocropandtrim.base.BaseRecyclerViewAdapter
import com.example.videocropandtrim.databinding.ItemVideoCropAndTrimVideoFrameBinding
import com.example.videocropandtrim.databinding.ItemVideoCropAndTrimVideoListBinding
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.utils.logg
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoCropAndTrimRvAdapter(
    vm: VideoCropAndTrimViewModel?,
    items: List<MediaFile>,
    val glide: RequestManager
) : BaseRecyclerViewAdapter<
        MediaFile,
        VideoCropAndTrimViewModel,
        ItemVideoCropAndTrimVideoListBinding>(
    R.layout.item_video_crop_and_trim_video_list,
    items = items,
    vm = vm
){

    override fun getItemIdR(position: Int): Long {
        return items?.get(position)?.createTime?.millis ?: -1
    }

    override fun bindItem(
        bindingView: ItemVideoCropAndTrimVideoListBinding,
        position: Int,
        item: MediaFile,
        viewType: Int,
        payloads: MutableList<Any>?
    ) {
        logg("bindItem item.dataURI: ${item.dataURI}")
        if(payloads.isNullOrEmpty()){
            bindingView.mediaFile = item
            item.dataURI?.let { dataUri ->
                glide
                    .load(Uri.parse(dataUri))
                    .into(binding.ivVideoCropAndTrimThumbnail)
            }
            mRecyclerViewItemClickListener = { _, _ ->
                logg("bindItem setOnClickListener: $this")
                vm?.selectedVideo(this)
            }
//            binding.clVideoListItem.setOnClickListener {
//                logg("bindItem setOnClickListener: $item")
//                vm?.selectedVideo(item)
//            }
            return
        }

        logg("bindItem payloads: $payloads")

    }

    class MyArchiveRvDiffUtil(private val oldList: List<MediaFile>, private val currentList: List<MediaFile>) : DiffUtil.Callback() {

        companion object{
            const val  PROJECT_TITLE_CHANGE = "isProjectTitleChange"
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition]._id == currentList[newItemPosition]._id
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].dataURI == currentList[newItemPosition].dataURI
                    && oldList[oldItemPosition].createTime == currentList[newItemPosition].createTime
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = currentList.size

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return null
        }
    }

    fun updateDiffItems(newItemList: List<MediaFile>) {
        items?.let { items ->
            if( itemCount < 1) {
                updateItems(newItemList)
                return
            }
//            disposables?.let { dispose ->
                Observable.create<DiffUtil.DiffResult> { it.onNext(
                    DiffUtil.calculateDiff(
                        MyArchiveRvDiffUtil(items, newItemList)
                    )) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({ diffResult ->
                        this.items = newItemList
                        diffResult.dispatchUpdatesTo(this)
                    },{
                        it.printStackTrace()
                        logg("DiffUtil.DiffResult error: $it")
                    })
//            }
        }
    }

}