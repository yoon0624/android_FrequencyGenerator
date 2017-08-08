package com.example.yoon.testtonegenerate;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by yoon on 2017-08-07.
 */

public class ToneGenerateTest {

    public class MainActivity extends AppCompatActivity {

        private final int duration = 3; // seconds
        private final int sampleRate = 8000;
        private final int numSamples= duration * sampleRate;
        private final double sample[] = new double[numSamples];
        private final double freqOfTone = 1500; //hz
        private final byte generatedSnd[] = new byte[2* numSamples];

        TextView tv_samplerate, tv_frequency, tv_title, tv_value;

        Handler handler  = new Handler();
        void setView(){
            tv_frequency =(TextView) findViewById(R.id.tv_freq);
            tv_samplerate =(TextView) findViewById(R.id.tv_sample);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_value = (TextView) findViewById(R.id.tv_state);
        }

        void ViewWrite(){
            tv_frequency.setText(Double.toString(freqOfTone));
            tv_samplerate.setText(Integer.toString(sampleRate));
            tv_title.setText("ToneGenerator Test");
            tv_value.setText("?");
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            setView();

            Thread thread = new Thread(new Runnable(){
                public void run(){

                    ViewWrite();

                    getTone();
                    handler.post(new Runnable(){
                        public void run(){
                            playSound();
                        }
                    });
                }
            });thread.start();
        }

        void getTone(){

            for(int i =0; i<numSamples; ++i){
                sample[i] = Math.sin(2*Math.PI * i / (sampleRate/freqOfTone));
            }

            int idx = 0;
            for(double dVal : sample){
                short val = (short) (dVal *32767);
                generatedSnd[idx++] = (byte) (val& 0x00ff);
                generatedSnd[idx++] = (byte) ((val&0xff00)>>>8);
            }
        }

        void playSound(){
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
            audioTrack.write(generatedSnd, 0, numSamples);
            audioTrack.play();
        }


    }


}
