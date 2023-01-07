package com.shadogr.mybattery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shadogr.mybattery.appusage.AppUsageFragment;
import com.shadogr.mybattery.databinding.MainActivityBinding;
import com.shadogr.mybattery.history.HistoryFragment;
import com.shadogr.mybattery.migrate.MigrateActivtiy;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.shadogr.mybattery.databinding.MainActivityBinding binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);
        toolBarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);
        bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id==R.id.page_1){
                viewPager.setCurrentItem(0);
                return true;
            }
            else if (id==R.id.page_2){
                viewPager.setCurrentItem(1);
                return true;
            }
            else if (id==R.id.page_3){
                viewPager.setCurrentItem(2);
                return true;
            }else return false;
        });
        FloatingActionButton fab = binding.fab;
        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.hide();
                        toolBarLayout.setTitle("Home");
                        bottomNavigationView.getMenu().findItem(R.id.page_1).setChecked(true);
                        break;
                    case 1:
                        fab.show();
                        toolBarLayout.setTitle("Charging Log");
                        bottomNavigationView.getMenu().findItem(R.id.page_2).setChecked(true);
                        break;
                    case 2:
                        fab.show();
                        toolBarLayout.setTitle("App Usage");
                        bottomNavigationView.getMenu().findItem(R.id.page_3).setChecked(true);
                        break;
                }
            }
        });
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedEdit = sharedPref.edit();
        sharedEdit.putLong("boot_time",System.currentTimeMillis());
        sharedEdit.apply();
        sharedEdit.clear();
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }
    @Override
    protected void onDestroy() {
        bottomNavigationView.setOnItemSelectedListener(null);
        viewPager.setAdapter(null);
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.menu_migrate) {
            startActivity(new Intent(this, MigrateActivtiy.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public @NotNull Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new LogFragment();
                case 1:
                    return new HistoryFragment();
                case 2:
                    return new AppUsageFragment();
            }

            return new LogFragment();

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

}