package com.android.sro4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etBirthDate;
    private Spinner spGroup;
    private SeekBar sbAge;
    private TextView tvAgeLabel;
    private Button btnSave, btnLoad;

    // Ключи для сохранения данных
    private static final String PREF_NAME = "Name";
    private static final String PREF_GROUP = "GroupPos";
    private static final String PREF_AGE = "Age";
    private static final String PREF_DATE = "BirthDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        etName = findViewById(R.id.etName);
        etBirthDate = findViewById(R.id.etBirthDate);
        spGroup = findViewById(R.id.spGroup);
        sbAge = findViewById(R.id.sbAge);
        tvAgeLabel = findViewById(R.id.tvAgeLabel);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);

        btnSave.setOnClickListener(this);
        btnLoad.setOnClickListener(this);

        // Слушатель для изменения текста возраста при движении ползунка
        sbAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAgeLabel.setText("Возраст: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            saveData();
        } else if (v.getId() == R.id.btnLoad) {
            loadData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    private void saveData() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_NAME, etName.getText().toString());
        editor.putInt(PREF_GROUP, spGroup.getSelectedItemPosition());
        editor.putInt(PREF_AGE, sbAge.getProgress());
        editor.putString(PREF_DATE, etBirthDate.getText().toString());

        editor.apply(); // apply() выполняется в фоновом режиме, в отличие от commit()
        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);

        String name = pref.getString(PREF_NAME, "");
        int groupPos = pref.getInt(PREF_GROUP, 0);
        int age = pref.getInt(PREF_AGE, 18);
        String date = pref.getString(PREF_DATE, "");

        etName.setText(name);
        spGroup.setSelection(groupPos);
        sbAge.setProgress(age);
        tvAgeLabel.setText("Возраст: " + age);
        etBirthDate.setText(date);

        Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_SHORT).show();
    }
}