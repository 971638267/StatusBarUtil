package com.hj.statusbardemo.base;

import android.os.Bundle;

import com.hj.fragmention.SupportActivity;

public class BaseActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getContainId() {
        return 0;
    }

}
