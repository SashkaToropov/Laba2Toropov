package com.example.laba2toropov;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.LineAndPointFormatter;


public class MainActivity extends AppCompatActivity {

    private XYPlot mPlot;

    private double[] numbers;
    private EditText mEditText;
    private CheckBox checkBoxMean;
    private CheckBox checkBoxSTD;
    private TextView textMean;
    private TextView textSTD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.file_context);
        checkBoxMean = findViewById(R.id.checkBoxMean);
        checkBoxSTD = findViewById(R.id.checkBoxSTD);
        textMean = findViewById(R.id.textMean);
        textSTD = findViewById(R.id.textSTD);
        mPlot = findViewById(R.id.plot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openFile(View view) {
        readFile();
    }

    public void plotGraph(View view) {
        if (numbers != null && numbers.length > 1) {
            List<Number> xNumbers = new ArrayList<>();
            List<Number> yNumbers = new ArrayList<>();
            for (int i = 0; i < numbers.length - 1; i++) {
                xNumbers.add(numbers[i]);
                yNumbers.add(numbers[i + 1]);
            }
            XYSeries series = new SimpleXYSeries(xNumbers, yNumbers, "Graph");
            LineAndPointFormatter pointFormatter = new LineAndPointFormatter(
                    null, Color.GREEN, null, null);
            mPlot.clear();
            mPlot.addSeries(series, pointFormatter);
            mPlot.redraw();
        } else {
            Toast.makeText(this, "Not enough data to plot the graph",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateStatistics(View view) {
        if (numbers != null && numbers.length > 0) {
            double mean = calculateMean();
            double std = calculateSTD();
            if (checkBoxMean.isChecked()) {
                textMean.setText(String.format(" : %.2f", mean));
            }
            if (checkBoxSTD.isChecked()) {
                // відображаємо  розрахунку
                textSTD.setText(String.format(" : %.2f", std));
            }
        } else {
            Toast.makeText(this, "No data available for calculations",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateMean() {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }
    private double calculateSTD() {
        double mean = calculateMean();
        double sumSquaredDiff = 0;
        for (double number : numbers) {
            sumSquaredDiff += Math.pow(number - mean, 2);
        }
        return Math.sqrt(sumSquaredDiff / numbers.length);
    }

    private void readFile() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.e001);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null && count <= 1000) {
                if (!line.trim().isEmpty()) {
                    stringBuilder.append(line.trim()).append("\n");
                }
                count ++;
            }
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            String fileContent = stringBuilder.toString().trim();
            if (TextUtils.isEmpty(fileContent)) {
                Log.e("ReadFile", "File content is empty.");
                return;
            }
            String[] numberStrings = fileContent.split("\\r?\\n");
            if (numberStrings.length == 0) {
                Log.e("ReadFile", "No numbers found in the file.");
                return;
            }
            numbers = new double[numberStrings.length];
            for (int i = 0; i < numberStrings.length; i++) {
                try {
                    numbers[i] = Double.parseDouble(numberStrings[i]);
                } catch (NumberFormatException e) {
                    Log.e("ReadFile", "Error parsing number: " + numberStrings[i]);
                    e.printStackTrace();
                }
            }
            mEditText.setText(fileContent);
            Log.d("ReadFile", "File content loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ReadFile", "Error reading file: " + e.getMessage());
        }
    }
}