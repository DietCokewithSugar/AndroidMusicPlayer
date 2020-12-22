package com.spicytomato.musicyplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.spicytomato.musicyplayer.adapters.MusicListAdapter;
import com.spicytomato.musicyplayer.fragments.MusicListFragment;
import com.spicytomato.musicyplayer.model.Music;
import com.spicytomato.musicyplayer.services.GrayService;

import java.io.IOException;
import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private final static int REQUESTCODE = 0x123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MusicListFragment musicListFragment = new MusicListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, musicListFragment)
                .commit();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "缺少读取文件的权限", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);

                return;
            } else {
                Log.d("TAG", "onCreate: " + 1);
            }
        }







    }


}


