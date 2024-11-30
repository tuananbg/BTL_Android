package com.buihuuduy.btl_android.activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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
//    private ExportUtils exportUtils;

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

        barChart = findViewById(R.id.barChart);
        exportExcelBtn = findViewById(R.id.exportExcelBtn);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        Random random = new Random();
        ArrayList<BarEntry> weeklyEntries = new ArrayList<>();
        float weeklySales = 0;
        int week = 1;

        for (int day = 1; day <= 30; day++) {
            float sales = random.nextInt(100) + 10;  // Giả lập số sách bán
            weeklySales += sales;

            // Sau khi đủ 7 ngày, thêm tuần vào biểu đồ và reset weeklySales
            if (day % 7 == 0 || day == 30) {  // Mỗi tuần có 7 ngày
                weeklyEntries.add(new BarEntry(week, weeklySales));
                weeklySales = 0;  // Reset cho tuần tiếp theo
                week++;
            }
        }

        // Tạo BarDataSet
        BarDataSet barDataSet = new BarDataSet(weeklyEntries, "Tổng Số lượng sách đã bán theo tuần");
        barDataSet.setColor(getResources().getColor(R.color.purple_700));
        barDataSet.setValueTextColor(getResources().getColor(R.color.black));

        // Tùy chỉnh trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Tạo BarData và cài đặt vào biểu đồ
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);  // Điều chỉnh độ rộng của cột

        barChart.setData(barData);
        barChart.invalidate(); // Refresh biểu đồ

        // Thiết lập listener cho nút xuất Excel và PDF
//        exportExcelBtn.setOnClickListener(v -> exportUtils.exportToExcel(weeklyEntries));
//        exportPdfBtn.setOnClickListener(v -> exportUtils.exportToPdf(weeklyEntries));

        exportExcelBtn.setOnClickListener(v -> exportToExcel(weeklyEntries));
        exportPdfBtn.setOnClickListener(v -> exportToPdf(weeklyEntries));

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Từ chối cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public void exportToExcel(ArrayList<BarEntry> weeklyEntries) {
//        try {
//            Workbook workbook = new XSSFWorkbook();
//            Sheet sheet = workbook.createSheet("Báo cáo bán hàng");
//
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("Tuần");
//            headerRow.createCell(1).setCellValue("Số lượng sách bán");
//
//            int rowIndex = 1;
//            for (BarEntry entry : weeklyEntries) {
//                Row row = sheet.createRow(rowIndex++);
//                row.createCell(0).setCellValue(entry.getX());
//                row.createCell(1).setCellValue(entry.getY());
//            }
//
//            File file = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS), "sales_report1.xlsx");
//
//            try (FileOutputStream fileOut = new FileOutputStream(file)) {
//                workbook.write(fileOut);
//                runOnUiThread(() -> {
//                    showAlert("Xuất Excel thành công tại: " + file.getAbsolutePath());
//                    Toast toast = Toast.makeText(this, "Xuất Excel thành công!", Toast.LENGTH_LONG);
//                    View view = toast.getView();
//                    TextView text = view.findViewById(android.R.id.message);
//                    text.setTextColor(Color.RED);
//                    text.setBackgroundColor(Color.YELLOW);
//                    toast.show();
//                });
//            }
//        } catch (Exception e) {
//            System.out.println("Debug message: " + e.getMessage());
//            e.printStackTrace();
//            runOnUiThread(() ->
//                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show()
//            );
//        }
//    }

    public void exportToExcel(ArrayList<BarEntry> weeklyEntries) {
        // Kiểm tra quyền ghi
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_CODE);
//            return;
//        }

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
                canvas.drawText("Tuần " + entry.getX() + ": " + entry.getY() + " cuốn", 50, yPosition, paint);
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