package com.spicytomato.musicyplayer.fragments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spicytomato.musicyplayer.DetailActivity;
import com.spicytomato.musicyplayer.R;
import com.spicytomato.musicyplayer.adapters.MusicListAdapter;
import com.spicytomato.musicyplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private MusicListAdapter mMusicListAdapter;
    private RecyclerView mRecyclerView;
    public static List<Music> mMusicList;
    private MutableLiveData<List<Music>> mMutableLiveData;
    public static boolean hasMusic = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.musiclist_fragment_layout,container,false);


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mRecyclerView = requireActivity().findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mMusicListAdapter = new MusicListAdapter();

        mRecyclerView.setAdapter(mMusicListAdapter);

        mMusicList = new ArrayList<>();

        mMutableLiveData = new MutableLiveData<>();
        mMutableLiveData.setValue(mMusicList);


        mMutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> music) {
                mMusicListAdapter.submitList(mMusicList);
            }
        });

//        initView();
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar3);
        toolbar.inflateMenu(R.menu.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.backTo_detail:
                            Intent intent = new Intent(requireActivity(), DetailActivity.class);
                            if(!hasMusic){
                                Toast.makeText(getContext(),"没播呢", Toast.LENGTH_SHORT).show();
                            }else{
                            intent.putExtra("position",0);
                            intent.putExtra("content","0");
                            intent.putExtra("name","0x123");
                            startActivity(intent);
                            break;}
                        case R.id.refresh:
                            mMusicList.clear();
                            searchMusicList();
                            mMusicListAdapter.notifyDataSetChanged();
                            Toast toast = Toast.makeText(getContext(),null, Toast.LENGTH_SHORT);
                            toast.setText("共找到" + mMusicList.size() +"音乐");
                            toast.show();
                            break;
                    }
                    return true;
                }
            });
        }


        searchMusicList();
        if (mMusicList.size() == 2) {
            mMusicList.clear();
        }
        Toast toast = Toast.makeText(getContext(),null, Toast.LENGTH_SHORT);
        toast.setText("共找到" + mMusicList.size() +"音乐");
        toast.show();

        mMusicListAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                Music music = mMusicList.get(position);
                intent.putExtra("position",position);
                intent.putExtra("content",music.getContentUri().toString());
                intent.putExtra("name",music.getMusicName());
                hasMusic = true;
                startActivity(intent);
            }
        });

    }

//    private void initView() {
//        mMusicList.add(new Music("Tours",Uri.parse("http://yinyueshiting.baidu.com/data2/music/123000197/64751879200128.mp3?xcode=a418dadf8b6675d26d68292374921cf6ee5844c2417c39ce")));
//    }

    public void searchMusicList(){
        ContentResolver contentProvider = requireActivity().getContentResolver();

        Uri uri  = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cursor = contentProvider.query(uri,null,null,null);
        }
        if (cursor == null) {
            //fail
        }else if (!cursor.moveToFirst()){
            //no data
        }else {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                long thisId = cursor.getLong(idColumn);
                Log.d("TAG", "thisId: " + thisId);
                String thisTitle = cursor.getString(titleColumn);

                Log.d("TAG", "searchMusicList: " + thisId + " " + thisTitle);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,thisId);
                Log.d("TAG", "searchMusicList: "  + contentUri.toString());
                if (thisTitle.length() == 0) {
                    continue;
                }
                mMusicList.add(new Music(thisTitle,contentUri));
            }while (cursor.moveToNext());
        }
    }


}
