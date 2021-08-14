package jp.javadrive.openweatherapifragmentsqlite;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class LocalAreaParentFragment extends Fragment {

    private String time;
    private String pop;
    private String weather;
    private String Icon;
    private String temp;
    static LocalAreaParentFragment newInstance(
            String areaname, int cnt) {
        // インスタンス生成
        LocalAreaParentFragment localAreaParentfragment = new LocalAreaParentFragment();
        // Bundleにパラメータを設定
        Bundle barg = new Bundle();
        barg.putString("areaname", areaname);
        barg.putInt("cnt", cnt);
        localAreaParentfragment.setArguments(barg);

        return localAreaParentfragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_current,
                container, false);

        TextView time1 = (TextView) view.findViewById(R.id.AreacurrentTime);
        TextView main = (TextView) view.findViewById(R.id.AreacurrentWeather);
        ImageView myImage= view.findViewById(R.id.AreacurrentIcon);
        TextView temp1 = (TextView) view.findViewById(R.id.AreacurrentTemp);

        TextView title = (TextView) view.findViewById(R.id.title);


        // Bundleにパラメータを取得
        Bundle args = getArguments();

        assert args != null;
        String areaid = args.getString("areaid");
        String areaname = args.getString("areaname");
        Double lon = args.getDouble("lon");
        Double lat = args.getDouble("lat");
        int cnt = args.getInt("cnt");

        title.setText(areaname + "の天気");

        // メイン(UI)スレッドでHandlerのインスタンスを生成する
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // 天気予報XMLデータ
                    URL url = new URL("http://10.0.2.2:8080/weather/data?cityname=" + areaname);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    String str = InputStreamToString(con.getInputStream());

                    Log.d("HTTP", str);

                    try {
                        JSONObject json = new JSONObject(str);
                        //時間をセット
                        String status = "";
                        String message = "";
                        try {
                            status = json.getString("status");
                            message = json.getString("message");
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("status", status);
                        Log.d("message", message);

                        if (status.equals("1")) {
                            JSONArray jsonArray = json.getJSONArray("weatherlist");

                            Log.d("cnt", String.valueOf(cnt));
                            for (int i = jsonArray.length()-cnt-1; i < jsonArray.length()-cnt; i++) {
                                //1日ごとにデータを取得する
                                JSONObject dayjson = new JSONObject(jsonArray.getString(i));

                                //時間をセット
                                time = dayjson.getString("day");
                                Log.d("time", time);
                                //weather(天気)の取り出し・セット
                                weather = dayjson.getString("weather");
                                Log.d("time", weather);
                                //tempの取り出し
                                temp = String.valueOf(Double.valueOf(dayjson.getString("temp")));
                                //小数点第二位を四捨五入
                                double doubletemp = Double.parseDouble(temp);
                                temp = String.format("%.1f", doubletemp);
                                Log.d("time", temp);
                                //アイコンの取り出し
                                Icon = dayjson.getString("icon");
                                Log.d("time", Icon);

                                handler.post(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void run() {

                                        time1.setText(time + "の天気");
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


                            }
                        } else {
                            String finalmessage = message;
                            handler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    String finalMessage = finalmessage;
                                    Optional.ofNullable(getActivity())
                                            .filter(activity -> activity instanceof LocalAreaWeekFragment.OnCurrentListener)
                                            .map(activity -> (LocalAreaWeekFragment.OnCurrentListener) activity)
                                            .orElseThrow(() -> new IllegalStateException("ActivityにOnListenerを実装してください"))
                                            .onDaialog(finalMessage);
                                }
                            });
                        }
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

