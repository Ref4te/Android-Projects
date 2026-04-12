package com.service.playservice;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayService extends Service {

    private MediaPlayer mPlayer;
    private List<Track> playlist;
    private int currentTrackIndex = 0;
    private boolean isLooping = false;
    private boolean shouldStartAfterPrepare = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playlist = new ArrayList<>();
        playlist.add(new Track("Subwoofer Lullaby", "https://raw.githubusercontent.com/Ref4te/MediaForLabs/main/1-03_-subwoofer-lullaby.mp3"));
        playlist.add(new Track("New Drop x Trance", "https://raw.githubusercontent.com/Ref4te/MediaForLabs/main/Don_Toliver_Travis_Scott_feat._Metro_Boomin_-_New_drop_x_trance_(SkySound.cc).mp3"));
        playlist.add(new Track("Rock U", "https://raw.githubusercontent.com/Ref4te/MediaForLabs/main/blaze_-_rock_u_(SkySound.cc).mp3"));
        playlist.add(new Track("New Person, Same Old Mistakes", "https://raw.githubusercontent.com/Ref4te/MediaForLabs/main/tame_impala_-_new_person_same_old_mistakes_(SkySound.cc).mp3"));

        initPlayer();
        prepareTrack(false);
    }

    private void initPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
        }

        mPlayer = new MediaPlayer();
        mPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        mPlayer.setOnCompletionListener(mp -> {
            if (isLooping) {
                mp.start();
            } else {
                nextTrack();
            }
        });

        mPlayer.setOnPreparedListener(mp -> {
            if (shouldStartAfterPrepare) {
                mp.start();
            }
        });
    }

    public void playTrack(int index) {
        if (currentTrackIndex == index && mPlayer != null) {
            togglePlayPause();
            return;
        }
        currentTrackIndex = index;
        prepareTrack(true);
    }

    private void prepareTrack(boolean start) {
        if (currentTrackIndex < 0 || currentTrackIndex >= playlist.size()) return;
        try {
            shouldStartAfterPrepare = start;
            mPlayer.reset();
            mPlayer.setDataSource(playlist.get(currentTrackIndex).getUrl());
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void togglePlayPause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            if (currentTrackIndex == -1) currentTrackIndex = 0;
            mPlayer.start();
        }
    }

    public void nextTrack() {
        if (playlist.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
        prepareTrack(true);
    }

    public void prevTrack() {
        if (playlist.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex - 1 + playlist.size()) % playlist.size();
        prepareTrack(true);
    }

    public void toggleLoop() {
        isLooping = !isLooping;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public int getDuration() {
        try {
            return mPlayer != null ? mPlayer.getDuration() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentPosition() {
        try {
            return mPlayer != null ? mPlayer.getCurrentPosition() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void seekTo(int pos) {
        if (mPlayer != null) {
            mPlayer.seekTo(pos);
        }
    }

    public List<Track> getPlaylist() {
        return playlist;
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}