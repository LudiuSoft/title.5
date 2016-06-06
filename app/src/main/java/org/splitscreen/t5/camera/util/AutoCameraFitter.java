package org.splitscreen.t5.camera.util;

import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import org.splitscreen.t5.R;
import org.splitscreen.t5.Title5;

/**
 * Created by IPat (Local) on 15.05.2016.
 */

@SuppressWarnings("deprecation")
public class AutoCameraFitter {

    private static final String LOG_TAG = Title5.LOG_TAG;

    private AutoCameraFitter() {}

    public static Camera.Parameters setSurfaceHolderFittingSize(Activity activity, SurfaceHolder holder,
                                                   Camera.Parameters params) {
        Log.d(LOG_TAG, "---NEW SESSION---");
        int cameraWidth = params.getPreviewSize().width;
        int cameraHeight = params.getPreviewSize().height;

        int layoutWidth = activity.findViewById(R.id.record_general_layout).getWidth();
        int layoutHeight = activity.findViewById(R.id.record_general_layout).getHeight();

        double cameraRatio = (double)cameraHeight / cameraWidth;
        double layoutRatio = (double)layoutHeight / layoutWidth;

        Log.d(LOG_TAG, "cameraW: "+String.valueOf(cameraWidth));
        Log.d(LOG_TAG, "cameraH: "+String.valueOf(cameraHeight));
        Log.d(LOG_TAG, "cameraRatio: "+String.valueOf(cameraRatio));

        Log.d(LOG_TAG, "layoutW: "+String.valueOf(layoutWidth));
        Log.d(LOG_TAG, "layoutH: "+String.valueOf(layoutHeight));
        Log.d(LOG_TAG, "layoutRatio: "+String.valueOf(layoutRatio));

        if (cameraRatio>layoutRatio) {
            Log.d(LOG_TAG, "cameraRatio>layoutRatio");
            layoutHeight = (int)((double)layoutWidth*cameraRatio);
        } else if (cameraRatio<layoutRatio) {
            Log.d(LOG_TAG, "cameraRatio<layoutRatio");
            layoutWidth = (int)((double)layoutHeight/cameraRatio);
        }

        layoutRatio = (double)layoutHeight / layoutWidth;

        Log.d(LOG_TAG, "layoutW: "+String.valueOf(layoutWidth));
        Log.d(LOG_TAG, "layoutH: "+String.valueOf(layoutHeight));
        Log.d(LOG_TAG, "layoutRatio: "+String.valueOf(layoutRatio));

        activity.getWindow().setLayout(layoutWidth, layoutHeight);

        holder.setFixedSize(layoutWidth, layoutHeight);

        params.setPreviewSize(params.getSupportedPreviewSizes().get(0).width,
                params.getSupportedPreviewSizes().get(0).height);
        params.setRecordingHint(true);

        return params;
    }
}