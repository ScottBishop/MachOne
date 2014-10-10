package com.zachary.sperske.machone;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private AudioManager myAudioManager;
    private int minVolume = 0;

    private static final float ABSOLUTE_MAX_SPEED = 8.94f;
    private float maxSpeed;

    private SeekBar volumeBar;
    private SeekBar maxSpeedBar;
    private Switch enabledSwitch;

    private TextView speedText;
    private TextView maxSpeedText;

    private boolean enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        maxSpeedBar = (SeekBar) findViewById(R.id.maxSpeedBar);

        enabledSwitch = (Switch) findViewById(R.id.enableSwitch);
        speedText = (TextView) findViewById(R.id.textView);
        maxSpeedText = (TextView) findViewById(R.id.maxSpeedText);

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        final int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percent = progress / 100.0f;

                minVolume = Math.round(percent * maxVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        maxSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percent = progress / 100.0f;

                maxSpeed = Math.round(percent * ABSOLUTE_MAX_SPEED);
                maxSpeedText.setText("Max speed is " + maxSpeed * 2.23f + " mph");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        enabledSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enabled = enabledSwitch.isChecked();
            }
        });

        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                float currentSpeed = location.getSpeed();

                float ratio = currentSpeed / maxSpeed;
                int volume = Math.round(ratio * maxVolume);

                if (volume < minVolume) {
                    volume = minVolume;
                }

                if (enabled) {
                    speedText.setText("Speed is " + currentSpeed * 2.23f + " mph");

                    myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                }
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                               0, locationListener);


    }

}
