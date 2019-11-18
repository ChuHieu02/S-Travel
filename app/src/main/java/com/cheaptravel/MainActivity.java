package com.cheaptravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
    private static final int RC_STORAGE_PERMS1 = 101;
    private static final int RC_STORAGE_PERMS2 = 102;
    private int hasWriteExtStorePMS;

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
            case RC_STORAGE_PERMS2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == RC_STORAGE_PERMS1) {
                    } else {
//                        startActivity(new Intent(this, DownloadActivity.class));
                    }
                    return;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Bạn cần cấp quyền để sử dụng ứng dụng");
                alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, requestCode);
                    }
                });
                alert.setCancelable(false);
                alert.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
            case RC_STORAGE_PERMS2:
                hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == RC_STORAGE_PERMS1) {
                    } else {
//                        startActivity(new Intent(this, DownloadActivity.class));
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS2);
                }
                break;
        }
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS1);
        }


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
