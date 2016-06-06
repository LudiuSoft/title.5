package org.splitscreen.t5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by IPat (Local) on 28.04.2016.
 */
public class SplashScreen extends Activity {
    private final static int SPLASH_TIMER = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, NavDrawerActivity.class));
                finish();
            }
        }, SPLASH_TIMER);

        Title5.prepareRand();
    }
}
