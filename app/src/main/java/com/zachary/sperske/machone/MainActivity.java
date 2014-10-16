package com.zachary.sperske.machone;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final int WALKING = 0;
    public static final int RUNNING = 1;
    public static final int BIKING = 2;
    public static final int DRIVING = 3;

    public static final float WALKING_SPEED = 3.1f / 2.23f;
    public static final float RUNNING_SPEED = 9.0f / 2.23f;
    public static final float BIKING_SPEED = 20.0f / 2.23f;
    public static final float FREEWAY_MAX_SPEED = 80.0f / 2.23f;

    private AudioManager audioManager;
    private int minVolume = 0;

    private static final float ABSOLUTE_MAX_SPEED = 8.94f;
    private float maxSpeed;

    private SeekBar volumeBar;
    private SeekBar speedBar;
    private SeekBar movementTypeBar;

    private CheckBox checkBox;

    private TextView speedText;
    private TextView maxSpeedText;
    private TextView positionText;
    private TextView highestSpeedText;

    private float highestSpeed = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeWidgets();

        setUpListeners();
    }

    private void setUpListeners() {
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        minVolume = maxVolume / 3;
        volumeBar.setProgress(30);

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

        movementTypeBar.setMax(3);
        movementTypeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case WALKING:
                        maxSpeed = WALKING_SPEED;
                        break;
                    case RUNNING:
                        maxSpeed = RUNNING_SPEED;
                        break;
                    case BIKING:
                        maxSpeed = BIKING_SPEED;
                        break;
                    case DRIVING:
                        //TODO: Use accelerometer for boost of sound when gunning the engine
                        maxSpeed = FREEWAY_MAX_SPEED;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout customLayout = (LinearLayout)findViewById(R.id.customContainer);
                if (checkBox.isChecked()) {
                    customLayout.setVisibility(View.VISIBLE);
                    movementTypeBar.setEnabled(false);
                } else {
                    customLayout.setVisibility(View.GONE);
                    movementTypeBar.setEnabled(true);
                }
            }
        });



        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                float currentSpeed = location.getSpeed();

                if (currentSpeed > highestSpeed) {
                    highestSpeed = currentSpeed;
                }

                int volume;
                if (currentSpeed == 0.0f) {
                    volume = minVolume;
                } else {
                    float ratio = currentSpeed / maxSpeed;
                    volume = Math.round(ratio * maxVolume);
                }

                if (volume < minVolume) {
                    volume = minVolume;
                }

                if (currentSpeed > 0) {
                    speedText.setText("Your speed is " + currentSpeed * 2.23f + " mph");
                }

                if (!location.hasSpeed()) {
                    speedText.setText("Doesn't have speed");
                }

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                positionText.setText("Your position is \n"+ location.getLatitude() + "," + location.getLongitude());

                highestSpeedText.setText("Your fastest speed was " + highestSpeed / 2.23f + " mph");
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                                               0, locationListener);

        setUpImageListeners();
    }

    private void initializeWidgets() {
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        speedBar = (SeekBar) findViewById(R.id.maxSpeedBar);
        movementTypeBar = (SeekBar) findViewById(R.id.movementTypeBar);

        speedText = (TextView) findViewById(R.id.currentSpeed);
        maxSpeedText = (TextView) findViewById(R.id.maxSpeedText);
        positionText = (TextView) findViewById(R.id.currentPosition);
        highestSpeedText = (TextView) findViewById(R.id.fastestSpeedText);

        checkBox = (CheckBox) findViewById(R.id.customCheck);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    private void setUpImageListeners() {
        ImageView walkImage = (ImageView) findViewById(R.id.walking);
        ImageView runImage = (ImageView) findViewById(R.id.running);
        ImageView bikeImage = (ImageView) findViewById(R.id.biking);
        ImageView carImage = (ImageView) findViewById(R.id.car);

        walkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementTypeBar.setProgress(WALKING);
            }
        });

        runImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementTypeBar.setProgress(RUNNING);
            }
        });

        bikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementTypeBar.setProgress(BIKING);
            }
        });

        carImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementTypeBar.setProgress(DRIVING);
            }
        });

    }

}
