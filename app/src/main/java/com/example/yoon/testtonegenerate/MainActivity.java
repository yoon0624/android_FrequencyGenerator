package com.example.yoon.testtonegenerate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Yoon";

    private final int duration = 10; // seconds
    private final int sampleRate = 8000; // 샘플링주기 (1초에 8000번 샘플링하겠다.)
    private final int numSamples = duration * sampleRate; // duration동안 유지하기위한 샘플수
    private final double sample[] = new double[numSamples];
    private double freqOfTone = 1500; //hz
    private final byte generatedSnd[] = new byte[2 * numSamples];

    TextView tv_samplerate, tv_frequency, tv_title, tv_value;
    Button btn_start, btn_freqdn, btn_frequp;
    private SeekBar volumeControl = null;

    boolean play_state = true;
    Handler handler = new Handler();

    private String[] permissions = {Manifest.permission.MODIFY_AUDIO_SETTINGS};
    private final int PERMISSION_CODE_MODIFY_AUDIO_SETTINGS = 100;
    private boolean permissionstate= false;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSION_CODE_MODIFY_AUDIO_SETTINGS:
                permissionstate = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
    }

    public void setPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE_MODIFY_AUDIO_SETTINGS);
        }
    }

    void setView() {
        tv_frequency = (TextView) findViewById(R.id.tv_freq);
        tv_samplerate = (TextView) findViewById(R.id.tv_sample);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_value = (TextView) findViewById(R.id.tv_state);

        btn_start = (Button) findViewById(R.id.btn_Start);
        btn_freqdn = (Button) findViewById(R.id.btn_freqdn);
        btn_frequp = (Button) findViewById(R.id.btn_frequp);

        volumeControl = (SeekBar) findViewById(R.id.seekbar);

    }

    void ViewWrite() {
        tv_frequency.setText(Double.toString(freqOfTone));
        tv_samplerate.setText(Integer.toString(sampleRate));
        tv_title.setText("ToneGenerator Test");
        tv_value.setText("?");
    }

    void btnSet(){
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewWrite();
                playSound();
            }
        });
        btn_freqdn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                freqOfTone = freqOfTone - 100;
                getTone();
                ViewWrite();
            }
        });
        btn_frequp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                freqOfTone = freqOfTone + 100;
                getTone();
                ViewWrite();
            }
        });
//        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//
//            int progressChanged= 0;
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progressChanged = progress;
//                // 0=< progress <= 100
//                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress*(nMax/100), 0);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(MainActivity.this,"seek bar progress:"+progressChanged,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermissions();
        setView();
        btnSet();
        ViewWrite();
        getTone();
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeControl.setMax(nMax);
        volumeControl.setProgress(nCurrentVolume);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            int progressChanged= 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                // 0=< progress <= 100
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this,"seek bar progress:"+progressChanged,
                        Toast.LENGTH_SHORT).show();
            }
        });

        final TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                Log.i(TAG, "TimeTask1");
                playSound();
            }
        };

        final TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "TimeTask2");
            }
        };

        final Timer timer = new Timer();
//        timer.schedule(tt, 0, 1000);
//        timer.schedule(tt2, 0, 1000);

//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//
//
//                handler.post(new Runnable() {
//                    public void run() {
//                        if(play_state) {
//                            playSound();
//                        }
//
//                    }
//                });
//            }
//        });
//        thread.start();
    }

    // sin함수 제작
    void getTone() {

        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }

        int idx = 0;
        for (double dVal : sample) {
            short val = (short) (dVal * 32767);
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    void playSound() {
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, numSamples);
        audioTrack.play();
    }
}