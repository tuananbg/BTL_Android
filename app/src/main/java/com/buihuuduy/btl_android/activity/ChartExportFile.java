package com.buihuuduy.btl_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import android.content.pm.PackageManager;
import android.widget.ImageButton;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.Manifest;
import java.io.OutputStream;
import java.util.ArrayList;
import android.graphics.Bitmap;
import java.io.IOException;

public class ChartExportFile extends AppCompatActivity {
    private BarChart barChart;
    private Button exportExcelBtn;
    private Button exportPdfBtn;
    private DataHandler dataHandler;

    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    ArrayList<String> days = new ArrayList<>();
    ArrayList<String> books = new ArrayList<>();
    ArrayList<BarEntry> weeklyEntries = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.export_sidebar);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
        }

        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_awaiting_approval) {
                    Intent intent = new Intent(ChartExportFile.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_logout) {
                    Intent intent = new Intent(ChartExportFile.this, LoginActivity.class);
                    startActivity(intent); finish();
                }
                drawerLayout.close();
                return false;
            }
        });

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
            headerRow.createCell(2).setCellValue("Sách được bán");

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
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        barChart.setDrawingCacheEnabled(true);
        Bitmap chartBitmap = Bitmap.createBitmap(barChart.getWidth(), barChart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(chartBitmap);
        barChart.draw(bitmapCanvas);

        float scale = Math.min((float) canvas.getWidth() / chartBitmap.getWidth(),
                (float) canvas.getHeight() / chartBitmap.getHeight());
        float x = (canvas.getWidth() - chartBitmap.getWidth() * scale) / 2;
        float y = (canvas.getHeight() - chartBitmap.getHeight() * scale) / 2;

        canvas.save();
        canvas.scale(scale, scale, x, y);
        canvas.drawBitmap(chartBitmap, x, y, null);
        canvas.restore();

        pdfDocument.finishPage(page);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            Toast.makeText(this, "PDF exported successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

}