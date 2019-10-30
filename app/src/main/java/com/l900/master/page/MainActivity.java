package com.l900.master.page;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.l900.master.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tv_item_one;
    private TextView tv_item_two;
    private TextView tv_item_three;
    private ViewPager myViewPager;
    private List<Fragment> list;
    private TabFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_main);
        InitView();

// 设置菜单栏的点击事件
        tv_item_one.setOnClickListener(this);
        tv_item_two.setOnClickListener(this);
        tv_item_three.setOnClickListener(this);
        myViewPager.addOnPageChangeListener(new MyPagerChangeListener());

//把Fragment添加到List集合里面
        list = new ArrayList<>();
        list.add(new OneFragment());
        list.add(new TwoFragment());
        list.add(new ThreeFragment());
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
        myViewPager.setAdapter(adapter);
        myViewPager.setCurrentItem(0);  //初始化显示第一个页面
        tv_item_one.setBackgroundColor(Color.RED);//被选中就为红色
    }


    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        tv_item_one =  findViewById(R.id.tv_item_one);
        tv_item_two = findViewById(R.id.tv_item_two);
        tv_item_three = findViewById(R.id.tv_item_three);
        myViewPager =  findViewById(R.id.myViewPager);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_item_one:
                myViewPager.setCurrentItem(0);
                tv_item_one.setBackgroundColor(Color.RED);
                tv_item_two.setBackgroundColor(Color.WHITE);
                tv_item_three.setBackgroundColor(Color.WHITE);
                break;
            case R.id.tv_item_two:
                myViewPager.setCurrentItem(1);
                tv_item_one.setBackgroundColor(Color.WHITE);
                tv_item_two.setBackgroundColor(Color.RED);
                tv_item_three.setBackgroundColor(Color.WHITE);
                break;
            case R.id.tv_item_three:
                myViewPager.setCurrentItem(2);
                tv_item_one.setBackgroundColor(Color.WHITE);
                tv_item_two.setBackgroundColor(Color.WHITE);
                tv_item_three.setBackgroundColor(Color.RED);
                break;
        }
    }

    /**
     * ViewPager的事件侦听
     *
     */
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.e("1900","onPageScrollStateChanged arg0:"+arg0   );
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.e("1900","onPageScrolled arg0:"+arg0 +" arg1:"+arg1 +" arg2:"+arg2 );
        }

        @Override
        public void onPageSelected(int arg0) {
            Log.e("1900","onPageSelected arg0:"+arg0   );
            switch (arg0) {
                case 0:
                    tv_item_one.setBackgroundColor(Color.RED);
                    tv_item_two.setBackgroundColor(Color.WHITE);
                    tv_item_three.setBackgroundColor(Color.WHITE);
                    break;
                case 1:
                    tv_item_one.setBackgroundColor(Color.WHITE);
                    tv_item_two.setBackgroundColor(Color.RED);
                    tv_item_three.setBackgroundColor(Color.WHITE);
                    break;
                case 2:
                    tv_item_one.setBackgroundColor(Color.WHITE);
                    tv_item_two.setBackgroundColor(Color.WHITE);
                    tv_item_three.setBackgroundColor(Color.RED);
                    break;
            }
        }
    }

}