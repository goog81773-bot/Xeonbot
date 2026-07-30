package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private Button btnActivate;
    private TextView tvStatus;
    
    // قائمة الأذونات المطلوبة
    private String[] permissions = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.POST_NOTIFICATIONS
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnActivate = findViewById(R.id.btnActivate);
        tvStatus = findViewById(R.id.tvStatus);
        
        // التحقق من الأذونات
        if (hasPermissions()) {
            tvStatus.setText("✅ الأذونات ممنوحة");
            btnActivate.setEnabled(true);
        } else {
            tvStatus.setText("⚠️ تحتاج إلى منح الأذونات");
            requestPermissions();
        }
        
        btnActivate.setOnClickListener(v -> {
            if (hasPermissions()) {
                startBackgroundService();
                tvStatus.setText("🔄 التطبيق يعمل في الخلفية");
                btnActivate.setText("✅ مفعل");
                btnActivate.setEnabled(false);
                Toast.makeText(this, "تم تفعيل الخدمة الخلفية", Toast.LENGTH_LONG).show();
            } else {
                requestPermissions();
            }
        });
    }
    
    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                tvStatus.setText("✅ الأذونات ممنوحة");
                btnActivate.setEnabled(true);
                Toast.makeText(this, "تم منح جميع الأذونات", Toast.LENGTH_SHORT).show();
            } else {
                tvStatus.setText("❌ بعض الأذونات غير ممنوحة");
                Toast.makeText(this, "يرجى منح جميع الأذونات", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void startBackgroundService() {
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
                                   }
