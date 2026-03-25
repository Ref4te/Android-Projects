package com.android.lab11;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    ImageView photoView;
    Button btnPrev;
    Button btnNext;
    TextView numeration;
    private List<Uri> photoUris = new ArrayList<>();

    int currentPhotoIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);

        photoView = findViewById(R.id.photoView);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        numeration = findViewById(R.id.numeration);

        btnPrev.setOnClickListener(v -> showPreviousPhoto());
        btnNext.setOnClickListener(v -> showNextPhoto());

        loadPhotosFromStorage();
    }
    private void loadPhotosFromStorage() {
        photoUris.clear(); // Очищаем список на всякий случай

        // Указываем, откуда брать данные (база данных изображений MediaStore)
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Указываем, какие колонки нам нужны (только ID и дата добавления)
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
        };

        // Сортируем: сначала самые новые (по дате добавления, по убыванию DESC)
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Делаем запрос в систему через Cursor (это как Excel-таблица с результатами)
        try (Cursor cursor = getContentResolver().query(
                collection,
                projection,
                null, // Без фильтров (берем все фото)
                null,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                // Находим индекс колонки ID
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                // Проходимся по всей "таблице" результатов
                do {
                    // Получаем ID фотографии
                    long id = cursor.getLong(idColumn);
                    // Превращаем ID в полный адрес (Uri) файла
                    Uri fileUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    // Добавляем этот адрес в наш список
                    photoUris.add(fileUri);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка загрузки фотографий", Toast.LENGTH_SHORT).show();
        }

        if (!photoUris.isEmpty()) {
            currentPhotoIndex = 0;
            updatePhotoDisplay();
        } else {
            // Если фоток нет, пишем об этом
            numeration.setText("0 / 0");
            Toast.makeText(this, "Галерея пуста", Toast.LENGTH_LONG).show();
            //заглушка
            photoView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
    private void updatePhotoDisplay(){
        if (photoUris.isEmpty()) return;
        Uri currentUri = photoUris.get(currentPhotoIndex);
        photoView.setImageURI(currentUri);

        String countText = (currentPhotoIndex + 1) + " / " + photoUris.size();
        numeration.setText(countText);
    }

    private void showPreviousPhoto() {
        if (photoUris.isEmpty()) return;

        currentPhotoIndex--;

        // цикл
        if (currentPhotoIndex < 0) {
            currentPhotoIndex = photoUris.size() - 1;
        }

        updatePhotoDisplay();
    }

    private void showNextPhoto() {
        if (photoUris.isEmpty()) return;

        currentPhotoIndex++;

        // цикл
        if (currentPhotoIndex >= photoUris.size()) {
            currentPhotoIndex = 0;
        }

        updatePhotoDisplay();
    }

}
