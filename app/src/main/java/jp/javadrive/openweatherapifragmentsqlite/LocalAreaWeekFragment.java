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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_day;

/**
 * A simple {@link ListFragment} subclass.
 * Use the {@link LocalAreaWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalAreaWeekFragment extends ListFragment {

    private ListAdapter adapter;

    static LocalAreaWeekFragment newInstance(
             String areaname,int cnt) {
        // インスタンス生成

        LocalAreaWeekFragment localAreaWeekfragment = new LocalAreaWeekFragment();

        // Bundleにパラメータを設定
        Bundle barg = new Bundle();
        barg.putString("areaname", areaname);
        barg.putInt("cnt", cnt);
        localAreaWeekfragment.setArguments(barg);


        return localAreaWeekfragment;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_days,
                container, false);

        Bundle args = getArguments();


        assert args != null;
        String areaname = args.getString("areaname");
        int cnt = args.getInt("cnt");



        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(areaname + "の1週間の天気");



        Weather_dayOpenHelper helper = new Weather_dayOpenHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        /*　クエリー(select)文
        　　idがCursorAdapterには必須なため、"rowid as _id"で代用
        　　limit "数字"で取得するデータ数を制限(nullで全部)　　*/
        Cursor c = db.query(table_weather_day, new String[]{"rowid as _id", "cityname", "day", "weather", "icon", "temps", "pop"}, "cityname=?",
                new String[]{areaname}, null, null, null ,null);


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
        String str11 = String.valueOf(resultSet);
        Log.v("結果1", str11);

        String pop = null;
        String temp = null;
        String time1 = null;
        String weather = null;
        String Icon = null;
        String cityname = null;
        adapter = new ListAdapter(getActivity());
        setListAdapter(adapter);


        JSONObject dayjson = null;
        for (int i = resultSet.length()-7; i < resultSet.length(); i++) {
            try {
                dayjson = new JSONObject(resultSet.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                cityname = dayjson.getString("cityname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //時間をセット
            try {
                String time = dayjson.getString("day");
                SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = sdFormat.parse(time);
                SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy年MM月dd日");

                time1 = fmt2.format(date);
                    Log.d("昨日", time1);

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

            //weather(天気)の取り出し・セット
            try {
                weather = dayjson.getString("weather");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //tempの取り出し
            try {
                temp = String.valueOf(Double.valueOf(dayjson.getString("temps")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //小数点第二位を四捨五入
            double doubletemp = Double.parseDouble(temp);
            temp = String.format("%.1f", doubletemp);

            //pop(降水確率)の取り出し
            try {
                pop = String.valueOf(Double.valueOf(dayjson.getString("pop")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //*100で％の単位に変換
            double doublepop = Double.parseDouble(pop);
            pop = String.valueOf(doublepop);

            //アイコンの取り出し
            try {
                Icon = dayjson.getString("icon");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Handlerを使用してメイン(UI)スレッドに処理を依頼する
            String finalPop = pop;
            String finalTemp = temp;
            String finalDay = time1;
            String finalWeather = weather;
            String finalIcon = Icon;




            adapter.add(finalDay, finalPop, finalTemp, finalWeather, finalIcon, cityname);
        }
        return view;
    }


    public interface OnCurrentListener {
        void onDaialog(String message);
    }

}


