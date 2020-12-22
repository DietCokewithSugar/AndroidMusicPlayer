package com.spicytomato.musicyplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.spicytomato.musicyplayer.adapters.MusicListAdapter;
import com.spicytomato.musicyplayer.services.PlayService;
import com.spicytomato.musicyplayer.utils.Utils;

import java.util.Random;

public class DetailActivity extends Activity {

    private static final int SEQUENCE = 0;
    private static final int RANDOM = 1;
    private static final int LOOP = 2;
    private static final String CLICKS = "clicks";
    private Button mButton_after;
    private Button mButton_before;
    private SeekBar mSeekBar;
    private TextView mTextView;
    private Button mButton_play;
    private final static int UPDATE_SEEKBAR = 1;
    private TextView mTextView_duration;
    private TextView mTextView_current;
    private final static String PROGRESS = "progress";
    private MyServiceConnection mMyServiceConnection;
    private String mMusicName;
    private Uri mPath;
    private int mPosition;
    private int playType = 0;
    private Button mButton_type;
    private int clicks = 0;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivtiy_layout);

        mTextView = findViewById(R.id.musicName_d);
        mSeekBar = findViewById(R.id.seekBar2);
        mButton_play = findViewById(R.id.button_play);
        mButton_before = findViewById(R.id.button_before);
        mButton_after = findViewById(R.id.button_after);
        mButton_type = findViewById(R.id.button_type);

        mTextView_current = findViewById(R.id.textView_current);
        mTextView_duration = findViewById(R.id.textView_duration);

        Intent intent = getIntent();
        mMusicName = intent.getStringExtra("name");
        mPath = Uri.parse(intent.getStringExtra("content"));
        mPosition = intent.getIntExtra("position", 0);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        mEditor = mSharedPreferences.edit();
        clicks = mSharedPreferences.getInt(CLICKS, 0);


        mButton_type.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                clicks++;
                Toast toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                switch (clicks % 3) {
                    case 0:
                        toast.setText("顺序循环播放");
                        toast.show();
                        playType = SEQUENCE;
                        break;
                    case 1:
                        toast.setText("随机播放");
                        toast.show();
                        playType = RANDOM;
                        break;
                    case 2:
                        toast.setText("单曲循环");
                        toast.show();
                        playType = LOOP;
                        break;
                }
                mEditor.putInt(CLICKS, clicks);
                mEditor.apply();

            }
        });

        registerService();

//        createBottomSheet();

    }

    @SuppressLint("ResourceType")
    private void createBottomSheet() {
        BottomSheetLayout bottomSheetLayout = findViewById(R.id.bottomSheet);
//        bottomSheetLayout.showWithSheetView(LayoutInflater.from(getApplicationContext()).inflate(R.id.bottom_recycler,bottomSheetLayout,false));

        RecyclerView recyclerView = findViewById(R.id.recyclerView_bottom);
        recyclerView.setLayoutManager(new LinearLayoutManager(com.spicytomato.musicyplayer.DetailActivity.this, RecyclerView.VERTICAL, false));
        MusicListAdapter musicListFragment = new MusicListAdapter();
        recyclerView.setAdapter(musicListFragment);

//        bottomSheetLayout.expandSheet();
    }

    private void registerService() {

        mMyServiceConnection = new MyServiceConnection();

        Intent intent = new Intent(this, PlayService.class);

        startService(intent);
        bindService(intent, mMyServiceConnection, BIND_AUTO_CREATE);
    }


    public class MyServiceConnection implements ServiceConnection {
        private boolean seekBarMoved;
        private PlayService.MyBinder mMyBinder;
        private Handler mHandler;
        private boolean mFirsttime;
        private int musicListSize = com.spicytomato.musicyplayer.fragments.MusicListFragment.mMusicList.size();
        private int clicks = 0;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG", "onServiceConnected: ");

            mMyBinder = (PlayService.MyBinder) service;

            if (mMusicName.equals("0x123")) {
                mFirsttime = true;
                mTextView.setText(mSharedPreferences.getString("musicName", "0"));
                mPosition = mSharedPreferences.getInt("position", 0);

            } else if (mPosition == mSharedPreferences.getInt("position", 0)
                    && mSharedPreferences.getString("musicName", "0").equals(mMusicName)) {
                mFirsttime = true;
                mTextView.setText(mSharedPreferences.getString("musicName", "0"));
                mPosition = mSharedPreferences.getInt("position", 0);
            } else {
                mFirsttime = true;
                mTextView.setText(mMusicName);


                mMyBinder.resetRescource();
                mMyBinder.setDataResource(getApplicationContext(), mPath);
            }

            mHandler = new Handler(new Handler.Callback() {


                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case UPDATE_SEEKBAR:
                            if (mMyBinder.isPlaying() && !mFirsttime) {
                                Log.d("TAG", "handleMessage: " + mMyBinder.isPlaying() + mMyBinder.getCurrentDuration());
                                mSeekBar.setProgress(mMyBinder.getCurrentDuration());
                                mTextView_current.setText(Utils.timeParse(mMyBinder.getCurrentDuration()));
                                mHandler.sendEmptyMessageDelayed(UPDATE_SEEKBAR, 500);
                                break;
                            } else if (mMyBinder.isPlaying() && mFirsttime) {
                                mSeekBar.setMax(mMyBinder.getDuration());
                                mTextView_duration.setText(Utils.timeParse(mMyBinder.getDuration()));
                                mFirsttime = false;
                            }
                            mHandler.sendEmptyMessageDelayed(UPDATE_SEEKBAR, 500);
                            break;
                        default:
                            if (mMyBinder.isPlaying()) {
                                mMyBinder.seekTo(msg.getData().getInt(PROGRESS, 0));
                            }
                            break;
                    }

                    return true;
                }
            });

            mHandler.sendEmptyMessage(UPDATE_SEEKBAR);

            mMyBinder.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (playType) {
                        case SEQUENCE:
                            mMyBinder.resetRescource();
                            mFirsttime = true;
                            nextSong();
                            break;
                        case RANDOM:
                            mMyBinder.resetRescource();
                            mFirsttime = true;
                            Random random = new Random();
                            switchSong(random.nextInt(musicListSize - 1));
                            break;
                        case LOOP:
                            mMyBinder.play();
                            break;
                    }
                }
            });


            mButton_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyBinder.play();
                    Log.d("TAG", "isPlaying: " + mMyBinder.isPlaying());
                }
            });

            mButton_before.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyBinder.resetRescource();
                    mFirsttime = true;
                    Log.d("TAG", "size: " + musicListSize);
                    if (playType == SEQUENCE || playType == LOOP) {
                        beforeSong();
                    } else if (playType == RANDOM) {
                        Random random = new Random();
                        switchSong(random.nextInt(musicListSize - 1));
                    }
                }
            });

            mButton_after.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyBinder.resetRescource();
                    mFirsttime = true;
                    if (playType == SEQUENCE || playType == LOOP) {
                        nextSong();
                    } else if (playType == RANDOM) {
                        Random random = new Random();
                        switchSong(random.nextInt(musicListSize - 1));
                    }
                }
            });

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                private Message mMessage;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        seekBarMoved = true;
                        mMessage = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt(PROGRESS, progress);
                        mMessage.setData(bundle);
                    } else {
                        seekBarMoved = false;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mHandler.sendMessage(mMessage);
                }
            });
        }

        private void nextSong() {
            if (mPosition == musicListSize - 1) {
                mPosition = 0;
                switchSong(mPosition);
            } else {
                mPosition = (mPosition + 1) % musicListSize;
                switchSong(mPosition);
            }
        }

        private void beforeSong() {
            Log.d("before", "beforeSong: " + mPosition + " " + musicListSize);
            if (mPosition == 0) {
                mPosition = musicListSize-1;
                switchSong(mPosition);
                Log.d("before", "beforeSong: " + mPosition);
            } else {
                mPosition = mPosition - 1;
                switchSong(mPosition);
                Log.d("before", "beforeSong: " + mPosition);
            }
        }

        private void switchSong(int position) {
            mTextView.setText(com.spicytomato.musicyplayer.fragments.MusicListFragment.mMusicList.get(position).getMusicName());
            mMyBinder.setDataResource(getApplicationContext(), com.spicytomato.musicyplayer.fragments.MusicListFragment.mMusicList.get(position).getContentUri());
            mHandler.sendEmptyMessage(UPDATE_SEEKBAR);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mMusicName.equals("0x123")) {
            mEditor.putString("musicName", mMusicName);
            mEditor.putInt("position", mPosition);
            mEditor.apply();
        }
        unbindService(mMyServiceConnection);
    }
}
