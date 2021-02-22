package com.ram.delivery.view.tutorial

import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ram.delivery.MainActivity
import com.ram.delivery.R
import com.ram.delivery.base.BaseActivity
import com.ram.delivery.databinding.ActTutorialBinding
import com.ram.delivery.model.api.res.ResTutorial
import com.ram.delivery.utils.BackPressCloseHandler
import com.ram.delivery.view.tutorial.termspopup.PopupFullActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.act_tutorial.*
import org.jetbrains.anko.startActivity
import java.util.*

@AndroidEntryPoint
class TutorialActivity : BaseActivity<ActTutorialBinding>(R.layout.act_tutorial) {

    val viewModel: TutorialViewModel by viewModels()

    private val backPressCloseHandler by lazy { BackPressCloseHandler(this) }

    lateinit var tutorialList: ArrayList<ResTutorial>
    private lateinit var mRecyclerAdapter: TutorialTermsAdapter

    private val context by lazy { this }

    override fun ActTutorialBinding.initVIew() {

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        tutorialList = intent.getParcelableArrayListExtra("Tutorials")!!

        val adapter = TutorialPagerAdapter()
        tutorialPager.adapter = adapter
        adapter.setDiffNewData(tutorialList)

        tutorialPager.offscreenPageLimit = 4
        tutorialPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.isBottomVisibility.value = position == tutorialList.size - 1
                handlePagination(position)
            }
        })

        setupPagination(tutorialList.count())

        initDataBinding()
        observe()
    }

    private fun ActTutorialBinding.handlePagination(position: Int) {
        with(grTutorialPagination) {
            check(getChildAt(position).id)
        }

    }

    private fun ActTutorialBinding.setupPagination(count: Int) {
        fun createRadioButton(): RadioButton =
            RadioButton(context).apply {
                buttonDrawable =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.all_pagination_item_selector
                    )
                isEnabled = false
            }

        with(grTutorialPagination) {
            for (x in 0 until count) addView(createRadioButton())
            check(getChildAt(0).id)
        }
    }

    private fun ActTutorialBinding.initDataBinding() {
//        viewModel = this@TutorialActivity.viewModel
        viewModel.tutorialList = tutorialList

        mRecyclerAdapter = TutorialTermsAdapter(viewModel)
        recyclerView.apply {
            adapter = mRecyclerAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }
        subscribeData()
    }

    private fun observe() {
        with(viewModel) {
            adapterEvent.observe(this@TutorialActivity, {
                mRecyclerAdapter.notifyDataSetChanged()
            })
            termsViewEvent.observe(this@TutorialActivity, {
                startActivity<PopupFullActivity>("title" to it.casNm, "contents" to it.casDesc)
            })
            startEvent.observe(this@TutorialActivity, {
                finish()
                startActivity<MainActivity>()
            })
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE")
        super.onSaveInstanceState(outState)
    }

    private fun subscribeData() {
        viewModel.listMutableLiveData.removeObservers(this)
        viewModel.listMutableLiveData.observe(this, {
            if (it != null)
                viewModel.updateData(it)
        })
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}