package com.example.weatherwidget.view.clock;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/***
 * 동네에보 Api 에서 하루종일 날씨 정보를 얻고 날씨 정보를 분석해서
 * 오늘 비가 오는지 , 맑은지 , 흐림는지를 판단한다
 */
public class Weather_today {

    public int time_now;
    public int time_data;
    public  int year;
    public  int count = 0;
    public  boolean rainfall = false;  // 강수확률>= 60% ?
    public  int sunny_day = 0; // 맑음 개수 개산
    public  int partly_cloudy= 0;//구름많음 개수 개산
    public   int cloudy_day = 0; //흐림 개수 개산
    public  int thunder_shower = 0;
    public   int rain = 0;
    public int music_playing_id = 0;
    public boolean staus = true;       //인터넷에서 정보를 얻은지 판단.   true듯은 아직 앋지 않다
    ArrayList<today_inf> inf = new ArrayList<today_inf>();
    private class item_today {
        int baseData;
        int baseTime;
        int fcstTime;
        int nx;
        int ny;
        String category;
        Double fcstValue;

    }

    public  class today_inf{

        int fcstTime;
        int baseData = 0;
        int baseTime = 0;
        int nx = 0;
        int ny = 0;
        double pop = 0;
        double pty = 0;
        double r06 = 0;
        double reh = 0;
        double s06 = 0;
        double sky = 0;
        double t3h = 0;
        double tmn = 0;
        double tmx = 0;
        double uuu = 0;
        double vvv = 0;
        double wav = 0;
        double vec = 0;
        double wsd = 0;
    }



    public Weather_today() {

    }

    public void getMsg(int x, int y) {
        final int lat = x;
        final int ln = y;
        Runnable thread_weather = new Runnable() {
            @Override
            public void run() {
                try {
                    Calendar c = Calendar.getInstance();
                    if(c.get(Calendar.HOUR_OF_DAY)>2)
                    {
                        time_now = (c.get(Calendar.HOUR_OF_DAY) - 3) * 100;
                        time_data = (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
                    }
                    else
                   {
                        time_now = 2300;
                        time_data = (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH)-1;
                   }
                    year = c.get(Calendar.YEAR);
/***
 * 기상청 API 주석 예시
 * http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=gq58KoM1h5D7RsudrAQNkZljhfnO2T%2B2y0EbvrhAUgCahFcDKWSS5oQrvv8NKtFUq3bawkRqsxjfwPyLtcsy2w%3D%3D&base_date=20191114&base_time=0800&nx=89&ny=91&numOfRows=999&pageNo=1&_type=json
 */
                    StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); /*URL*/
                    urlBuilder.append("?" + "ServiceKey=gq58KoM1h5D7RsudrAQNkZljhfnO2T%2B2y0EbvrhAUgCahFcDKWSS5oQrvv8NKtFUq3bawkRqsxjfwPyLtcsy2w%3D%3D");
                    if (time_now >= 1000)
                        urlBuilder.append("&" + "base_time=" + String.valueOf(time_now));
                    else
                        urlBuilder.append("&" + "base_time=" + "0" + String.valueOf(time_now));
                    if (time_data >= 1000)
                        urlBuilder.append("&" + "base_date=" + String.valueOf(year) + String.valueOf(time_data));
                    else
                        urlBuilder.append("&" + "base_date=" + "0" + String.valueOf(year) + String.valueOf(time_data));
                    urlBuilder.append("&" + "nx=" + String.valueOf(lat));
                    urlBuilder.append("&" + "ny=" + String.valueOf(ln));
                    urlBuilder.append("&" + "numOfRows=999");
                    urlBuilder.append("&" + "pageNo=1");
                    urlBuilder.append("&" + "_type=json");
                      Log.i("URL",urlBuilder.toString());
                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                    Log.i("web_inf",sb.toString());
                    json_to_string(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(thread_weather).start();
    }

    private void json_to_string(String json) {
        try {
            inf.add(new today_inf());
            JSONObject jsob = new JSONObject(json);
            JSONObject response = jsob.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");
            int i;
            Weather_today.item_today[] today = new Weather_today.item_today[999];
            for (i = 0; i < item.length(); i++) {
                today[i] = new Weather_today.item_today();
            }
            for (i = 0; i < item.length(); i++) {
                JSONObject temp = item.getJSONObject(i);
                today[i].baseData = today[i].baseData = temp.getInt("baseDate");
                today[i].baseTime = today[i].baseTime = temp.getInt("baseTime");
                today[i].fcstTime = temp.getInt("fcstTime");
                today[i].nx = today[i].nx = temp.getInt("nx");
                today[i].ny = today[i].ny = temp.getInt("ny");
                today[i].category = temp.getString("category");
                today[i].fcstValue = temp.getDouble("fcstValue");
                if(inf.get(count).fcstTime!=today[i].fcstTime)
                {
                    inf.add(new today_inf());
                    count++;
                    inf.get(count).fcstTime = today[i].fcstTime;
                }
                switch (today[i].category) {
                    case "POP":
                        inf.get(count).pop = today[i].fcstValue;
                        break;
                    case "PTY":
                        inf.get(count).pty = today[i].fcstValue;
                        break;
                    case "R06":
                        inf.get(count).r06 = today[i].fcstValue;
                        break;
                    case "REH":
                        inf.get(count).reh = today[i].fcstValue;
                        break;
                    case "S06":
                        inf.get(count).s06 = today[i].fcstValue;
                        break;
                    case "SKY":
                        inf.get(count).sky = today[i].fcstValue;
                        break;
                    case "T3H":
                        inf.get(count).t3h = today[i].fcstValue;
                        break;
                    case "TMN":
                        inf.get(count).tmn = today[i].fcstValue;
                        break;
                    case "TMX":
                        inf.get(count).tmx = today[i].fcstValue;
                        break;
                    case "UUU":
                        inf.get(count).uuu = today[i].fcstValue;
                        break;
                    case "VVV":
                        inf.get(count).vvv = today[i].fcstValue;
                        break;
                    case "WAV":
                        inf.get(count).wav = today[i].fcstValue;
                        break;
                    case "VEC":
                        inf.get(count).vec = today[i].fcstValue;
                        break;
                    case "WSD":
                        inf.get(count).wsd = today[i].fcstValue;
                        break;


                }
            }
            int h;
            for(h= 1 ; h <= count ;h++)
            {
                if(inf.get(h).pop>60)
                    rainfall = true;
                if(inf.get(h).sky== 1)
                    sunny_day++;
                if(inf.get(h).sky== 3)
                    partly_cloudy++;
                if(inf.get(h).sky== 4)
                    cloudy_day++;

            }
           Log.i("Weather_Inf",String.valueOf(rainfall)+"\n"+String.valueOf(sunny_day)+"\n"+String.valueOf(partly_cloudy)+"\n"+String.valueOf(cloudy_day)+"\n");
            if(rainfall)
                music_playing_id = 1;
            else
                if(sunny_day >= partly_cloudy && sunny_day >= cloudy_day )
                    music_playing_id = 2;
                else
                    if(partly_cloudy >= cloudy_day)
                        music_playing_id = 3;
                    else
                        music_playing_id = 4;

            staus = false;
            Log.i("Test",String.valueOf(music_playing_id));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
