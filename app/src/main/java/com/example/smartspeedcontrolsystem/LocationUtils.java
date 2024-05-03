package com.example.smartspeedcontrolsystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class LocationUtils {
    private static final String TAG = "LocationUtils";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static void getCurrentLocation(final Activity activity, final LocationListener locationListener) {
        Log.d(TAG, "getCurrentLocation: ");

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우, 사용자에게 권한 요청
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // 권한이 있는 경우, 위치 정보 가져오기
            getLocation(activity, locationListener);
        }
    }

    private static void getLocation(final Activity activity, final LocationListener locationListener) {
        // 위치 서비스 관리자 객체 가져오기
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // 위치 업데이트 요청
        if (locationManager != null) {
            // GPS 설정이 활성화되어 있는지 확인
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS가 비활성화되어 있는 경우, 사용자에게 알림 표시
                Toast.makeText(activity, "Please enable GPS", Toast.LENGTH_SHORT).show();
            } else {
                // GPS가 활성화되어 있는 경우, 위치 업데이트 요청
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}

