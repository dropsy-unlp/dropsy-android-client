package com.fuentesfernandez.dropsy.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Fragment.SavedProjectsFragment;
import com.fuentesfernandez.dropsy.Fragment.ServerInfoFragment;
import com.fuentesfernandez.dropsy.Fragment.dummy.DummyContent;
import com.fuentesfernandez.dropsy.R;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,SavedProjectsFragment.OnListFragmentInteractionListener, ServerInfoFragment.OnFragmentInteractionListener {

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.activity_main_rfal);
        rfaBtn = (RapidFloatingActionButton) findViewById(R.id.activity_main_rfab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getBaseContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("Nuevo proyecto")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(getBaseContext(), 4)))
                        .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("Cargar proyecto")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xffd84315)
                        .setIconPressedColor(0xffbf360c)
                        .setWrapper(0)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(getBaseContext(), 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(getBaseContext(), 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getBaseContext(),
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();

        Fragment fragment = new ServerInfoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        switch ((Integer)item.getWrapper()){
            case 0:
                try {
                    Fragment fragment = new SavedProjectsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                Intent i = new Intent(getApplicationContext(), ProjectActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        switch ((Integer)item.getWrapper()){
            case 0:
                try {
                    Fragment fragment = new SavedProjectsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                Intent i = new Intent(getApplicationContext(), ProjectActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        rfabHelper.toggleContent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass;

        int id = item.getItemId();

        switch (id){
            case R.id.nav_server_info:
                rfaBtn.setVisibility(View.VISIBLE);
                fragmentClass = ServerInfoFragment.class;
                break;
            case R.id.nav_saved_projects:
                rfaBtn.setVisibility(View.INVISIBLE);
                fragmentClass = SavedProjectsFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SavedProjectsFragment.class;
                break;
            case R.id.nav_help:
                fragmentClass = SavedProjectsFragment.class;
                break;
            case R.id.nav_credits:
                fragmentClass = SavedProjectsFragment.class;
                break;
            default:
                fragmentClass = SavedProjectsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
