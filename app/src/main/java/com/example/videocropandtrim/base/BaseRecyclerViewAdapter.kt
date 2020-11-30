package com.example.videocropandtrim.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.videocropandtrim.utils.logg


abstract class BaseRecyclerViewAdapter<T, VM, VDB: ViewDataBinding>
    (val layoerId: Int,
     var items: List<T>? = emptyList(),
     val vm: VM? = null,
     private var headerResId: Int? = null,
     private var footerResId: Int? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    lateinit var binding: VDB

    private var headerNum: Int = if(headerResId == null) 0 else 1
    private var footerNum: Int = if(footerResId == null) 0 else 1

    protected val TYPE_HEADER = 0
    protected val TYPE_ITEM = 1
    protected val TYPE_FOOTER = 2

    private var isHeaderView: Boolean? = false
    private var isFooterView: Boolean? = false

    private var headerView: View? = null
    private var footerView: View? = null

    private var isHeaderViewShowing = headerResId != null
    private var isFooterViewShowing = footerResId != null

    interface RecyclerViewItemClickListener<T>{
        fun itemClick(view: View, position: Int, item: T)
    }

//    var mRecyclerViewItemClickListener: RecyclerViewItemClickListener<T>? = null

    var mRecyclerViewItemClickListener: (T.(view: View, position: Int) -> Unit)? = null

    fun isHeaderView(): Boolean = headerNum == 1
    fun isFooterView(): Boolean = footerNum == 1


    open fun updateItems(updateItems: List<T>){
        items = updateItems
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        items?.isNullOrEmpty().let { isNullOrEmpty ->
            return when(isNullOrEmpty){
                true -> {
                    when(position){
                        0 -> if(headerNum == 1) TYPE_HEADER else TYPE_FOOTER
                        else -> TYPE_FOOTER
                    }
                }

                else -> {
                    when(position){
                        0 -> if(headerNum == 1 ) TYPE_HEADER else TYPE_ITEM
                        (items!!.size + headerNum + footerNum -1) -> if(footerNum == 1) TYPE_FOOTER else TYPE_ITEM
                        else -> TYPE_ITEM
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoerId, parent, false)
        val rootView = when(viewType){
            TYPE_FOOTER -> {
                footerResId?.let {
                    footerView = LayoutInflater.from(parent.context).inflate(it, parent, false )
                    footerView
                } ?: binding.root
            }
            TYPE_HEADER -> {
                headerResId?.let {
                    headerView = LayoutInflater.from(parent.context).inflate(it, parent, false )
                    headerView
                } ?: binding.root
            }
            else -> binding.root
        }
        return BaseRecyclerViewHolder(rootView)
    }


    abstract fun bindItem(
        bindingView: VDB,
        position: Int,
        item: T,
        viewType: Int = TYPE_ITEM,
        payloads: MutableList<Any>?
    )
    protected open fun bindItemHeaderFooter(view: View, position: Int, viewType: Int){

    }

    protected open fun getItemIdR(position: Int): Long{
        return super.getItemId(position)
    }

    override fun getItemId(position: Int): Long {
        val changePosition = position - headerNum
        return when(getItemViewType(position)){
            TYPE_FOOTER -> -1
            TYPE_HEADER -> -2
            else -> getItemIdR(changePosition)
        }
    }

    fun changeHeaderView(headerResId: Int){
        this.headerResId = headerResId

        showHedaerView(true)
        notifyDataSetChanged()
    }

    fun changeFooterView(footerResId: Int){
        this.footerResId = footerResId

        showFooterView(true)
        notifyDataSetChanged()
    }

    fun showHedaerView(isShowHeader: Boolean){
        val isShowingValue = if(isShowHeader) 1 else 0
        if(headerNum == isShowingValue) return

        headerNum = isShowingValue
        notifyDataSetChanged()
    }
    fun showFooterView(isShowFooter: Boolean){
        val isShowingValue = if(isShowFooter) 1 else 0
        if(footerNum == isShowingValue) return

        footerNum = isShowingValue
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items?.size?.plus(headerNum)?.plus(footerNum) ?:0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(holder.itemViewType){
//            TYPE_ITEM -> {
//                items?.let {
//                    val changePosition = position - headerNum
//                    if(changePosition in it.indices){
//                        DataBindingUtil.bind<VDB>(holder.itemView)?.let { viewBinding -> bindItem(viewBinding, changePosition , it[changePosition]) }
//                        holder.itemView.setOnClickListener { _ -> mRecyclerViewItemClickListener?.itemClick(holder.itemView, changePosition, it[changePosition]) }
//                    }
//                }
//            }
//            else -> {
//                bindItemHeaderFooter(holder.itemView, position, holder.itemViewType)
//            }
//        }

        onBindViewHolderR(holder, position)
    }

    override fun onBindViewHolder( holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolderR(holder, position, payloads)
    }

    private fun onBindViewHolderR(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>? = null){

        when(holder.itemViewType){
            TYPE_ITEM -> {
                items?.let {
                    val changePosition = position - headerNum
                    if(changePosition in it.indices){
                        DataBindingUtil.bind<VDB>(holder.itemView)?.let { viewBinding -> bindItem(viewBinding, changePosition, it[changePosition], payloads = payloads ) }

//                        holder.itemView.setOnClickListener { _ -> mRecyclerViewItemClickListener?.itemClick(holder.itemView, changePosition, it[changePosition]) }
                        holder.itemView.setOnClickListener { _ -> mRecyclerViewItemClickListener?.invoke(it[changePosition], holder.itemView, changePosition) }
                    }
                }
            }
            else -> {
                bindItemHeaderFooter(holder.itemView, position, holder.itemViewType)
            }
        }
    }

    fun loadMoreItem(){
        logg("${this::class.java.simpleName} loadMoreItem")
    }
}

open class BaseRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


class EndlessRecyclerViewScrollListener(
    private val mLayoutManager: RecyclerView.LayoutManager,
    private val function: (page: Int, totalItemCount: Int, recyclerView: RecyclerView) -> Unit
) : RecyclerView.OnScrollListener(){

    private var visibleThreshold = 5
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private var startingPageIndex = 0

    init {
        when(mLayoutManager){
            is GridLayoutManager -> visibleThreshold *= mLayoutManager.spanCount
            is StaggeredGridLayoutManager -> visibleThreshold *= mLayoutManager.spanCount
        }
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int{
        var maxSize = 0

        lastVisibleItemPositions.forEachIndexed { index, value ->
            if(index == 0) maxSize = value
            else if(value > maxSize) maxSize = value
        }

        return maxSize
    }



    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var totalItemCount = mLayoutManager.itemCount

        var lastVisibleItemPosition = when(mLayoutManager){
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null)
                getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager ->   mLayoutManager.findLastVisibleItemPosition()
            is LinearLayoutManager -> mLayoutManager.findLastVisibleItemPosition()
            else -> 0
        }

        if(totalItemCount < previousTotalItemCount){
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if(totalItemCount == 0) this.loading = true
        }

        if(loading && (totalItemCount > previousTotalItemCount)){
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if(!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount){
            currentPage ++
            function(currentPage, totalItemCount, recyclerView)
//            onLoadMore(currentPage, totalItemCount, recyclerView)
            loading = true
        }
    }

//    abstract fun onLoadMore(page: Int, totalItemCount: Int, recyclerView: RecyclerView)

    fun resetState(){
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }
}