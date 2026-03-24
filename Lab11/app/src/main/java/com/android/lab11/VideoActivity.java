package com.android.lab11;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video); // Проверьте, что ваш XML файл называется activity_media.xml

        surfaceView = findViewById(R.id.videoView);
        etFieldURL = findViewById(R.id.etFieldURL);
        cbLoop = findViewById(R.id.CBloop);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnStop = findViewById(R.id.btnStop);

        // Настройка SurfaceView для отображения видео
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // Слушатели нажатий
        btnPlay.setOnClickListener(v -> playVideo());
        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        });
        btnResume.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start();
        });
        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                // После stop() плеер нужно подготовить заново, если захотим нажать Play опять
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void playVideo() {
        String videoPath = etFieldURL.getText().toString().trim();

        // Проверка на пустоту и стандартный подсказчик
        if (videoPath.isEmpty() || videoPath.equalsIgnoreCase("Name")) {
            Toast.makeText(this, "Введите корректную ссылку", Toast.LENGTH_SHORT).show();
            return;
        }

        releaseMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();

            // 1. Устанавливаем слушатели СРАЗУ
            mediaPlayer.setOnPreparedListener(mp -> {
                Toast.makeText(VideoActivity.this, "Воспроизведение...", Toast.LENGTH_SHORT).show();
                mp.start();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                // Выводим конкретные коды ошибок для отладки
                String errorMsg = "Ошибка: " + what + " (Extra: " + extra + ")";
                android.util.Log.e("VIDEO_DEBUG", errorMsg);
                Toast.makeText(VideoActivity.this, "Проверьте URL или интернет: " + errorMsg, Toast.LENGTH_LONG).show();
                return true;
            });

            // Привязываем к SurfaceHolder
            mediaPlayer.setDisplay(surfaceHolder);

            // Используем Uri для сетевых путей
            mediaPlayer.setDataSource(this, android.net.Uri.parse(videoPath));

            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.setLooping(cbLoop.isChecked());

            // Запускаем подготовку
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка пути: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Методы жизненного цикла SurfaceView
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}