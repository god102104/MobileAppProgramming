package com.example.weatherwidget.view.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherwidget.R;
import com.example.weatherwidget.databinding.RecyclerViewItemCalendarBinding;
import com.example.weatherwidget.model.CalendarItem;
import com.example.weatherwidget.model.middle_temperature_response.MiddleTemperatureResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private ArrayList<CalendarItem> mfCalendarItems;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerViewItemCalendarBinding mfBinding;

        public ViewHolder(@NonNull RecyclerViewItemCalendarBinding binding) {
            super(binding.getRoot());
            mfBinding = binding;
        }

        public void bind(final CalendarItem calendarItem) {
            mfBinding.setCalendarItem(calendarItem);

            /*
                온도에 따라 의상을 바꿔줍니다
             */
            int temperature = calendarItem.getMinTemperatureAsInteger();
            if (28 <= temperature) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_sleeveless);
            } else if ((23 <= temperature) && (temperature < 28)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_shirt);
            } else if ((20 <= temperature) && (temperature < 23)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_cardigan);
            } else if ((17 <= temperature) && (temperature < 20)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_sweater);
            } else if ((12 <= temperature) && (temperature < 17)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_jacket);
            } else if ((9 <= temperature) && (temperature < 12)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_trench_coat);
            } else if ((5 <= temperature) && (temperature < 9)) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_coat);
            } else if (temperature < 5) {
                mfBinding.appCompatImageView.setImageResource(R.drawable.symbol_overcoat);
            } else {
                ;
            }
        }
    }

    public CalendarAdapter(MiddleTemperatureResponse middleTemperatureResponse) {
        mfCalendarItems = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

        /*
            CalendarActivity에서 획득한 중기예보 정보를 가지고 CalendarItem들을 생성 합니다
         */
        if (middleTemperatureResponse.getResponse().getBody().getTotalCount() > 0) {
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin3(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax3());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin4(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax4());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin5(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax5());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin6(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax6());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin7(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax7());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin8(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax8());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin9(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax9());
            createCalendarItem(calendar, dateFormat, middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMin10(), middleTemperatureResponse.getResponse().getBody().getItems().getItem().getTaMax10());
        }
    }

    /*
        CalendarActivity에서 획득한 중기예보 정보를 가지고 CalendarItem을 생성 합니다
     */
    private void createCalendarItem(Calendar calendar, SimpleDateFormat dateFormat, Integer minTemperature, Integer maxTemperature) {
        mfCalendarItems.add(new CalendarItem(
                dateFormat.format(calendar.getTime()),
                minTemperature + "",
                maxTemperature + ""));
        calendar.add(Calendar.DATE, 1);
    }

    /*
        CalendarItem를 표현할 뷰를 정의함
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CalendarAdapter.ViewHolder(
                RecyclerViewItemCalendarBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false));
    }

    /*
        인덱스에 해당하는 CalendarItem를 리턴
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mfCalendarItems.get(position));
    }

    /*
        총 CalendarItem의 갯수를 리턴
     */
    @Override
    public int getItemCount() {
        return mfCalendarItems.size();
    }
}
