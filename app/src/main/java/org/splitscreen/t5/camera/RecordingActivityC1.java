package org.splitscreen.t5.camera;

import android.app.Activity;

import android.hardware.Camera;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.util.Matrix;

import org.splitscreen.t5.R;
import org.splitscreen.t5.Title5;
import org.splitscreen.t5.camera.util.AutoCameraFitter;
import org.splitscreen.t5.util.OrientationManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
public abstract class RecordingActivityC1 extends Activity implements SurfaceHolder.Callback, OrientationManager.OrientationListener {
    private OrientationManager om;

    private MediaRecorder recorder;

    private Camera camera;
    private SurfaceHolder surfaceHolder;

    private boolean recording;
    private File lastVideo;

    private OrientationManager.ScreenOrientation currentOrientation = OrientationManager.ScreenOrientation.LANDSCAPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        om = new OrientationManager(getApplicationContext(), this);
        om.enable();

        surfaceHolder = ((SurfaceView) findViewById(R.id.camera_preview)).getHolder();

        surfaceHolder.addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        om.disable();
    }

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {

        if (currentOrientation==screenOrientation) return;

        switch (screenOrientation) {
            case LANDSCAPE:
                Log.d(Title5.LOG_TAG, "onOrientationChange_LANDSCAPE");
                break;
            case REVERSED_LANDSCAPE:
                Log.d(Title5.LOG_TAG, "onOrientationChange_REVERSED_LANDSCAPE");
                break;
            default:
                return;
        }
        onRelevantOrientationChange(screenOrientation);
    }

    public void onRelevantOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        if (recording) stopVideo();
        currentOrientation = screenOrientation;
    }

    public abstract boolean onCapture();

    public void captureVideo() throws IOException {
        Log.d(Title5.LOG_TAG, "captureVideo()");
        if (recording) return;
        if (!onCapture()) return;
        recording = true;
        camera.unlock();
        recorder = new MediaRecorder();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        lastVideo = getOutputMediaFile();
        recorder.setOutputFile(lastVideo.toString());
        recorder.setPreviewDisplay(surfaceHolder.getSurface());
        recorder.prepare();
        recorder.start();
    }

    public abstract void onStopVideo();

    public void stopVideo() {
        Log.d(Title5.LOG_TAG, "stopVideo()");
        if (!recording) return;
        onStopVideo();
        recording = false;
        recorder.stop();
        recorder.release();
        camera.lock();

        if (currentOrientation == OrientationManager.ScreenOrientation.REVERSED_LANDSCAPE)
        try {
            IsoFile isoFile = new IsoFile(lastVideo.getAbsolutePath());
            Movie m = new Movie();
            List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);

            for (TrackBox trackBox : trackBoxes) {
                trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_180);
                m.addTrack(new Mp4TrackImpl("va1", trackBox));
            }

            Container out = new DefaultMp4Builder().build(m);
            File videoToDelete = lastVideo;
            lastVideo = getOutputMediaFile();
            FileChannel fc = new RandomAccessFile(lastVideo.getAbsolutePath(), "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
            videoToDelete.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean isRecording() {return recording;}

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "Title.5");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Title.5", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4");

        return mediaFile;
    }

    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        camera.stopPreview();
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public void toggleCameraFacingSide() {
        camera.stopPreview();
        camera.release();
        currentCameraId =
                currentCameraId==Camera.CameraInfo.CAMERA_FACING_BACK
                ?Camera.CameraInfo.CAMERA_FACING_FRONT
                :Camera.CameraInfo.CAMERA_FACING_BACK;
        camera = Camera.open(currentCameraId);
        camera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(currentCameraId);
        }

        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        // DEPRECATED due to the fixed orientation in the AndroidManifest.xml
        /*
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result); */

        camera.setParameters(AutoCameraFitter.setSurfaceHolderFittingSize(this, holder, camera.getParameters()));

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}