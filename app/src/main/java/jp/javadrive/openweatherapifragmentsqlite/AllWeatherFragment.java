package jp.javadrive.openweatherapifragmentsqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_hour;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalAreaCurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AllWeatherFragment extends ListFragment {

    private HourAdapter adapter;

    static AllWeatherFragment newInstance(int cnt, String area,int cnt2) {
        // インスタンス生成
        AllWeatherFragment allWeatherfragment = new AllWeatherFragment();

        Bundle barg = new Bundle();
        barg.putInt("cnt", cnt);
        barg.putString("area", area);
        barg.putInt("cnt2", cnt2);
        allWeatherfragment.setArguments(barg);

        return allWeatherfragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_weather,
                container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
       //         .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);

        Bundle args = getArguments();

        assert args != null;
        int cnt = args.getInt("cnt");
        String area = args.getString("area");
        int cnt0 = args.getInt("cnt2");
        TextView titleText;

        titleText = view.findViewById(R.id.dateText);

        // メイン(UI)スレッドでHandlerのインスタンスを生成する
        final Handler handler = new Handler();

        adapter = new HourAdapter(getActivity());
        setListAdapter(adapter);

        final String[] time = {null};

        Weather_hourOpenHelper helper = new Weather_hourOpenHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();



        Date date1 = new Date();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        date1 = calendar1.getTime();
        SimpleDateFormat fmt1 = new SimpleDateFormat("yyyyMMdd");
        String time1 = fmt1.format(date1);
        Log.d("検索日付", time1);

         /*　クエリー(select)文
        　　idがCursorAdapterには必須なため、"rowid as _id"で代用
        　　limit "数字"で取得するデータ数を制限(nullで全部)　　*/
        Cursor c = db.query(table_weather_hour, new String[]{"rowid as _id", "cityname", "time", "day", "weather", "temps", "icon", "pop"}, "time=?",
                new String[]{time1}, null, null, null ,null);


        JSONArray resultSet = new JSONArray();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int totalColumn = c.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (c.getColumnName(i) != null) {
                    try {
                        rowObject.put(c.getColumnName(i),
                                c.getString(i));
                    } catch (Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            c.moveToNext();
        }
        String str = String.valueOf(resultSet);
        //Log.v("結果1", str);




        JSONObject dayjson = null;
        try {
            dayjson = new JSONObject(resultSet.getString(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            String time0 = dayjson.getString("time");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = sdFormat.parse(time0);
            SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy年MM月dd日");

            time[0] = fmt2.format(date);
            Log.d("昨日", time[0]);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        for (int j = 0; j < cnt; j++ ) {
                //1日ごとにデータを取得する

                titleText.setText(time[0] + "の" + area + "の天気");

                adapter.add(str, cnt, cnt0);
            }


        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    //spiinerの実装+spinnerの値を持った画面再表示
    @Override
    public void onResume() {
        super.onResume();

        // Bundleにパラメータを取得
        Bundle args = getArguments();

        assert args != null;

        int cnt = args.getInt("cnt");

        //spinnerインスタンスを取得
        Spinner weatherSpinner = (Spinner) getActivity().findViewById(R.id.weather_spinner);
        // ArrayAdapterインスタンスをResourceXMLから取得
        SpinnerAdapter adapterWeather = ArrayAdapter.createFromResource(
                getActivity(), R.array.arrays,
                android.R.layout.simple_spinner_item);
        // ドロップダウンリスト表示レイアウトを設定
        ((ArrayAdapter<?>) adapterWeather).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // adapterクラスをspinnerクラスに適用
        weatherSpinner.setAdapter(adapterWeather);

        final String[] item = new String[1];
        final int[] cnt1 = new int[1];
        final String[] area1 = new String[1];
        final int[] cnt2 = new int[1];
        weatherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択したアイテムを取得
                item[0] = (String) spinner.getSelectedItem();
                // ログで確認
                Log.v("spinner item", item[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {// アイテムを選択しなかったとき
            }
        });

        // 選択されているアイテムを取得
        item[0] = weatherSpinner.getSelectedItem().toString();

        //this.getView()はonCreateViewのview
        Button KakoButton = (Button) this.getView().findViewById(R.id.dataWeather);
        KakoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //選択された項目に対するcntを設定
                switch (item[0]) {
                    case "全国の天気": cnt1[0] = 47;area1[0] = "全国";cnt2[0] = 0;
                        break;
                    case "北海道・東北の天気": cnt1[0] = 7;area1[0] = "北海道・東北";cnt2[0] = 0;
                        break;
                    case "関東の天気": cnt1[0] = 7;area1[0] = "関東";cnt2[0] = 7;
                        break;
                    case "中部の天気": cnt1[0] = 9;area1[0] = "中部";cnt2[0] = 14;
                        break;
                    case "関西の天気": cnt1[0] = 7;area1[0] = "関西";cnt2[0] = 23;
                        break;
                    case "中国の天気": cnt1[0] = 5;area1[0] = "中国";cnt2[0] = 30;
                        break;
                    case "四国の天気": cnt1[0] = 4;area1[0] = "四国";cnt2[0] = 35;
                        break;
                    case "九州・沖縄の天気": cnt1[0] = 8;area1[0] = "九州・沖縄";cnt2[0] = 39;
                        break;
                }
                Log.v("過去の天気に対応するcnt:", String.valueOf(cnt1[0]));

                //選択されたcntを値として渡して、画面再表示
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    // BackStackを設定
                    fragmentTransaction.addToBackStack(null);
                    //県名、緯度、経度を引数で渡して画面遷移設定
                    fragmentTransaction.replace(R.id.container,
                            AllWeatherFragment.newInstance(cnt1[0],area1[0],cnt2[0]));
                    //実行(絶対最後!!)
                    fragmentTransaction.commit();
                }
            }
        });

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


