package com.example.weatherwidget.view.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.weatherwidget.R;
import com.example.weatherwidget.databinding.ActivityCalendarBinding;
import com.example.weatherwidget.model.middle_temperature_response.MiddleTemperatureResponse;
import com.example.weatherwidget.task.Newsky2Api;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarActivity extends AppCompatActivity {

    private CalendarAdapter mCalendarAdapter = null;
    private ActivityCalendarBinding mActivityCalendarBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityCalendarBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);
        mActivityCalendarBinding.setCalendarActivity(this);

        /*
            Status Bar(휴대폰 최상단에 시계나오는 그부분 투명하게 만들기)
         */
        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));

        /*
            http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey=TD5T1EapjYVT5OS2BqqOJOwmKvb94OKVrALkhuUHYk6kblyjbcZQUe7AJFYevD3Sa711uDEU1AifpJPzZJQgUQ%3D%3D&_type=json 를 통해

            중기 날씨 정보를 가져옵니다
         */
        final Calendar calendar = Calendar.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://newsky2.kma.go.kr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Newsky2Api service = retrofit.create(Newsky2Api.class);
        Call<MiddleTemperatureResponse> call = service.getMiddleTemperature(
            "11B10101",
                new SimpleDateFormat("yyyyMMdd0600").format(calendar.getTime()));

        call.enqueue(new Callback<MiddleTemperatureResponse>() {
            @Override
            public void onResponse(Call<MiddleTemperatureResponse> call, Response<MiddleTemperatureResponse> response) {
                if (response.isSuccessful()) {
                    mCalendarAdapter = new CalendarAdapter(response.body());

                    /*
                        중기날씨 정보 획득 성공시 RecyclerView를 업데이트 합니다
                     */
                    mActivityCalendarBinding.recyclerView.setAdapter(mCalendarAdapter);
                    mActivityCalendarBinding.recyclerView.setLayoutManager(new LinearLayoutManager(CalendarActivity.this, RecyclerView.VERTICAL, false));

                    mActivityCalendarBinding.relativeLayoutCover.setVisibility(View.GONE);
                } else {
                    Toast.makeText(CalendarActivity.this, "중기예보 불러오기 실패:MsrstnAcctoRltmMesureDnsty" + response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MiddleTemperatureResponse> call, Throwable throwable) {
                Toast.makeText(CalendarActivity.this, "중기예보 불러오기 실패:MsrstnAcctoRltmMesureDnsty" + throwable.getMessage(), Toast.LENGTH_SHORT);
                finish();
            }
        });
    }
}
