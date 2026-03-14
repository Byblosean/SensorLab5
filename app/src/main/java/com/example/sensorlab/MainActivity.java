package com.example.sensorlab;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private LinearLayout mainLayout;
    private TextView titleText;
    private TextView lightValueText;
    private TextView statusText;
    private TextView recommendationText;
    private TextView minText;
    private TextView maxText;
    private TextView avgText;
    private ProgressBar lightProgressBar;
    private Button resetButton;

    private float minLux = Float.MAX_VALUE;
    private float maxLux = 0f;

    private final ArrayList<Float> values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        titleText = findViewById(R.id.titleText);
        lightValueText = findViewById(R.id.lightValueText);
        statusText = findViewById(R.id.statusText);
        recommendationText = findViewById(R.id.recommendationText);
        minText = findViewById(R.id.minText);
        maxText = findViewById(R.id.maxText);
        avgText = findViewById(R.id.avgText);
        lightProgressBar = findViewById(R.id.lightProgressBar);
        resetButton = findViewById(R.id.resetButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        if (lightSensor == null) {
            lightValueText.setText("Поточне значення: -- lx");
            statusText.setText("Статус: датчик недоступний");
            recommendationText.setText("Рекомендація: пристрій не підтримує датчик освітлення");
            minText.setText("Мінімум: --");
            maxText.setText("Максимум: --");
            avgText.setText("Середнє: --");
            resetButton.setEnabled(false);
        }

        resetButton.setOnClickListener(v -> resetStatistics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_LIGHT) {
            return;
        }

        float lux = event.values[0];

        lightValueText.setText(String.format(Locale.US, "%.2f lx", lux));
        if (lux < minLux) {
            minLux = lux;
        }

        if (lux > maxLux) {
            maxLux = lux;
        }

        values.add(lux);

        if (values.size() > 20) {
            values.remove(0);
        }

        float avg = calculateAverage();

        minText.setText(String.format(Locale.US, "Мінімум: %.2f lx", minLux));
        maxText.setText(String.format(Locale.US, "Максимум: %.2f lx", maxLux));
        avgText.setText(String.format(Locale.US, "Середнє: %.2f lx", avg));

        updateProgressBar(lux);
        updateUiByLux(lux);
    }

    private float calculateAverage() {
        if (values.isEmpty()) {
            return 0f;
        }

        float sum = 0f;
        for (float value : values) {
            sum += value;
        }

        return sum / values.size();
    }

    private void updateProgressBar(float lux) {
        int progress;

        if (lux >= 1000f) {
            progress = 100;
        } else {
            progress = (int) (lux / 10f);
        }

        lightProgressBar.setProgress(progress);
    }

    private void updateUiByLux(float lux) {
        if (lux < 50f) {
            statusText.setText("Статус: Темно");
            recommendationText.setText("Рекомендація: освітлення недостатнє, краще увімкнути лампу");
            setDarkTheme();
        } else if (lux < 300f) {
            statusText.setText("Статус: Нормальне освітлення");
            recommendationText.setText("Рекомендація: освітлення комфортне для звичайного користування");
            setNormalTheme();
        } else if (lux < 700f) {
            statusText.setText("Статус: Добре освітлення");
            recommendationText.setText("Рекомендація: підходить для читання та роботи");
            setBrightTheme();
        } else {
            statusText.setText("Статус: Дуже яскраво");
            recommendationText.setText("Рекомендація: освітлення надто сильне, можливий дискомфорт для очей");
            setVeryBrightTheme();
        }
    }

    private void resetStatistics() {
        values.clear();
        minLux = Float.MAX_VALUE;
        maxLux = 0f;

        lightValueText.setText("-- lx");
        statusText.setText("Статус: очікування");
        recommendationText.setText("Рекомендація: очікування нових вимірювань");
        minText.setText("Мінімум: --");
        maxText.setText("Максимум: --");
        avgText.setText("Середнє: --");
        lightProgressBar.setProgress(0);

        mainLayout.setBackgroundColor(Color.parseColor("#101418"));
        titleText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.parseColor("#B0BEC5"));
        recommendationText.setTextColor(Color.parseColor("#ECEFF1"));
        minText.setTextColor(Color.parseColor("#CFD8DC"));
        maxText.setTextColor(Color.parseColor("#CFD8DC"));
        avgText.setTextColor(Color.parseColor("#CFD8DC"));
        lightValueText.setTextColor(Color.parseColor("#FFD54F"));
        resetButton.setTextColor(Color.BLACK);
    }

    private void setDarkTheme() {
        mainLayout.setBackgroundColor(Color.parseColor("#101418"));
        titleText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.parseColor("#B0BEC5"));
        recommendationText.setTextColor(Color.parseColor("#ECEFF1"));
        minText.setTextColor(Color.parseColor("#CFD8DC"));
        maxText.setTextColor(Color.parseColor("#CFD8DC"));
        avgText.setTextColor(Color.parseColor("#CFD8DC"));
        lightValueText.setTextColor(Color.parseColor("#FFD54F"));
    }

    private void setNormalTheme() {
        mainLayout.setBackgroundColor(Color.parseColor("#101418"));
        titleText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.parseColor("#B0BEC5"));
        recommendationText.setTextColor(Color.parseColor("#ECEFF1"));
        minText.setTextColor(Color.parseColor("#CFD8DC"));
        maxText.setTextColor(Color.parseColor("#CFD8DC"));
        avgText.setTextColor(Color.parseColor("#CFD8DC"));
        lightValueText.setTextColor(Color.parseColor("#FFD54F"));
    }

    private void setBrightTheme() {
        mainLayout.setBackgroundColor(Color.parseColor("#101418"));
        titleText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.parseColor("#B0BEC5"));
        recommendationText.setTextColor(Color.parseColor("#ECEFF1"));
        minText.setTextColor(Color.parseColor("#CFD8DC"));
        maxText.setTextColor(Color.parseColor("#CFD8DC"));
        avgText.setTextColor(Color.parseColor("#CFD8DC"));
        lightValueText.setTextColor(Color.parseColor("#81C784"));
    }

    private void setVeryBrightTheme() {
        mainLayout.setBackgroundColor(Color.parseColor("#101418"));
        titleText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.parseColor("#B0BEC5"));
        recommendationText.setTextColor(Color.parseColor("#ECEFF1"));
        minText.setTextColor(Color.parseColor("#CFD8DC"));
        maxText.setTextColor(Color.parseColor("#CFD8DC"));
        avgText.setTextColor(Color.parseColor("#CFD8DC"));
        lightValueText.setTextColor(Color.parseColor("#4FC3F7"));
    }

    private void setTextColor(int color) {
        titleText.setTextColor(color);
        lightValueText.setTextColor(color);
        statusText.setTextColor(color);
        recommendationText.setTextColor(color);
        minText.setTextColor(color);
        maxText.setTextColor(color);
        avgText.setTextColor(color);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}