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

import com.fuentesfernandez.dropsy.Fragment.CreditsFragment;
import com.fuentesfernandez.dropsy.Fragment.ServerInfoFragment;
import com.fuentesfernandez.dropsy.Fragment.StreamFragment;
import com.fuentesfernandez.dropsy.R;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.glassfish.grizzly.streams.Stream;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, ServerInfoFragment.OnFragmentInteractionListener {

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
                        .setResId(R.drawable.ic_play_arrow_white_24dp)
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(getBaseContext(), 4)))
                        .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("Cargar proyecto")
                        .setResId(R.drawable.ic_file_upload_white_24dp)
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

        Fragment fragment;
        fragment = new ServerInfoFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        Intent i;
        switch ((Integer)item.getWrapper()){
            case 0:
                i = new Intent(getApplicationContext(), ProjectLoadActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getApplicationContext(), ProjectActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        Intent i;
        switch ((Integer)item.getWrapper()){
            case 0:
                i = new Intent(getApplicationContext(), ProjectLoadActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getApplicationContext(), ProjectActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (id){
            case R.id.nav_robot_view:
                rfaBtn.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction().replace(R.id.flContent, new StreamFragment()).commit();
                break;
            case R.id.nav_server_info:
                rfaBtn.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().replace(R.id.flContent, new ServerInfoFragment()).commit();
                break;
            case R.id.nav_saved_projects:
                Intent i = new Intent(getApplicationContext(), ProjectLoadActivity.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                Intent s = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(s);
                break;
            case R.id.nav_help:
                rfaBtn.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().replace(R.id.flContent, new ServerInfoFragment()).commit();
                break;
            case R.id.nav_credits:
                rfaBtn.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().replace(R.id.flContent, new CreditsFragment()).commit();
                break;
            default:
        }

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
