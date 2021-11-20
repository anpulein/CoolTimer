package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private TextView time;
    private SeekBar seekBar;
    private int defaultInterval = 30;
    private CountDownTimer timer;
    private Button st;
    private boolean isTimerOn = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        time = findViewById(R.id.time);
        st = findViewById(R.id.start_stop);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600); // Max - 10 minuts
        setIntervalFromSharedPreferences(sharedPreferences);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText(transformTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }


    @SuppressLint("SetTextI18n")
    public void start(View view) {

        if(!isTimerOn) {
            st.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;
            timer = new CountDownTimer(seekBar.getProgress() * 1000L, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
//                Log.d("Time tag: ", transformTime((int)(millisUntilFinished / 1000)));
                    time.setText(transformTime((int)(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // Получаем доступ к файлу
                    if(sharedPreferences.getBoolean("enable_sound", true)) {

                        String melodyName = sharedPreferences.getString("timer_melody", "bell");

                        if(melodyName.equals("bell")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.bell_sound);
                            mediaPlayer.start();
                        } else if(melodyName.equals("alarm_siren")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.alarm_siren_sound);
                            mediaPlayer.start();
                        } else if(melodyName.equals("bip")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.bip_sound);
                            mediaPlayer.start();
                        }
                    }

                    resetTimer();
                }
            }.start();

        } else {
            resetTimer();
        }

    }

    @SuppressLint("SetTextI18n")
    private void resetTimer() {
        timer.cancel();
        st.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @SuppressLint("DefaultLocale")
    static private String transformTime(int progress) {
        int m = (progress / 60) % 60;
        int s = progress % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Creating menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Получаем элемент меню
        if(id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if(id == R.id.action_about) {
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences) {

        defaultInterval = Integer.parseInt(sharedPreferences.getString("default_interval", "30"));
        time.setText(transformTime(defaultInterval));
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("default_interval")) {
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this); // Снимаем с регистрии Listener
    }
}