package com.android.lab11;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private EditText etFieldURL;
    private CheckBox cbLoop;
    private Button btnPlay, btnPause, btnResume, btnStop;

    private String currentVideoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        surfaceView = findViewById(R.id.videoView);
        etFieldURL = findViewById(R.id.etFieldURL);
        cbLoop = findViewById(R.id.CBloop);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnStop = findViewById(R.id.btnStop);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        btnPlay.setOnClickListener(v -> playVideo());

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        Toast.makeText(VideoActivity.this, "Видео на паузе", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalStateException e) {
                    Toast.makeText(VideoActivity.this, "Ошибка паузы", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnResume.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.start();
                    Toast.makeText(VideoActivity.this, "Воспроизведение продолжено", Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(VideoActivity.this, "Сначала нажмите Play", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VideoActivity.this, "Сначала нажмите Play", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(v -> stopVideo());

        cbLoop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.setLooping(isChecked);
                } catch (IllegalStateException e) {
                    Toast.makeText(VideoActivity.this, "Не удалось изменить loop", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playVideo() {
        String videoPath = etFieldURL.getText().toString().trim();

        if (videoPath.isEmpty() || videoPath.equalsIgnoreCase("Name")) {
            Toast.makeText(this, "Введите корректную ссылку", Toast.LENGTH_SHORT).show();
            return;
        }

        if (surfaceHolder == null) {
            Toast.makeText(this, "Поверхность видео еще не готова", Toast.LENGTH_SHORT).show();
            return;
        }

        currentVideoPath = videoPath;
        releaseMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnPreparedListener(mp -> {
                Toast.makeText(VideoActivity.this, "Воспроизведение...", Toast.LENGTH_SHORT).show();
                mp.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (!mp.isLooping()) {
                    Toast.makeText(VideoActivity.this, "Видео завершено", Toast.LENGTH_SHORT).show();
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                String errorMsg = "Ошибка: " + what + " (Extra: " + extra + ")";
                android.util.Log.e("VIDEO_DEBUG", errorMsg);
                Toast.makeText(VideoActivity.this, "Проверьте URL или интернет. " + errorMsg, Toast.LENGTH_LONG).show();
                releaseMediaPlayer();
                return true;
            });

            mediaPlayer.setDisplay(surfaceHolder);

            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.setDataSource(this, Uri.parse(videoPath));
            mediaPlayer.setLooping(cbLoop.isChecked());
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка пути: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Неверная ссылка на видео", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка состояния плеера", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            releaseMediaPlayer();
            Toast.makeText(this, "Видео остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}