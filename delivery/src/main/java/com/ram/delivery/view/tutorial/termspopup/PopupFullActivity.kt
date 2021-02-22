package com.ram.delivery.view.tutorial.termspopup

import com.ram.delivery.R
import com.ram.delivery.base.BaseActivity
import com.ram.delivery.databinding.ActPopupFullBinding

class PopupFullActivity : BaseActivity<ActPopupFullBinding>(R.layout.act_popup_full) {

    override fun ActPopupFullBinding.initVIew() {
        toolbarTitle.text = intent.getStringExtra("title")
        message.text = intent.getStringExtra("contents")
        titleButtonLeft.setOnClickListener {
            finish()
        }
    }
}