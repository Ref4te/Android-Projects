package com.service.playservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private PlayService playService;
    private boolean isBound = false;

    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    private SeekBar seekBar;
    private ImageButton btnLoop, btnPrev, btnPlayPause, btnNext;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
            playService = binder.getService();
            isBound = true;
            setupUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        seekBar = findViewById(R.id.seekBar);
        btnLoop = findViewById(R.id.btn_loop);
        btnPrev = findViewById(R.id.btn_prev);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        updateSeekBar();
    }

    private void setupUI() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackAdapter(playService.getPlaylist(), position -> {
            playService.playTrack(position);
            updateUIStates();
        });
        recyclerView.setAdapter(adapter);

        btnPlayPause.setOnClickListener(v -> {
            playService.togglePlayPause();
            updateUIStates();
        });

        btnNext.setOnClickListener(v -> {
            playService.nextTrack();
            updateUIStates();
        });

        btnPrev.setOnClickListener(v -> {
            playService.prevTrack();
            updateUIStates();
        });

        btnLoop.setOnClickListener(v -> {
            playService.toggleLoop();
            btnLoop.setAlpha(playService.isLooping() ? 1.0f : 0.5f);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isBound) {
                    playService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateUIStates() {
        if (!isBound) return;
        
        boolean isPlaying = playService.isPlaying();
        int currentIndex = playService.getCurrentTrackIndex();

        if (isPlaying) {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }

        if (adapter != null) {
            adapter.setCurrentTrackIndex(currentIndex, isPlaying);
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBound) {
                    seekBar.setMax(playService.getDuration());
                    seekBar.setProgress(playService.getCurrentPosition());
                    updateUIStates();
                    btnLoop.setAlpha(playService.isLooping() ? 1.0f : 0.5f);
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        handler.removeCallbacksAndMessages(null);
    }
}