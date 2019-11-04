package com.cheaptravel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cheaptravel.activity.LoginActivity;
import com.cheaptravel.adapter.ScreenSlidePagerAdapter;
import com.cheaptravel.fragment.FragmentHome;
import com.cheaptravel.fragment.FragmentPlace;
import com.cheaptravel.fragment.FragmentUser;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentUser.FragmentUserLogout{
    private FragmentHome fragmentHome;
    private FragmentPlace fragmentPlace;
    private FragmentUser fragmentUser;
    private long backPresstime;
    private Toast backToast;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentHome = new FragmentHome();
        this.fragmentPlace = new FragmentPlace();
        this.fragmentUser = new FragmentUser();

        List<Fragment> dataFragment = new ArrayList<>();
        dataFragment.add(fragmentHome);
        dataFragment.add(fragmentPlace);
        dataFragment.add(fragmentUser);

        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setTypeface(Typeface.createFromAsset(getAssets(), "rubik.ttf"));

        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), dataFragment);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });

    }
    @Override
    public void onBackPressed() {

        if (backPresstime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, getString(R.string.Press_back_to_exit), Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPresstime = System.currentTimeMillis();
    }

    @Override
    public void logOut() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
