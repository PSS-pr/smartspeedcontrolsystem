package com.example.smartspeedcontrolsystem;

import androidx.appcompat.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private int num = 1;
    private GoogleMap mMap;
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private double currentLatitude, currentLongitude;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 권한 확인 및 요청
        checkLocationPermission();

        // CSV 파일에서 주소 읽어오기
        readCsvFile();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // 위치 권한이 이미 부여된 경우 현재 위치 가져오기
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 부여된 경우 현재 위치 가져오기
                getCurrentLocation();
            } else {
                // 위치 권한이 거부된 경우 사용자에게 알림을 보여줄 수 있습니다.
                // 이 예제에서는 간단히 로그를 출력합니다.
                Log.e(TAG, "Location permission denied");
            }
        }
    }

    private void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception: " + e.getMessage());
        }
    }

    private void readCsvFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) { // 무한 루프
                    InputStream inputStream = getResources().openRawResource(R.raw.combined); // CSV 파일 리소스 가져오기
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        // 첫 번째 행(헤더)을 읽어서 버림
                        reader.readLine();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] data = line.split(","); // CSV 행을 쉼표(,)로 분리하여 데이터 추출
                            if (data.length >= 4) { // 최소한 주소 정보가 있어야 함
                                String address = data[2]; // 주소 추출
                                LatLng location = getLocationFromAddress(address);
                                if (location != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            calculateDistance(location.latitude, location.longitude);
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading CSV file: " + e.getMessage());
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Error closing InputStream: " + e.getMessage());
                        }
                    }
                    // 파일의 끝에 도달하면 다시 처음부터 읽기 위해 스레드를 잠시 일시 정지합니다.
                    try {
                        Thread.sleep(1000); // 적절한 시간 간격을 설정하여 파일을 다시 읽을 수 있도록 합니다.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        LatLng location = null;
        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                location = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting location from address: " + e.getMessage());
        }
        return location;
    }

    private void addMarker(LatLng location) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)); // 마커가 있는 위치로 카메라 이동
        }
    }

    private void calculateDistance(double latitude, double longitude) {
//        double EARTH_R = 6371000.0;
//        double Rad = Math.PI / 180;
//        double radLat1 = Rad * currentLatitude;
//        double radLat2 = Rad * latitude;
//        double radDist = Rad * (currentLongitude - longitude);

        double EARTH_R = 6371000.0;
        double Rad = Math.PI / 180;
        double radLat1 = Rad * 35.169472;
        double radLat2 = Rad * latitude;
        double radDist = Rad * (128.995720 - longitude);

        double distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        double ret = EARTH_R * Math.acos(distance);
        double resultInMeters = Math.round(ret);

        Log.d("Distance", "Distance: " + resultInMeters + " meters" + "||"+num);
        LatLng location = new LatLng(latitude, longitude);

        if(resultInMeters <= 2500) {
            addMarker(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
