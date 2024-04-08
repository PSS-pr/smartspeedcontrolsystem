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
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class LocationUtils {
    private static final String TAG = "LocationUtils";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static void getCurrentLocation(final Activity activity) {
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
            getLocation(activity);
        }
    }

    private static void getLocation(final Activity activity) {
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
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // 위치 정보를 얻었을 때의 동작
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d(TAG, "onLocationChanged: " + latitude + ", " + longitude);

                        // MainActivity로 좌표를 보내기 위한 인텐트 생성
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);

                        // MainActivity 시작
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {
                        // GPS가 비활성화된 경우, 사용자에게 알림 표시
                    }
                }, null);
            }
        }
    }
}
