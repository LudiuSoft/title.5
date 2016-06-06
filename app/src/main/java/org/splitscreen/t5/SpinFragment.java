package org.splitscreen.t5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.splitscreen.t5.camera.RecordingActivityC1;

/**
 * Created by IPat (Local) on 22.04.2016.
 */
public class SpinFragment extends Fragment {

    private static View frame;

    private String videoTitleString;
    private Button button;

    private boolean runningAnimation = false;

    private View upperTitleLine;
    private View lowerTitleLine;
    private float upperLineY;
    private float lowerLineY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SpinFragment.frame = view;

        button = (Button) findViewById(R.id.spin_button);
        upperTitleLine = findViewById(R.id.upper_title_line);
        lowerTitleLine = findViewById(R.id.lower_title_line);

        button.setOnClickListener(new SpinButtonListener());
        findViewById(R.id.video_title_container).setOnClickListener(new SpinButtonListener());

        findViewById(R.id.toolbar_nav_drawer_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
            }
        });
        findViewById(R.id.toolbar_fragment_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!runningAnimation)
                    button.callOnClick();
                else {
                    if (videoTitleString==null)
                        videoTitleString = getRandomVideoTitle();
                    startRecordingActivity(videoTitleString);
                }
            }
        });
    }

    private class SpinButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (runningAnimation) return;
            onAnimationStart();
        }

        private void onAnimationStart() {
            runningAnimation = true;
            upperLineY = upperTitleLine.getY();
            lowerLineY = lowerTitleLine.getY();

            float middle = (upperLineY + lowerLineY) / 2;

            upperTitleLine.animate().y(middle - (upperTitleLine.getHeight() / 2)).withLayer();
            lowerTitleLine.animate().y(middle + (lowerTitleLine.getHeight() / 2)).withLayer().withEndAction(new Runnable() {
                @Override
                public void run() {
                    onAnimationMiddle();
                }
            });
        }

        private void onAnimationMiddle() {
            TextView videoTitle = (TextView) findViewById(R.id.video_title);

            videoTitleString = getRandomVideoTitle();
            videoTitle.setText(videoTitleString);

            upperTitleLine.animate().y(upperLineY).withLayer();
            lowerTitleLine.animate().y(lowerLineY).withLayer()
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            onAnimationEnd();
                        }
                    });
        }

        private void onAnimationEnd() {
            runningAnimation = false;
            button.setText(R.string.spin_button_film);
            button.setOnClickListener(new FilmButtonListener());
        }
    }

    private class FilmButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            upperTitleLine.animate().cancel();
            lowerTitleLine.animate().cancel();

            startRecordingActivity(videoTitleString);
        }
    }

    private void startRecordingActivity(String spinTitle) {
        startActivity(new Intent(getActivity(), RecordingActivity.class).putExtra("title", spinTitle));
    }

    private String getRandomVideoTitle() {
        int index = (int) (Title5.random.nextFloat() * Title5.titleList.size());
        return Title5.titleList.get(index);
    }

    private View findViewById(@IdRes int id) {
        return frame.findViewById(id);
    }
}