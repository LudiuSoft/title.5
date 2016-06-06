package org.splitscreen.t5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

public class NavDrawerActivity extends FragmentActivity {

    private static FragmentManager fragmentManager;
    private void prepareStatic() { fragmentManager = getSupportFragmentManager(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        prepareStatic();

        getWindow().setBackgroundDrawable(null);

        Log.d(Title5.LOG_TAG, "onCreateActivity");

        if (!Title5.fetchTitleList()) throw new RuntimeException();

        setMainContent(new SpinFragment());
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    public static void setMainContent(Fragment newFragment) {
        if (fragmentManager==null)
            throw new RuntimeException("prepareStatic() hasn't been called");
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_content, newFragment)
                .commit();
    }

    public void onProfile(View view) {

    }

    public void onWatch(View view) {
        setMainContent(new SpinFragment());
    }

    public void onSettings(View view) {

    }

    public void onHelp(View view) {

    }
}