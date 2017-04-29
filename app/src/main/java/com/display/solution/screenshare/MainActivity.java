package com.display.solution.screenshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.majorkernelpanic.streaming.rtsp.RtspServer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;

    private static final int REQUEST_MEDIA_PROJECTION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaProjectionManager = (MediaProjectionManager)
                getSystemService(MEDIA_PROJECTION_SERVICE);

        // Sets the port of the RTSP server to 1234
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
        editor.commit();

        Log.d(TAG, "ip = " + Utils.getLocalIpAddress(this));
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "User cancelled the access", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            Intent startServerIntent = new Intent(MainActivity.this, ScreenCaptureService.class);
            startServerIntent.setAction("START");
            startService(startServerIntent);
        }
    }

    @SuppressLint("NewApi")
    private void startScreenCapture() {
        startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @SuppressLint("NewApi")
    public void start(View v) {
        startService(new Intent(this,RtspServer.class));
        startScreenCapture();
    }
}
