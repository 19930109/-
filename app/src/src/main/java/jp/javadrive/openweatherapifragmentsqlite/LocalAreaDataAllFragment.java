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
import android.widget.GridView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_day;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalAreaCurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalAreaDataAllFragment extends ListFragment {
    private List<Date> dateArray1 = new ArrayList();
    private DateManager mDateManager;
    private TextView titleText;
    private CalendarAdapter mCalendarAdapter;
    private GridView calendarGridView;

    static LocalAreaDataAllFragment newInstance(
            String areaname, int cnt) {
        // インスタンス生成
        LocalAreaDataAllFragment localAreaDataAllfragment = new LocalAreaDataAllFragment();
        // Bundleにパラメータを設定
        Bundle barg = new Bundle();
        barg.putString("areaname", areaname);
        barg.putInt("cnt", cnt);
        localAreaDataAllfragment.setArguments(barg);

        return localAreaDataAllfragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_data,
                container, false);

        titleText = view.findViewById(R.id.titleText);

        Bundle args = getArguments();
        assert args != null;
        String areaname = args.getString("areaname");
        int cnt = args.getInt("cnt");

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(areaname + "の1か月前のカレンダー");

        // メイン(UI)スレッドでHandlerのインスタンスを生成する
        final Handler handler = new Handler();
        calendarGridView = view.findViewById(R.id.calendarGridView);
        mCalendarAdapter = new CalendarAdapter(getActivity());
        calendarGridView.setAdapter(mCalendarAdapter);
        //先月のカレンダーにする
        //mCalendarAdapter.prevMonth();
        titleText.setText(mCalendarAdapter.getTitle());


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
            String time = null;
            String weather = null;
            String Icon = null;
            String cityname = null;


            /*カレンダーの先頭の日付に該当する配列の位置を探す(そこからカレンダーのマス分、ループさせる)
             DateManagerのgetDays()で日付を取得。"yyyy/MM/dd"の形で(String)
             for文で配列すべてを確認。一致したら終了。試行回数をカウントする。(int)
             試行回数の数からカレンダーのマス数回文ループを実行
            */
        //CalendarAdapterで取得したデータを代入


        dateArray1 = mCalendarAdapter.dateArray;

        //カレンダーの先頭の日付を"yyyy/MM/dd"の形で取得
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String day = dateFormat1.format(dateArray1.get(0));
        Log.d("day",day);
        //カレンダーのマス数(int count)
        int count = dateArray1.size();
        Log.d("count", String.valueOf(count));
        //ループでjsonの配列から一致する日付を探す
        int roop=0;
        int roop1=0;
        for(int i=0;i<resultSet.length();i++){
            JSONObject dayjson = null;
            try {
                dayjson = new JSONObject(resultSet.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String day1 = null;
            try {
                day1 = dayjson.getString("day");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(day1.equals(day)){
                roop1=roop;
            }else {
                roop++;
            }
        }
        Log.d("roop", String.valueOf(roop1));


        int max =roop1 + count;
        Log.d("max", String.valueOf(max));


            JSONObject dayjson = null;
        for (int j = roop1; j < resultSet.length(); j++) {
                try {
                    dayjson = new JSONObject(resultSet.getString(j));
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
                    time = dayjson.getString("day");
                } catch (JSONException e) {
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
                String finalDay = time;
                String finalWeather = weather;
                String finalIcon = Icon;




                mCalendarAdapter.add(finalDay, finalPop, finalTemp, finalWeather, finalIcon, cityname);
            }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        assert args != null;

        String areaname = args.getString("areaname");
        int cnt = args.getInt("cnt");

    }

    //spiinerの実装+spinnerの値を持った画面再表示
    @Override
    public void onResume() {
        super.onResume();

        // Bundleにパラメータを取得
        Bundle args = getArguments();

        assert args != null;
        String areaname = args.getString("areaname");
        int cnt = args.getInt("cnt");

    }

    public interface OnCurrentListener {
        void onDaialog(String message);
    }


}

