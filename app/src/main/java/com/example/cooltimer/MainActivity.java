package com.example.cooltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView time;
    private SeekBar seekBar;
    private int defaultTime = 30;
    private CountDownTimer timer;
    private Button st;
    private boolean isButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        time = findViewById(R.id.time);
        st = findViewById(R.id.start_stop);

        seekBar.setProgress(defaultTime);
        changeSeekBar(seekBar, time);

        timer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(transformTime((int)(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {

            }
        };

        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isButton) {
                    st.setText("Stop");
                    countDown(seekBar, time, timer);
                    isButton = false;
                } else {
                    st.setText("Start");
                    timer.cancel();
                    seekBar.setEnabled(true);
                    defaultValue(seekBar, time, defaultTime);
                    isButton = true;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultValue(seekBar, time, defaultTime);
    }

    static void changeSeekBar(SeekBar seekBar, TextView time) {
        seekBar.setMax(600); //
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int newProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    time.setText(transformTime(progress));
                    newProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(newProgress);
            }

        });
    }

    static void countDown(SeekBar seekBar, TextView textView, CountDownTimer countDownTimer) {
        seekBar.setEnabled(false);
        countDownTimer.onTick((long)seekBar.getProgress());
        countDownTimer.start();
    }

    static void defaultValue(SeekBar seekBar, TextView textView, int defaultTime) {
        seekBar.setProgress(defaultTime);
        textView.setText(transformTime(defaultTime));
    }


    static String transformTime(int progress) {
        int m = (progress / 60) % 60;
        int s = progress % 60;
        return String.format("%02d:%02d", m, s);
    }

}