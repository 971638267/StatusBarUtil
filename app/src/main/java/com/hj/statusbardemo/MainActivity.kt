package com.hj.statusbardemo

import android.app.FragmentManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //StatusBarUtil.setStatusBar(this, ContextCompat.getColor(this!!, R.color.colorAccent)/*Color.TRANSPARENT*/, ContextCompat.getColor(this!!, R.color.ffffff), true, true,false);

        //获取到FragmentManager，在V4包中通过getSupportFragmentManager，

        //2.开启一个事务，通过调用beginTransaction方法开启。
        var mfragmentTransactions = supportFragmentManager.beginTransaction() as FragmentTransaction
        //把自己创建好的fragment创建一个对象
        var f2 = Fragment02();
        //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
        mfragmentTransactions?.add(R.id.fragment_root, f2);
        //提交事务，调用commit方法提交。
        mfragmentTransactions.commit();

    }

}
