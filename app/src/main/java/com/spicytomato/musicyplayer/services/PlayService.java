package com.spicytomato.musicyplayer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

public class PlayService extends Service {

    MediaPlayer mMediaPlayer;
    private boolean mIsLoaded;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.stop();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return new MyBinder(mMediaPlayer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mMediaPlayer) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    public class MyBinder extends Binder {

        private final MediaPlayer mMediaPlayer;
        private boolean mIsLoaded;

        MyBinder(MediaPlayer _mediaPlayer) {
            mMediaPlayer = _mediaPlayer;
        }


        public void play() {

            if (mMediaPlayer.isPlaying() && mIsLoaded) {
                mMediaPlayer.pause();
            } else if (mIsLoaded && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }

        public boolean isPlaying() {
            return mMediaPlayer.isPlaying();
        }

        public boolean isLooping() {
            return mMediaPlayer.isLooping();
        }

        public void setLooping(boolean lopping) {
            if (!mMediaPlayer.isLooping()) {
                mMediaPlayer.setLooping(true);
            }
        }

        public int getDuration() {
            return mMediaPlayer.getDuration();
        }

        public int getCurrentDuration() {
            return mMediaPlayer.getCurrentPosition();
        }

        public void seekTo(int msec) {
            mMediaPlayer.seekTo(msec);
        }

        public boolean isComplete() {
            if (mMediaPlayer.getDuration() == mMediaPlayer.getCurrentPosition()) {

                return true;
            }
            return false;
        }

        public void setDataResource(Context context, Uri uri) {

            try {
                mMediaPlayer.setDataSource(context, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mIsLoaded = true;
                    mMediaPlayer.start();
                }
            });


        }

        public void resetRescource() {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mIsLoaded = false;

        }

        public boolean isLoaded() {
            return mIsLoaded;
        }

        public MediaPlayer getMediaPlayer() {
            return mMediaPlayer;
        }

    }


}
