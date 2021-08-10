package jp.javadrive.openweatherapifragmentsqlite;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalAreaCurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalAreaCurrentFragment extends Fragment {

    private String dt;
    private String weather;
    private String Icon;
    private String temp;
    static LocalAreaCurrentFragment newInstance(
            String areaname,int cnt,Double lon, Double lat) {
        // インスタンス生成
        LocalAreaCurrentFragment localAreaCurrentfragment = new LocalAreaCurrentFragment();
        // Bundleにパラメータを設定
        Bundle barg = new Bundle();
        barg.putString("areaname", areaname);
        barg.putInt("cnt", cnt);
        barg.putDouble("lon", lon);
        barg.putDouble("lat", lat);
        localAreaCurrentfragment.setArguments(barg);

        return localAreaCurrentfragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_current,
                container, false);

        TextView day1 = (TextView) view.findViewById(R.id.AreacurrentDay);
        TextView time1 = (TextView) view.findViewById(R.id.AreacurrentTime);
        TextView main = (TextView) view.findViewById(R.id.AreacurrentWeather);
        ImageView myImage= view.findViewById(R.id.AreacurrentIcon);
        TextView temp1 = (TextView) view.findViewById(R.id.AreacurrentTemp);

        TextView title = (TextView) view.findViewById(R.id.title);


        // Bundleにパラメータを取得
        Bundle args = getArguments();
        assert args != null;
        String areaname = args.getString("areaname");
        int cnt = args.getInt("cnt");
        Double lon = args.getDouble("lon");
        Double lat = args.getDouble("lat");
        title.setText(areaname + "の天気");

        Log.v("緯度", String.valueOf(lat));
        Log.v("経度", String.valueOf(lon));

        // メイン(UI)スレッドでHandlerのインスタンスを生成する
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // 天気予報XMLデータ
                    URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                            "&lon=" + lon +
                            "&lang=ja&units=metric&appid=88ffedd13af1bf272241ec39f417b2f5");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    String str = InputStreamToString(con.getInputStream());

                    Log.d("HTTP", str);

                    try {
                        JSONObject json = new JSONObject(str);

                        JSONArray jsonArray = json.getJSONArray("weather");
                        JSONObject weatherjson = new JSONObject(jsonArray.getString(0));
                            JSONObject mainjson = json.getJSONObject("main");



                                //日付をセット
                                dt = json.getString("dt");
                                Log.d("dt", dt);

                                Long dt1 = Long.valueOf(dt);

                        Date date = new Date(dt1 * 1000);
                        TimeZone timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo");
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy年M月d日");
                        SimpleDateFormat fmt1 = new SimpleDateFormat("H時m分");
                        fmt.setTimeZone(timeZoneJP);
                        fmt1.setTimeZone(timeZoneJP);
                        String time0 = fmt.format(date);//day用
                        String time2 = fmt1.format(date);//time用


                                //weather(天気)の取り出し・セット
                                weather = weatherjson.getString("description");
                                Log.d("weather", weather);
                                //tempの取り出し
                                temp = String.valueOf(Double.valueOf(mainjson.getString("temp")));
                                //小数点第二位を四捨五入
                                double doubletemp = Double.parseDouble(temp);
                                temp = String.format("%.1f", doubletemp);
                                Log.d("temp", temp);
                                //アイコンの取り出し
                                Icon = weatherjson.getString("icon");
                                Log.d("icon", Icon);

                                handler.post(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void run() {

                                        day1.setText(time0);
                                        time1.setText(time2);
                                        main.setText("天気の種類："+weather);
                                        temp1.setText("平均気温："+temp+"℃");

                                        switch (Icon){

                                            case "01d" :
                                                myImage.setImageResource(R.drawable._1d);
                                                break;

                                            case "01n" :
                                                myImage.setImageResource(R.drawable._01n);
                                                break;

                                            case "02d":
                                                myImage.setImageResource(R.drawable._2d);
                                                break;

                                            case "02n" :
                                                myImage.setImageResource(R.drawable._02n);
                                                break;

                                            case "03d":
                                                myImage.setImageResource(R.drawable._3d);
                                                break;

                                            case "03n" :
                                                myImage.setImageResource(R.drawable._03n);
                                                break;

                                            case "04d":
                                                myImage.setImageResource(R.drawable._4d);
                                                break;

                                            case "04n" :
                                                myImage.setImageResource(R.drawable._04n);
                                                break;

                                            case "09d":
                                                myImage.setImageResource(R.drawable._9d);
                                                break;

                                            case "09n" :
                                                myImage.setImageResource(R.drawable._9n);
                                                break;

                                            case "10d":
                                                myImage.setImageResource(R.drawable._10d);
                                                break;

                                            case "10n" :
                                                myImage.setImageResource(R.drawable._10n);
                                                break;

                                            case "11d":
                                                myImage.setImageResource(R.drawable._11d);
                                                break;

                                            case "11n" :
                                                myImage.setImageResource(R.drawable._11n);
                                                break;

                                            case "13d":
                                                myImage.setImageResource(R.drawable._13d);
                                                break;

                                            case "13n" :
                                                myImage.setImageResource(R.drawable._13n);
                                                break;

                                            case "50d":
                                                myImage.setImageResource(R.drawable._50d);
                                                break;

                                            case "50n" :
                                                myImage.setImageResource(R.drawable._50n);
                                                break;


                                        }



                                    }
                                });

                    }catch (JSONException e) {
                        handler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                String finalMessage = "データがありません";
                                Optional.ofNullable(getActivity())
                                        .filter(activity -> activity instanceof LocalAreaDataFragment.OnCurrentListener)
                                        .map(activity -> (LocalAreaDataFragment.OnCurrentListener) activity)
                                        .orElseThrow(() -> new IllegalStateException("ActivityにOnListenerを実装してください"))
                                        .onDaialog(finalMessage);
                            }
                        });
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }


        }).start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bundleにパラメータを取得
        Bundle args = getArguments();
        assert args != null;
        String areaname = args.getString("areaname");


    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = bufReader.readLine()) != null) {
            response.append(line);
        }
        bufReader.close();


        return response.toString();
    }
    public interface OnCurrentListener {
        void onDaialog(String message);
    }


}

