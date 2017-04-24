package com.display.solution.screenshare;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.video.VideoQuality;

public class ScreenCaptureService extends Service implements Session.Callback {

    private static final String TAG = "ScreenCaptureService";

    private VirtualDisplay virtualDisplay;

    public static final int WIDTH = 1080 / 4;
    public static final int HEIGHT = 1920 / 4;

    static int deviceWidth;
    static int deviceHeight;
    Point resolution = new Point();
    final float resolutionRatio = 0.25f;

    private Session mSession;

    public ScreenCaptureService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        calculateScreenDimens();

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setPreviewOrientation(90)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .setDestination("localhost")
                .build();

        mSession.configure();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void calculateScreenDimens() {
        DisplayMetrics dm = new DisplayMetrics();
        Display mDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDisplay.getMetrics(dm);
        deviceWidth = dm.widthPixels;
        deviceHeight = dm.heightPixels;
        mDisplay.getRealSize(resolution);
        resolution.x = (int) (resolution.x * resolutionRatio);
        resolution.y = (int) (resolution.y * resolutionRatio);
    }

    @SuppressLint("NewApi")
    public void createVirtualDisplay() {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        virtualDisplay = MainActivity.mMediaProjection.createVirtualDisplay("Remote Droid",
                WIDTH, HEIGHT, 50,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSession.getSurface(), null, null);
    }

    @Override
    public void onBitrateUpdate(long bitrate) {

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {

    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG, "Session Configured");
        createVirtualDisplay();

        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }

    @Override
    public void onSessionStarted() {
    }

    @Override
    public void onSessionStopped() {

    }
}
