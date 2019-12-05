package com.example.weatherwidget.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weatherwidget.R;
import com.example.weatherwidget.databinding.ActivityMainBinding;
import com.example.weatherwidget.databinding.ActivityShopBinding;
import com.example.weatherwidget.model.kakao_place.Document;
import com.example.weatherwidget.model.kakao_place.KakaoPlace;
import com.example.weatherwidget.model.msrstn_accto_rltm_mesure_dnsty.MsrstnAcctoRltmMesureDnsty;
import com.example.weatherwidget.model.sgis_transcoord.SgisTranscoord;
import com.example.weatherwidget.task.AirkoreaApi;
import com.example.weatherwidget.task.KakaoApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap = null;
    private ActivityShopBinding mActivityShopBinding = null;

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

            /*
                https://dapi.kakao.com/v2/local/search/category.json 을 통해

                현재 위치 4km 이내의 편의점, 약국 정보를 얻어옵니다
             */
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://dapi.kakao.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            KakaoApi service = retrofit.create(KakaoApi.class);

            loadCS2(service, locationResult.getLastLocation().getLatitude() + "", locationResult.getLastLocation().getLongitude() + "");
        }
    };

    private void loadCS2(final KakaoApi service, final String latitude, final String longitude) {
        Call<KakaoPlace> call = service.category(
                "CS2",
                longitude,
                latitude,
                4000);

        call.enqueue(new Callback<KakaoPlace>() {
            @Override
            public void onResponse(Call<KakaoPlace> call, Response<KakaoPlace> response) {
                if (response.isSuccessful()) {
                    /*
                        편의점 정보 얻기 성공
                     */
                    loadPM9(service, latitude, longitude, response.body());
                } else {
                    /*
                        편의점 정보 얻기 실패
                     */
                    loadPM9(service, latitude, longitude, null);
                }
            }

            @Override
            public void onFailure(Call<KakaoPlace> call, Throwable throwable) {
                /*
                    편의점 정보 얻기 실패
                 */
                loadPM9(service, latitude, longitude, null);
            }
        });
    }

    private void loadPM9(KakaoApi service, String latitude, String longitude, final KakaoPlace kakaoPlaceFromCS2) {
        Call<KakaoPlace> call = service.category(
                "PM9",
                longitude,
                latitude,
                4000);

        call.enqueue(new Callback<KakaoPlace>() {
            @Override
            public void onResponse(Call<KakaoPlace> call, Response<KakaoPlace> response) {
                if (response.isSuccessful()) {
                    /*
                        약국 정보 얻기 성공
                     */
                    postLocationCallback(kakaoPlaceFromCS2, response.body());
                } else {
                    /*
                        약국 정보 얻기 실패
                     */
                    postLocationCallback(kakaoPlaceFromCS2, null);
                }
            }

            @Override
            public void onFailure(Call<KakaoPlace> call, Throwable throwable) {
                /*
                    약국 정보 얻기 실패
                 */
                postLocationCallback(kakaoPlaceFromCS2, null);
            }
        });
    }

    private void postLocationCallback(KakaoPlace kakaoPlaceFromCS2, KakaoPlace kakaoPlaceFromPM9) {
        mGoogleMap.clear();

        /*
            지도에 편의점 정보를 마커를 이용하여 추가합니다
         */
        if (kakaoPlaceFromCS2 != null) {
            for (Document document : kakaoPlaceFromCS2.getDocuments()) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(document.getY()), Double.parseDouble(document.getX())))
                        .title(document.getPlaceName())
                );
            }
        }

        /*
            지도에 약국 정보를 마커를 이용하여 추가합니다
         */
        if (kakaoPlaceFromPM9 != null) {
            for (Document document : kakaoPlaceFromPM9.getDocuments()) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(document.getY()), Double.parseDouble(document.getX())))
                        .title(document.getPlaceName())
                );
            }
        }

        mActivityShopBinding.relativeLayoutCover.setVisibility(View.GONE);
        mActivityShopBinding.floatingActionButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityShopBinding = DataBindingUtil.setContentView(this, R.layout.activity_shop);
        mActivityShopBinding.setShopActivity(this);

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
        mActivityShopBinding.relativeLayoutCover.setVisibility(View.VISIBLE);
        mActivityShopBinding.floatingActionButton.setVisibility(View.GONE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(new LocationRequest(), mfLocationCallback,null);
    }
}
