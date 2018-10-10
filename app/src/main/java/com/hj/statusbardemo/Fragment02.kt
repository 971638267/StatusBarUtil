package com.hj.statusbardemo

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hj.statusbardemo.base.util.StatusBarUtil

class Fragment02 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //引用创建好的xml布局
        var view = inflater.inflate(R.layout.fragment01, container, false);
        StatusBarUtil.setStatusBar(activity!!, ContextCompat.getColor(activity!!, R.color.ffffff)/*Color.TRANSPARENT*/, Color.RED/*ContextCompat.getColor(activity!!, R.color.colorPrimary)*/, true, false, true);
        return view;
    }

}
