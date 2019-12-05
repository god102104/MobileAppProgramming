package com.example.weatherwidget.view.clock;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weatherwidget.R;

import java.text.SimpleDateFormat;
import java.util.Date;
/***
    알람이 자동해서 오늘 하루 날씨 정보를 얻고 비가 오는지 , 맑은지 , 흐림는지를 판단해서 ,미리 준비된 음악을 재생한다
 */

public class ClockActivity extends AppCompatActivity {
    LatLngToXY latlngtoxy = new LatLngToXY();
    LocationManager locationManager;
    Location location;
    double lat;
    double lng;
    MediaPlayer mediaPlayer;
    int musicid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        getLocation();
        latlngtoxy.method(lat,lng);
        Weather_today ts;
        ts = new Weather_today();
        ts.getMsg((int)latlngtoxy.tmp.x,(int)latlngtoxy.tmp.y);
        try {
            while(ts.staus) {
                Thread.sleep(500);
            }
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        musicid = ts.music_playing_id;
        switch (musicid)
        {
            case 1:
                mediaPlayer = mediaPlayer.create(this, R.raw.alive_test4);
                mediaPlayer.start();
                break;
            case 2:
                mediaPlayer = mediaPlayer.create(this, R.raw.alive_test1);
                mediaPlayer.start();
                break;
            case 3:
                mediaPlayer = mediaPlayer.create(this, R.raw.alive_test6);
                mediaPlayer.start();
                break;
            case 4:
                mediaPlayer = mediaPlayer.create(this, R.raw.alive_test4);
                mediaPlayer.start();
                break;

        }

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년MM월dd일 HH:mm:ss");
        new AlertDialog.Builder(ClockActivity.this).setTitle("WeatherWidget").setMessage(formatter.format(date))
                .setPositiveButton("turn-off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        ClockActivity.this.finish();
                    }
                }).show();
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null)
        {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

    }
    }

