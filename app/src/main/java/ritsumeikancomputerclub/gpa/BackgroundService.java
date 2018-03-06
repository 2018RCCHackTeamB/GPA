package ritsumeikancomputerclub.gpa;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    // Fused Location Provider API.
    private FusedLocationProviderClient fusedLocationClient;

    // Location Settings APIs.
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private Boolean requestingLocationUpdates;
    private int priority = 0;

    private Timer timer = null;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        priority = 0;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private int i;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        i = 0;
        double testLati = 34.9800554;
        double testLong = 135.9627596;

        startLocationUpdates();
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                i++;
                Log.d("location", "latitude: "+location.getLatitude()+"  longitude: "+location.getLongitude());
                Log.d("distance", String.valueOf(getDistance(location.getLatitude(), location.getLongitude(), testLati, testLong)));
                if(i > 10){
                    stopLocationUpdates();
                    startActivity(new Intent(getApplicationContext(), AlarmActivity.class));
                    stopService(new Intent(getApplicationContext(), BackgroundService.class));
                }
            }
        }, 10000, 10000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    // locationのコールバックを受け取る
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        if (priority == 0) {
            // 高い精度の位置情報を取得したい場合
            // インターバルを例えば5000msecに設定すれば
            // マップアプリのようなリアルタイム測位となる
            // 主に精度重視のためGPSが優先的に使われる
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        } else if (priority == 1) {
            // バッテリー消費を抑えたい場合、精度は100mと悪くなる
            // 主にwifi,電話網での位置情報が主となる
            // この設定の例としては　setInterval(1時間)、setFastestInterval(1分)
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        } else if (priority == 2) {
            // バッテリー消費を抑えたい場合、精度は10kmと悪くなる
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        } else {
            // 受け身的な位置情報取得でアプリが自ら測位せず、
            // 他のアプリで得られた位置情報は入手できる
            locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        }

        // アップデートのインターバル期間設定
        // このインターバルは測位データがない場合はアップデートしません
        // また状況によってはこの時間よりも長くなることもあり
        // 必ずしも正確な時間ではありません
        // 他に同様のアプリが短いインターバルでアップデートしていると
        // それに影響されインターバルが短くなることがあります。
        // 単位：msec
        locationRequest.setInterval(60000);
        // このインターバル時間は正確です。これより早いアップデートはしません。
        // 単位：msec
        locationRequest.setFastestInterval(60000);

    }

    // 端末で測位できる状態か確認する。wifi, GPSなどがOffになっているとエラー情報のダイアログが出る
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    // FusedLocationApiによるlocation updatesをリクエスト
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener((LocationSettingsResponse locationSettingsResponse) -> {
            Log.i("debug", "All location settings are satisfied.");

            // パーミッションの確認
            if (ActivityCompat.checkSelfPermission(BackgroundService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(BackgroundService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }).addOnFailureListener((@NonNull Exception e) -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i("debug", "Location settings are not satisfied. Attempting to upgrade " + "location settings ");
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    String errorMessage = "Location settings are inadequate, and cannot be " +
                            "fixed here. Fix in Settings.";
                    Log.e("debug", errorMessage);
                    Toast.makeText(BackgroundService.this, errorMessage, Toast.LENGTH_LONG).show();

                    requestingLocationUpdates = false;
            }
        });

        requestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            Log.d("debug", "stopLocationUpdates: " + "updates never requested, no-op.");

            return;
        }

        fusedLocationClient.removeLocationUpdates(locationCallback).addOnCompleteListener((@NonNull Task<Void> task) -> requestingLocationUpdates = false);
    }

    private double getDistance(double lati1, double long1, double lati2, double long2){
        double radius = 6378137;
        double latiDelta = Math.abs(lati1 - lati2) * (Math.PI/180);
        double longDelta = ((long1 - long2 <= 180) ? Math.abs(long1 - long2) : Math.abs(2*Math.PI - (long1 - long2))) * (Math.PI/180);

        double theta = 2 * Math.asin(Math.sqrt(Math.sin(latiDelta/2)*Math.sin(latiDelta/2) + Math.cos(lati1)*Math.cos(lati2)*Math.sin(longDelta/2)*Math.sin(longDelta/2)));

        return radius*theta;
    }
}