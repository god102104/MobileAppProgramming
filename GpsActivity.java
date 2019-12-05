package com.example.weatherwidget.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.weatherwidget.R;
import com.example.weatherwidget.databinding.ActivityGpsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GpsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mGoogleMap = null;
    private ActivityGpsBinding mActivityGpsBinding = null;

    private FusedLocationProviderClient mFusedLocationClient = null;
    private final LocationCallback mfLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            /*
                FusedLocationProviderClient의 requestLocationUpdates는 위치 변경시 지속적으로
                현재 메서드를 호출합니다.

                하지만 우리는 딱 한번만 현재위치가 필요하기 때문에 callback을 제거해줍니다
             */
            mFusedLocationClient.removeLocationUpdates(mfLocationCallback);

            /*
                현재 위치로 맵 위치를 이동합니다
             */
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), 14));

            mActivityGpsBinding.relativeLayoutCover.setVisibility(View.GONE);
            mActivityGpsBinding.floatingActionButton.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityGpsBinding = DataBindingUtil.setContentView(this, R.layout.activity_gps);
        mActivityGpsBinding.setGpsActivity(this);

        /*
            구글 맵을 초기화 합니다.

            초기화 완료돼면 onMapReady 메서드 호출
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLongClickListener(this);

        /*
            FusedLocationProviderClient로 현재 위치를 획득 요청합니다

            현재위치를 얻으면 mfLocationCallback의 onLocationResult 호출
         */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(new LocationRequest(), mfLocationCallback,null);
    }

    /*
        앱 우하단의 현재위치 버튼을 누르면 호출

        FusedLocationProviderClient로 현재 위치를 획득 요청합니다

        현재위치를 얻으면 mfLocationCallback의 onLocationResult 호출
     */
    public void onClickMyLocation() {
        mActivityGpsBinding.relativeLayoutCover.setVisibility(View.VISIBLE);
        mActivityGpsBinding.floatingActionButton.setVisibility(View.GONE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(new LocationRequest(), mfLocationCallback,null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /*
            지도의 한점을 꾹 누르면, 해당위치를 MainActivity로(OnActivityResult로) 보낸후 현재 액티비티를 종료합니다
         */
        setResult(RESULT_OK, new Intent().putExtra("latitude", latLng.latitude).putExtra("longitude", latLng.longitude));
        finish();
    }
}
