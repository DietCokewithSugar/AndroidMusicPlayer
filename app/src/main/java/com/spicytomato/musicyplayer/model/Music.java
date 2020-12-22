package com.spicytomato.musicyplayer.model;

import android.net.Uri;

public class Music  {

    private String musicName;
    private Uri contentUri;

    public Music(String _musicName, Uri _contentUri) {
        this.musicName = _musicName;
        this.contentUri = _contentUri;
    }

    public String getMusicName() {
        return musicName;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }
}
