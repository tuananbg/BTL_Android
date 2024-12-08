package com.buihuuduy.btl_android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;


import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class ChartExportFile extends AppCompatActivity {
    private BarChart barChart;
    private Button exportExcelBtn;
    private Button exportPdfBtn;
    private DataHandler dataHandler;

    ArrayList<String> days = new ArrayList<>();
    ArrayList<String> books = new ArrayList<>();
    ArrayList<BarEntry> weeklyEntries = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart_export_file);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
        }

        dataHandler = new DataHandler(this);

        barChart = findViewById(R.id.barChart);
        exportExcelBtn = findViewById(R.id.exportExcelBtn);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        ArrayList<BarEntry> weeklyEntries1 = dataHandler.getWeeklySalesFromDatabase();;

        int numberOfWeeks =  weeklyEntries1.size();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < numberOfWeeks; i++) {
            BarEntry entry = weeklyEntries1.get(i);
            // Sử dụng i làm giá trị x
            weeklyEntries.add(new BarEntry(i, entry.getY()));
        }

        days = dataHandler.getDaysFromDatabase();
        books = dataHandler.getSellBookListFromDatabase();
        for (int i = 0; i < numberOfWeeks; i++) {
            labels.add(days.get(i));
        }

        // Tạo BarDataSet
        BarDataSet barDataSet = new BarDataSet(weeklyEntries, "Tổng số lượng sách đã bán theo tuần");
        barDataSet.setColor(getResources().getColor(R.color.purple_700));
        barDataSet.setValueTextColor(getResources().getColor(R.color.black));

        // Tùy chỉnh trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getDescription().setEnabled(false);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f); // Bắt đầu từ 0
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setGranularityEnabled(true);
        yAxisRight.setAxisMinimum(0f); // Bắt đầu từ 0

        // Tạo BarData và cài đặt vào biểu đồ
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.invalidate();

        exportExcelBtn.setOnClickListener(v -> exportToExcel());
        exportPdfBtn.setOnClickListener(v -> exportToPdf());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                saveFileExcelToUri(uri);
            }
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                savePdfToUri(uri);
            }
        }
    }

    private void exportToExcel() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "sales_report.xlsx"); // Tên file mặc định
        startActivityForResult(intent, 1);
    }

    private void exportToPdf() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "BarChart_" + System.currentTimeMillis() + ".pdf");
        startActivityForResult(intent, 2);
    }

    private void saveFileExcelToUri(Uri uri)
    {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri))
        {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Báo cáo bán hàng");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Ngày");
            headerRow.createCell(1).setCellValue("Số lượng sách bán");

            // Ghi dữ liệu
            for (int i = 0; i < weeklyEntries.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(days.get(i));
                row.createCell(1).setCellValue(weeklyEntries.get(i).getY());
                row.createCell(2).setCellValue(books.get(i));
            }

            // Ghi dữ liệu vào file
            if (outputStream != null) {
                workbook.write(outputStream);
                workbook.close();
                Toast.makeText(this, "File đã được lưu thành công!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi lưu file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void savePdfToUri(Uri uri) {

    }

}