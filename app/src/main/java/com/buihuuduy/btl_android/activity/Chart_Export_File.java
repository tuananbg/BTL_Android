package com.buihuuduy.btl_android.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;


import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.buihuuduy.btl_android.entity.BookEntity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import android.graphics.Color;

public class Chart_Export_File extends AppCompatActivity {
    private BarChart barChart;
    Button exportExcelBtn;
    Button exportPdfBtn;
    private DataHandler dataHandler;

    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart_export_file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            }
        }

        dataHandler = new DataHandler(this);

        barChart = findViewById(R.id.barChart);
        exportExcelBtn = findViewById(R.id.exportExcelBtn);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        Random random = new Random();
        ArrayList<BarEntry> weeklyEntries1 = dataHandler.getWeeklySalesFromDatabase();;

        int numberOfWeeks =  weeklyEntries1.size();
        ArrayList<String> labels = new ArrayList<>();

        ArrayList<BarEntry> weeklyEntries = new ArrayList<>();
        for (int i = 0; i < numberOfWeeks; i++) {
            BarEntry entry = weeklyEntries1.get(i);
            // Sử dụng i làm giá trị x
            weeklyEntries.add(new BarEntry(i, entry.getY()));
        }

        for (int i = 0; i < numberOfWeeks; i++) {
            labels.add("Tuần " + (i + 1)); // Tạo nhãn: "Tuần 1", "Tuần 2", ...
        }

        // Tạo BarDataSet
        BarDataSet barDataSet = new BarDataSet(weeklyEntries, "Tổng số lượng sách đã bán theo tuần");
        barDataSet.setColor(getResources().getColor(R.color.purple_700));
        barDataSet.setValueTextColor(getResources().getColor(R.color.black));


        // Tùy chỉnh trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Thiết lập nhãn động
        xAxis.setGranularity(1f); // Đảm bảo hiển thị mỗi nhãn một cách chính xác
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt trục X ở dưới cùng
        //xAxis.setDrawGridLines(false); // Ẩn lưới
        barChart.getDescription().setEnabled(false);

        // Tạo BarData và cài đặt vào biểu đồ
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);  // Điều chỉnh độ rộng của cột

        barChart.setData(barData);
        barChart.invalidate(); // Refresh biểu đồ

        exportExcelBtn.setOnClickListener(v -> exportToExcel(weeklyEntries));
        exportPdfBtn.setOnClickListener(v -> exportToPdf(weeklyEntries));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Đã cấp quyền", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Từ chối cấp quyền", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void exportToExcel(ArrayList<BarEntry> weeklyEntries) {

        try {
            // Tạo workbook mới
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Báo cáo bán hàng");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tuần");
            headerRow.createCell(1).setCellValue("Số lượng sách bán");

            // Ghi dữ liệu
            for (int i = 0; i < weeklyEntries.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(weeklyEntries.get(i).getX());
                row.createCell(1).setCellValue(weeklyEntries.get(i).getY());
            }

            // Kiểm tra và tạo thư mục
            File directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo file
            File file = new File(directory, "sales_report_" + System.currentTimeMillis() + ".xlsx");

            // Ghi file
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
                workbook.close();

                // Thông báo thành công
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Xuất Excel thành công: " + file.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();

                    // Hiển thị dialog thông báo
                    new AlertDialog.Builder(this)
                            .setTitle("Xuất File")
                            .setMessage("File Excel đã được lưu tại: " + file.getAbsolutePath())
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
            e.printStackTrace();
            Log.e("ExportExcel", "Lỗi xuất file", e);

            runOnUiThread(() -> {
                Toast.makeText(this,
                        "Lỗi xuất file: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            });
        }
    }

    public void exportToPdf(ArrayList<BarEntry> weeklyEntries) {
        try {
            // Tạo tài liệu PDF mới
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // Chuẩn bị vẽ nội dung lên PDF
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(12);
            canvas.drawText("Báo cáo bán hàng theo tuần", 50, 50, paint);

            int yPosition = 100;
            for (BarEntry entry : weeklyEntries) {
                canvas.drawText("Tuần " + (entry.getX() + 1) + ": " + entry.getY() + " cuốn", 50, yPosition, paint);
                yPosition += 30;
            }

            pdfDocument.finishPage(page);

            // Tạo thư mục trong DOWNLOADS nếu chưa tồn tại
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo file PDF
            File file = new File(directory, "sales_report_" + System.currentTimeMillis() + ".pdf");

            // Ghi dữ liệu vào file PDF
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                pdfDocument.writeTo(fileOut);

                // Thông báo thành công
                runOnUiThread(() -> {
                    Toast.makeText(this, "Xuất PDF thành công tại: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    // Hiển thị dialog thông báo
                    showAlert("Xuất PDF thành công tại: " + file.getAbsolutePath());
                });
            }
            pdfDocument.close();
        } catch (Exception e) {
            // Xử lý ngoại lệ
            e.printStackTrace();
            Log.e("ExportPDF", "Lỗi xuất file PDF", e);

            runOnUiThread(() -> {
                Toast.makeText(this, "Lỗi xuất file PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }


    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export to File")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}