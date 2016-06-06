package org.splitscreen.t5;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.splitscreen.t5.camera.RecordingActivityC1;
import org.splitscreen.t5.util.OrientationManager;

import java.io.IOException;

/**
 * Created by IPat (Local) on 11.05.2016.
 */
public class RecordingActivity extends RecordingActivityC1 {

    private String videoTitle;
    private float titleOriginalY;

    private ImageView recordingButtonProgress;
    private ImageView recordingButton;
    private ImageButton backArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavDrawerActivity.setMainContent(new SpinFragment());
            }
        }, 100);

        videoTitle = getIntent().getStringExtra("title");
        if (videoTitle == null || videoTitle.isEmpty()) videoTitle="Video Title Lost";  // Only while debugging
        ((TextView) findViewById(R.id.video_title)).setText(videoTitle);

        titleOriginalY = findViewById(R.id.video_title).getY();

        recordingButtonProgress = (ImageView) findViewById(R.id.recording_progress_circle);
        recordingButton = (ImageView) findViewById(R.id.recording_button);
        backArrow = (ImageButton) findViewById(R.id.toolbar_back_arrow);

        addListeners();
    }

    public void addListeners() {
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isRecording())
                        stopVideo();
                    else
                        captureVideo();
                } catch (IOException e) {
                    throw new RuntimeException("Error at saving video.", e);
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideo();
            }
        });
    }

    @Override
    public void onRelevantOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        super.onRelevantOrientationChange(screenOrientation);

        switch (screenOrientation) {
            case LANDSCAPE:
                break;
            case REVERSED_LANDSCAPE:
                break;
            default:
                throw new RuntimeException("Nonrelevant screen orientation in onRelevantOrientationChange()");
        }
    }

    private class RecordingProgressTask extends AsyncTask<Void, Integer, Void> {

        int length;
        int maxLength;
        int videoTime;
        int maxTime;

        @Override
        protected void onPreExecute() {
            length = recordingButtonProgress.getWidth();
            maxLength = recordingButton.getWidth();
            maxTime = 30 * 1000;

            videoTime = ((int)((double)length / maxLength)*maxTime);
        }

        @Override
        protected Void doInBackground(Void... params) {
            long systemTime = System.currentTimeMillis();

            while(isRecording()) {
                long passedTime = System.currentTimeMillis() - systemTime;
                systemTime = System.currentTimeMillis();
                videoTime += passedTime;
                publishProgress(videoTime);
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            int videoProgress = progress[0];
            int buttonLength = ((int)((double)videoProgress / maxTime) * maxLength);
            ViewGroup.LayoutParams recordingButtonLayoutParams = recordingButton.getLayoutParams();
            recordingButtonLayoutParams.width = buttonLength;
            recordingButtonLayoutParams.height = buttonLength;
            recordingButton.setLayoutParams(recordingButtonLayoutParams);
        }
    }

    @Override
    public boolean onCapture() {
        if (recordingButton.getWidth() == recordingButtonProgress.getWidth()) return false;
        recordingButton.setImageResource(R.drawable.recording_button_on);
        new RecordingProgressTask().execute();
        return true;
    }

    @Override
    public void onStopVideo() {
        recordingButton.setImageResource(R.drawable.recording_button_off);
    }
}
