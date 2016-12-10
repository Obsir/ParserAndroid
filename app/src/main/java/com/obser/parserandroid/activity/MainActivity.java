package com.obser.parserandroid.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.obser.parserandroid.R;
import com.obser.parserandroid.fragment.DrawFragment;
import com.obser.parserandroid.fragment.ParseFragment;
import com.obser.parserandroid.fragment.ScannerFragment;
import com.obser.parserandroid.interpreter.Parser;
import com.obser.parserandroid.interpreter.Scanner;
import com.obser.parserandroid.utils.FileUtils;
import com.obser.parserandroid.view.MyViewPager;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.menu)
    private FloatingActionMenu faMenu;
    @ViewInject(R.id.bt_scanner)
    private FloatingActionButton btScanner;
    @ViewInject(R.id.bt_parser)
    private FloatingActionButton btParser;
    @ViewInject(R.id.bt_draw)
    private FloatingActionButton btDraw;
    @ViewInject(R.id.bt_dot)
    private FloatingActionButton btDot;
    @ViewInject(R.id.dl)
    private DrawerLayout mDrawerLayout;
    @ViewInject(R.id.vp)
    private MyViewPager mViewPager;
    private ScannerFragment scannerFragment;

    @ViewInject(R.id.pager_title_strip)
    private PagerTabStrip mPagerTabStrip;
    @ViewInject(R.id.toolbar)
    private Toolbar toolBar;
    @ViewInject(R.id.bt_save)
    private FloatingActionButton btSave;
    @ViewInject(R.id.bt_clear)
    private FloatingActionButton btClear;
    @ViewInject(R.id.linear_fab)
    private FloatingActionMenu linearFab;
    @ViewInject(R.id.et)
    private EditText editText;
    private ActionBarDrawerToggle drawerToggle;
    private ParseFragment parseFragment;
    private DrawFragment drawFragment;
    private String[] tab_names;
    private Parser parser;
    private Scanner scanner;

    public FloatingActionButton getBtParser() {
        return btParser;
    }

    public FloatingActionButton getBtDraw() {
        return btDraw;
    }

    public FloatingActionButton getBtDot(){
        return btDot;
    }

    public FloatingActionButton getBtScanner() {
        return btScanner;
    }

    public FloatingActionMenu getFaMenu() {
        return faMenu;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        tab_names = new String[]{"词法", "语法", "运行"};
        scanner = new Scanner();
        parser = new Parser(scanner);
        parseFragment = new ParseFragment();
        parseFragment.setParser(parser);

        drawFragment = new DrawFragment();
        drawFragment.setmParser(parser);

        scannerFragment = new ScannerFragment();
        scannerFragment.setScanner(scanner);
    }

    protected void initView(){
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPagerTabStrip.setTabIndicatorColorResource(R.color.indicatorcolor);

        mViewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);    //增加viewPager的缓存数量
        mViewPager.setCurrentItem(1);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                faMenu.showMenu(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                faMenu.hideMenu(true);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.saveLocal(editText.getText().toString());
                linearFab.close(true);
            }
        });
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                linearFab.close(true);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private class MainAdapter extends FragmentPagerAdapter{

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return scannerFragment;
            else if(position == 1)
                return parseFragment;
            else if(position == 2)
                return drawFragment;
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_names[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
