package jp.javadrive.openweatherapifragmentsqlite;



import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// 下位のバージョンにも対応させる場合はsupport-v4パッケージを使用します

// Fragmentクラスを継承します
public class MainFragment extends Fragment {

    static MainFragment newInstance() {
        // インスタンス生成
        MainFragment mainfragment = new MainFragment();

        return mainfragment;
    }

    final static String table_weather_day = "weatherapi_day2";//テーブル名変更時はWeather_dayOpenHelperのsql文のみ名前変更!!
    final static String table_weather_hour = "weatherapi_hour2";//テーブル名変更時はWeather_hourOpenHelperのsql文のみ名前変更!!

    // FragmentのViewを生成して返す
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,
                container, false);

        List<String> list = new ArrayList<>(Arrays.asList(
                "101","北海道", "43.06417", "141.34694",
                "102","青森県","40.82444","140.74",
                "103","岩手県", "39.70361", "141.1525",
                "104","宮城県", "38.26889", "140.87194",
                "105","秋田県", "39.71861", "140.1025",
                "106","山形県", "38.24056", "140.36333",
                "107","福島県", "37.75", "140.46778",
                "108","茨城県", "36.34139", "140.44667",
                "109","栃木県", "36.56583", "139.88361",
                "110","群馬県", "36.39111", "139.06083",
                "111","埼玉県", "35.85694", "139.64889",
                "112","千葉県", "35.60472", "140.12333",
                "113","東京都", "35.68944", "139.69167",
                "114","神奈川県", "35.44778", "139.6425",
                "115","新潟県", "37.90222", "139.02361",
                "116","富山県", "36.69528", "137.21139",
                "117","石川県","36.59444","136.62556",
                "118","福井県", "36.06528", "136.22194",
                "119","山梨県", "35.66389", "138.56833",
                "120","長野県", "36.65139", "138.18111",
                "121","岐阜県", "35.39111", "136.72222",
                "122","静岡県", "34.97694", "138.38306",
                "123","愛知県", "35.18028", "136.90667",
                "124","三重県", "34.73028", "136.50861",
                "125","滋賀県", "35.00444", "135.86833",
                "126","京都府", "35.02139", "135.75556",
                "127","大阪府", "34.68639", "135.52",
                "128","兵庫県", "34.69139", "135.18306",
                "129","奈良県", "34.68528", "135.83278",
                "130","和歌山県", "34.22611", "135.1675",
                "131","鳥取県", "35.50361", "134.23833",
                "132","島根県", "35.47222", "133.05056",
                "133","岡山県", "34.66167", "133.935",
                "134","広島県", "34.39639", "132.45944",
                "135","山口県", "34.18583", "131.47139",
                "136","徳島県", "34.06583", "134.55944",
                "137","香川県", "34.34028", "134.04333",
                "138","愛媛県", "33.84167", "132.76611",
                "139","高知県", "33.55972", "133.53111",
                "140","福岡県", "33.60639", "130.41806",
                "141","佐賀県", "33.24944", "130.29889",
                "142","長崎県", "32.74472", "129.87361",
                "143","熊本県", "32.78972", "130.74167",
                "144","大分県", "33.23806", "131.6125",
                "145","宮崎県", "31.91111", "131.42389",
                "146","鹿児島県", "31.560282", "130.55806",
                "147","沖縄県", "26.2125", "127.68111"
        ));


        String time = null;
        JSONObject dayjson = null;


//1週間分の天気取得について



        //現在の日付の取得から昨日と当日のIDを作成
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.add(Calendar.DAY_OF_MONTH, -1);//昨日
        Date date11 = calendar3.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, +4);//4日後
        Date date2 = calendar2.getTime();
        calendar1.add(Calendar.DAY_OF_MONTH, +6);//7日後
        Date date1 = calendar1.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -84);//84日前
        Date date3 = calendar.getTime();


        SimpleDateFormat fmt2 = new SimpleDateFormat("yyyyMMdd");

        String day11 = fmt2.format(date11);//昨日
        Log.d("昨日", day11);
        String day4 = fmt2.format(date2);//4日後
        Log.d("4日後", day4);
        String day7 = fmt2.format(date1);//7日後
        Log.d("7日後", day7);
        String day84 = fmt2.format(date3);//84日前
        Log.d("84日前", day84);


        //検索検索
        /*　クエリー(select)文
        　　idがCursorAdapterには必須なため、"rowid as _id"で代用
        　　limit "数字"で取得するデータ数を制限(nullで全部)　　*/
        Weather_dayOpenHelper helper = new Weather_dayOpenHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(table_weather_day, new String[]{"id", "cityname", "day", "weather", "icon", "temps", "pop"}, "day=?",
                new String[]{day7}, null, null, null, null);
        Log.v("検索s", "実行完了");

        Weather_hourOpenHelper helper2 = new Weather_hourOpenHelper(getActivity());
        SQLiteDatabase db2 = helper2.getReadableDatabase();


        /*　クエリー(select)文
        　　idがCursorAdapterには必須なため、"rowid as _id"で代用
        　　limit "数字"で取得するデータ数を制限(nullで全部)　　*/
        Cursor c1 = db2.query(table_weather_hour, new String[]{"id", "cityname", "time", "day", "weather", "temps", "pop", "icon"}, "time=?",
                new String[]{day4}, null, null, null, null);
        Log.v("検索1", "実行完了");


        //weatherapi_day2のレコード数
        long recodeCount = DatabaseUtils.queryNumEntries(db,table_weather_day);
        Log.d("TAG", table_weather_day + "のレコード数 : " + recodeCount);

        //weatherapi_hour2のレコード数
        long recodeCount1 = DatabaseUtils.queryNumEntries(db2,table_weather_hour);
        Log.d("TAG", table_weather_hour + "のレコード数 : " + recodeCount1);

/*
        SQLiteDatabase db5 = helper.getWritableDatabase();
        db5.execSQL("delete from weatherapi_day2");

        for(int a=1;a<8;a++) {



    String day = "2021080"+a;
    String cityname1 = "北海道";
    String weather = "適度な雨";
    String icon = "10d";
    String temp = "29.55";
    String doublepop = "0.66";
    String id2 = "1012021080"+a;

    SQLiteDatabase db3 = helper.getWritableDatabase();
    ContentValues insertValues = new ContentValues();
    insertValues.put("day", day);//日付用
    insertValues.put("cityname", cityname1);
    insertValues.put("weather", weather);
    insertValues.put("icon", icon);
    insertValues.put("temps", temp);
    insertValues.put("pop", doublepop);
    insertValues.put("id", id2);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day, insertValues);//(table名,NOT NULLのもの,column)

    String day2 = "2021080"+a;
    String cityname12 = "青森県";
    String weather2 = "適度な雨";
    String icon2 = "10d";
    String temp2 = "31.9";
    String doublepop2 = "0.7";
    String id22 = "1022021080"+a;


    insertValues.put("day", day2);//日付用
    insertValues.put("cityname", cityname12);
    insertValues.put("weather", weather2);
    insertValues.put("icon", icon2);
    insertValues.put("temps", temp2);
    insertValues.put("pop", doublepop2);
    insertValues.put("id", id22);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day2, insertValues);//(table名,NOT NULLのもの,column)

    String day3 = "2021080"+a;
    String cityname13 = "岩手県";
    String weather3 = "小雨";
    String icon3 = "10d";
    String temp3 = "29.68";
    String doublepop3 = "0.36";
    String id23 = "1032021080"+a;


    insertValues.put("day", day3);//日付用
    insertValues.put("cityname", cityname13);
    insertValues.put("weather", weather3);
    insertValues.put("icon", icon3);
    insertValues.put("temps", temp3);
    insertValues.put("pop", doublepop3);
    insertValues.put("id", id23);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day3, insertValues);//(table名,NOT NULLのもの,column)

    String day44 = "2021080"+a;
    String cityname14 = "宮城県";
    String weather4 = "小雨";
    String icon4 = "10d";
    String temp4 = "33.71";
    String doublepop4 = "0.76";
    String id24 = "1042021080"+a;


    insertValues.put("day", day44);//日付用
    insertValues.put("cityname", cityname14);
    insertValues.put("weather", weather4);
    insertValues.put("icon", icon4);
    insertValues.put("temps", temp4);
    insertValues.put("pop", doublepop4);
    insertValues.put("id", id24);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day4, insertValues);//(table名,NOT NULLのもの,column)

    String day5 = "2021080"+a;
    String cityname15 = "秋田県";
    String weather5 = "小雨";
    String icon5 = "10d";
    String temp5 = "32.06";
    String doublepop5 = "0.33";
    String id25 = "1052021080"+a;


    insertValues.put("day", day5);//日付用
    insertValues.put("cityname", cityname15);
    insertValues.put("weather", weather5);
    insertValues.put("icon", icon5);
    insertValues.put("temps", temp5);
    insertValues.put("pop", doublepop5);
    insertValues.put("id", id25);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day5, insertValues);//(table名,NOT NULLのもの,column)

    String day6 = "2021080"+a;
    String cityname16 = "山形県";
    String weather6 = "小雨";
    String icon6 = "04d";
    String temp6 = "29.21";
    String doublepop6 = "0.47";
    String id26 = "1062021080"+a;


    insertValues.put("day", day6);//日付用
    insertValues.put("cityname", cityname16);
    insertValues.put("weather", weather6);
    insertValues.put("icon", icon6);
    insertValues.put("temps", temp6);
    insertValues.put("pop", doublepop6);
    insertValues.put("id", id26);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day6, insertValues);//(table名,NOT NULLのもの,column)

    String day77 = "2021080"+a;
    String cityname17 = "福島県";
    String weather7 = "小雨";
    String icon7 = "10d";
    String temp7 = "29.86";
    String doublepop7 = "0.66";
    String id27 = "1072021080"+a;


    insertValues.put("day", day77);//日付用
    insertValues.put("cityname", cityname17);
    insertValues.put("weather", weather7);
    insertValues.put("icon", icon7);
    insertValues.put("temps", temp7);
    insertValues.put("pop", doublepop7);
    insertValues.put("id", id27);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day7, insertValues);//(table名,NOT NULLのもの,column)

    String day8 = "2021080"+a;
    String cityname18 = "茨城県";
    String weather8 = "小雨";
    String icon8 = "小雨";
    String temp8 = "28.75";
    String doublepop8 = "0.36";
    String id28 = "1082021080"+a;


    insertValues.put("day", day8);//日付用
    insertValues.put("cityname", cityname18);
    insertValues.put("weather", weather8);
    insertValues.put("icon", icon8);
    insertValues.put("temps", temp8);
    insertValues.put("pop", doublepop8);
    insertValues.put("id", id28);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day8, insertValues);//(table名,NOT NULLのもの,column)


    String day9 = "2021080"+a;
    String cityname19 = "栃木県";
    String weather9 = "厚い雲";
    String icon9 = "04d";
    String temp9 = "29.11";
    String doublepop9 = "0.19";
    String id29 = "1092021080"+a;


    insertValues.put("day", day9);//日付用
    insertValues.put("cityname", cityname19);
    insertValues.put("weather", weather9);
    insertValues.put("icon", icon9);
    insertValues.put("temps", temp9);
    insertValues.put("pop", doublepop9);
    insertValues.put("id", id29);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day9, insertValues);//(table名,NOT NULLのもの,column)

    String day10 = "2021080"+a;
    String cityname110 = "群馬県";
    String weather10 = "曇りがち";
    String icon10 = "04d";
    String temp10 = "29.55";
    String doublepop10 = "0.66";
    String id210 = "1102021080"+a;


    insertValues.put("day", day10);//日付用
    insertValues.put("cityname", cityname110);
    insertValues.put("weather", weather10);
    insertValues.put("icon", icon10);
    insertValues.put("temps", temp10);
    insertValues.put("pop", doublepop10);
    insertValues.put("id", id210);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day10, insertValues);//(table名,NOT NULLのもの,column)



    String day111 = "2021080"+a;
    String cityname111 = "埼玉県";
    String weather11 = "適度な雨";
    String icon11 = "10d";
    String temp11 = "29.55";
    String doublepop11 = "0.66";
    String id211 = "1112021080"+a;


    insertValues.put("day", day111);//日付用
    insertValues.put("cityname", cityname111);
    insertValues.put("weather", weather11);
    insertValues.put("icon", icon11);
    insertValues.put("temps", temp11);
    insertValues.put("pop", doublepop11);
    insertValues.put("id", id211);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day111, insertValues);//(table名,NOT NULLのもの,column)

    String day12 = "2021080"+a;
    String cityname112 = "千葉県";
    String weather12 = "適度な雨";
    String icon12 = "10d";
    String temp12 = "31.9";
    String doublepop12 = "0.7";
    String id212 = "1122021080"+a;


    insertValues.put("day", day12);//日付用
    insertValues.put("cityname", cityname112);
    insertValues.put("weather", weather12);
    insertValues.put("icon", icon12);
    insertValues.put("temps", temp12);
    insertValues.put("pop", doublepop12);
    insertValues.put("id", id212);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day12, insertValues);//(table名,NOT NULLのもの,column)

    String day13 = "2021080"+a;
    String cityname113 = "東京都";
    String weather13 = "小雨";
    String icon13 = "10d";
    String temp13 = "29.68";
    String doublepop13 = "0.36";
    String id213 = "1132021080"+a;


    insertValues.put("day", day13);//日付用
    insertValues.put("cityname", cityname113);
    insertValues.put("weather", weather13);
    insertValues.put("icon", icon13);
    insertValues.put("temps", temp13);
    insertValues.put("pop", doublepop13);
    insertValues.put("id", id213);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day13, insertValues);//(table名,NOT NULLのもの,column)

    String day414 = "2021080"+a;
    String cityname114 = "神奈川県";
    String weather14 = "小雨";
    String icon14 = "10d";
    String temp14 = "33.71";
    String doublepop14 = "0.76";
    String id214 = "1142021080"+a;


    insertValues.put("day", day414);//日付用
    insertValues.put("cityname", cityname114);
    insertValues.put("weather", weather14);
    insertValues.put("icon", icon14);
    insertValues.put("temps", temp14);
    insertValues.put("pop", doublepop14);
    insertValues.put("id", id214);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day414, insertValues);//(table名,NOT NULLのもの,column)

    String day15 = "2021080"+a;
    String cityname115 = "新潟県";
    String weather15 = "小雨";
    String icon15 = "10d";
    String temp15 = "32.06";
    String doublepop15 = "0.33";
    String id215 = "1152021080"+a;


    insertValues.put("day", day15);//日付用
    insertValues.put("cityname", cityname115);
    insertValues.put("weather", weather15);
    insertValues.put("icon", icon15);
    insertValues.put("temps", temp15);
    insertValues.put("pop", doublepop15);
    insertValues.put("id", id215);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day15, insertValues);//(table名,NOT NULLのもの,column)

    String day16 = "2021080"+a;
    String cityname116 = "富山県";
    String weather16 = "小雨";
    String icon16 = "04d";
    String temp16 = "29.21";
    String doublepop16 = "0.47";
    String id216 = "1162021080"+a;


    insertValues.put("day", day16);//日付用
    insertValues.put("cityname", cityname116);
    insertValues.put("weather", weather16);
    insertValues.put("icon", icon16);
    insertValues.put("temps", temp16);
    insertValues.put("pop", doublepop16);
    insertValues.put("id", id216);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day16, insertValues);//(table名,NOT NULLのもの,column)

    String day17 = "2021080"+a;
    String cityname117 = "石川県";
    String weather17 = "小雨";
    String icon17 = "10d";
    String temp17 = "29.86";
    String doublepop17 = "0.66";
    String id217 = "1172021080"+a;


    insertValues.put("day", day17);//日付用
    insertValues.put("cityname", cityname117);
    insertValues.put("weather", weather17);
    insertValues.put("icon", icon17);
    insertValues.put("temps", temp17);
    insertValues.put("pop", doublepop17);
    insertValues.put("id", id217);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day17, insertValues);//(table名,NOT NULLのもの,column)

    String day18 = "2021080"+a;
    String cityname118 = "福井県";
    String weather18 = "小雨";
    String icon18 = "小雨";
    String temp18 = "28.75";
    String doublepop18 = "0.36";
    String id218 = "1182021080"+a;


    insertValues.put("day", day18);//日付用
    insertValues.put("cityname", cityname118);
    insertValues.put("weather", weather18);
    insertValues.put("icon", icon18);
    insertValues.put("temps", temp18);
    insertValues.put("pop", doublepop18);
    insertValues.put("id", id218);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day18, insertValues);//(table名,NOT NULLのもの,column)


    String day19 = "2021080"+a;
    String cityname119 = "山梨県";
    String weather19 = "厚い雲";
    String icon19 = "04d";
    String temp19 = "29.11";
    String doublepop19 = "0.19";
    String id219 = "1192021080"+a;


    insertValues.put("day", day19);//日付用
    insertValues.put("cityname", cityname119);
    insertValues.put("weather", weather19);
    insertValues.put("icon", icon19);
    insertValues.put("temps", temp19);
    insertValues.put("pop", doublepop19);
    insertValues.put("id", id219);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day19, insertValues);//(table名,NOT NULLのもの,column)

    String day20 = "2021080"+a;
    String cityname120 = "長野県";
    String weather20 = "曇りがち";
    String icon20 = "04d";
    String temp20 = "29.55";
    String doublepop20 = "0.66";
    String id220 = "1202021080"+a;


    insertValues.put("day", day20);//日付用
    insertValues.put("cityname", cityname120);
    insertValues.put("weather", weather20);
    insertValues.put("icon", icon20);
    insertValues.put("temps", temp20);
    insertValues.put("pop", doublepop20);
    insertValues.put("id", id220);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day20, insertValues);//(table名,NOT NULLのもの,column)

    String day21 = "2021080"+a;
    String cityname121 = "岐阜県";
    String weather21 = "適度な雨";
    String icon21 = "10d";
    String temp21 = "29.55";
    String doublepop21 = "0.66";
    String id221 = "1212021080"+a;


    insertValues.put("day", day21);//日付用
    insertValues.put("cityname", cityname121);
    insertValues.put("weather", weather21);
    insertValues.put("icon", icon21);
    insertValues.put("temps", temp21);
    insertValues.put("pop", doublepop21);
    insertValues.put("id", id221);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day21, insertValues);//(table名,NOT NULLのもの,column)

    String day22 = "2021080"+a;
    String cityname122 = "静岡県";
    String weather22 = "適度な雨";
    String icon22 = "10d";
    String temp22 = "31.9";
    String doublepop22 = "0.7";
    String id222 = "1222021080"+a;


    insertValues.put("day", day22);//日付用
    insertValues.put("cityname", cityname122);
    insertValues.put("weather", weather22);
    insertValues.put("icon", icon22);
    insertValues.put("temps", temp22);
    insertValues.put("pop", doublepop22);
    insertValues.put("id", id222);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day22, insertValues);//(table名,NOT NULLのもの,column)

    String day23 = "2021080"+a;
    String cityname123 = "愛知県";
    String weather23 = "小雨";
    String icon23 = "10d";
    String temp23 = "29.68";
    String doublepop23 = "0.36";
    String id223 = "1232021080"+a;


    insertValues.put("day", day23);//日付用
    insertValues.put("cityname", cityname123);
    insertValues.put("weather", weather23);
    insertValues.put("icon", icon23);
    insertValues.put("temps", temp23);
    insertValues.put("pop", doublepop23);
    insertValues.put("id", id223);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day23, insertValues);//(table名,NOT NULLのもの,column)

    String day24 = "2021080"+a;
    String cityname124 = "三重県";
    String weather24 = "小雨";
    String icon24 = "10d";
    String temp24 = "33.71";
    String doublepop24 = "0.76";
    String id224 = "1242021080"+a;


    insertValues.put("day", day24);//日付用
    insertValues.put("cityname", cityname124);
    insertValues.put("weather", weather24);
    insertValues.put("icon", icon24);
    insertValues.put("temps", temp24);
    insertValues.put("pop", doublepop24);
    insertValues.put("id", id224);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day24, insertValues);//(table名,NOT NULLのもの,column)

    String day25 = "2021080"+a;
    String cityname125 = "滋賀県";
    String weather25 = "小雨";
    String icon25 = "10d";
    String temp25 = "32.06";
    String doublepop25 = "0.33";
    String id225 = "1252021080"+a;


    insertValues.put("day", day25);//日付用
    insertValues.put("cityname", cityname125);
    insertValues.put("weather", weather25);
    insertValues.put("icon", icon25);
    insertValues.put("temps", temp25);
    insertValues.put("pop", doublepop25);
    insertValues.put("id", id225);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day25, insertValues);//(table名,NOT NULLのもの,column)

    String day26 = "2021080"+a;
    String cityname126 = "京都府";
    String weather26 = "小雨";
    String icon26 = "04d";
    String temp26 = "29.21";
    String doublepop26 = "0.47";
    String id226 = "1262021080"+a;


    insertValues.put("day", day26);//日付用
    insertValues.put("cityname", cityname126);
    insertValues.put("weather", weather26);
    insertValues.put("icon", icon26);
    insertValues.put("temps", temp26);
    insertValues.put("pop", doublepop26);
    insertValues.put("id", id226);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day26, insertValues);//(table名,NOT NULLのもの,column)

    String day727 = "2021080"+a;
    String cityname127 = "大阪府";
    String weather27 = "小雨";
    String icon27 = "10d";
    String temp27 = "29.86";
    String doublepop27 = "0.66";
    String id227 = "1272021080"+a;


    insertValues.put("day", day727);//日付用
    insertValues.put("cityname", cityname127);
    insertValues.put("weather", weather27);
    insertValues.put("icon", icon27);
    insertValues.put("temps", temp27);
    insertValues.put("pop", doublepop27);
    insertValues.put("id", id227);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day727, insertValues);//(table名,NOT NULLのもの,column)

    String day28 = "2021080"+a;
    String cityname128 = "兵庫県";
    String weather28 = "小雨";
    String icon28 = "小雨";
    String temp28 = "28.75";
    String doublepop28 = "0.36";
    String id228 = "1282021080"+a;


    insertValues.put("day", day28);//日付用
    insertValues.put("cityname", cityname128);
    insertValues.put("weather", weather28);
    insertValues.put("icon", icon28);
    insertValues.put("temps", temp28);
    insertValues.put("pop", doublepop28);
    insertValues.put("id", id228);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day28, insertValues);//(table名,NOT NULLのもの,column)


    String day29 = "2021080"+a;
    String cityname129 = "奈良県";
    String weather29 = "厚い雲";
    String icon29 = "04d";
    String temp29 = "29.11";
    String doublepop29 = "0.19";
    String id229 = "1292021080"+a;


    insertValues.put("day", day29);//日付用
    insertValues.put("cityname", cityname129);
    insertValues.put("weather", weather29);
    insertValues.put("icon", icon29);
    insertValues.put("temps", temp29);
    insertValues.put("pop", doublepop29);
    insertValues.put("id", id229);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day29, insertValues);//(table名,NOT NULLのもの,column)

    String day30 = "2021080"+a;
    String cityname130 = "和歌山県";
    String weather30 = "曇りがち";
    String icon30 = "04d";
    String temp30 = "29.55";
    String doublepop30 = "0.66";
    String id230 = "1302021080"+a;


    insertValues.put("day", day30);//日付用
    insertValues.put("cityname", cityname130);
    insertValues.put("weather", weather30);
    insertValues.put("icon", icon30);
    insertValues.put("temps", temp30);
    insertValues.put("pop", doublepop30);
    insertValues.put("id", id230);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day30, insertValues);//(table名,NOT NULLのもの,column)

    String day31 = "2021080"+a;
    String cityname131 = "鳥取県";
    String weather31 = "適度な雨";
    String icon31 = "10d";
    String temp31 = "29.55";
    String doublepop31 = "0.66";
    String id231 = "1312021080"+a;


    insertValues.put("day", day31);//日付用
    insertValues.put("cityname", cityname131);
    insertValues.put("weather", weather31);
    insertValues.put("icon", icon31);
    insertValues.put("temps", temp31);
    insertValues.put("pop", doublepop31);
    insertValues.put("id", id231);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day31, insertValues);//(table名,NOT NULLのもの,column)

    String day32 = "2021080"+a;
    String cityname132 = "島根県";
    String weather32 = "適度な雨";
    String icon32 = "10d";
    String temp32 = "31.9";
    String doublepop32 = "0.7";
    String id232 = "1322021080"+a;


    insertValues.put("day", day32);//日付用
    insertValues.put("cityname", cityname132);
    insertValues.put("weather", weather32);
    insertValues.put("icon", icon32);
    insertValues.put("temps", temp32);
    insertValues.put("pop", doublepop32);
    insertValues.put("id", id232);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day32, insertValues);//(table名,NOT NULLのもの,column)

    String day33 = "2021080"+a;
    String cityname133 = "岡山県";
    String weather33 = "小雨";
    String icon33 = "10d";
    String temp33 = "29.68";
    String doublepop33 = "0.36";
    String id233 = "1332021080"+a;


    insertValues.put("day", day33);//日付用
    insertValues.put("cityname", cityname133);
    insertValues.put("weather", weather33);
    insertValues.put("icon", icon33);
    insertValues.put("temps", temp33);
    insertValues.put("pop", doublepop33);
    insertValues.put("id", id233);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day33, insertValues);//(table名,NOT NULLのもの,column)

    String day34 = "2021080"+a;
    String cityname134 = "広島県";
    String weather34 = "小雨";
    String icon34 = "10d";
    String temp34 = "33.71";
    String doublepop34 = "0.76";
    String id234 = "1342021080"+a;


    insertValues.put("day", day34);//日付用
    insertValues.put("cityname", cityname134);
    insertValues.put("weather", weather34);
    insertValues.put("icon", icon34);
    insertValues.put("temps", temp34);
    insertValues.put("pop", doublepop34);
    insertValues.put("id", id234);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day34, insertValues);//(table名,NOT NULLのもの,column)

    String day35 = "2021080"+a;
    String cityname135 = "山口県";
    String weather35 = "小雨";
    String icon35 = "10d";
    String temp35 = "32.06";
    String doublepop35 = "0.33";
    String id235 = "1352021080"+a;


    insertValues.put("day", day35);//日付用
    insertValues.put("cityname", cityname135);
    insertValues.put("weather", weather35);
    insertValues.put("icon", icon35);
    insertValues.put("temps", temp35);
    insertValues.put("pop", doublepop35);
    insertValues.put("id", id235);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day35, insertValues);//(table名,NOT NULLのもの,column)

    String day36 = "2021080"+a;
    String cityname136 = "徳島県";
    String weather36 = "小雨";
    String icon36 = "04d";
    String temp36 = "29.21";
    String doublepop36 = "0.47";
    String id236 = "1362021080"+a;


    insertValues.put("day", day36);//日付用
    insertValues.put("cityname", cityname136);
    insertValues.put("weather", weather36);
    insertValues.put("icon", icon36);
    insertValues.put("temps", temp36);
    insertValues.put("pop", doublepop36);
    insertValues.put("id", id236);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day36, insertValues);//(table名,NOT NULLのもの,column)

    String day37 = "2021080"+a;
    String cityname137 = "香川県";
    String weather37 = "小雨";
    String icon37 = "10d";
    String temp37 = "29.86";
    String doublepop37 = "0.66";
    String id237 = "1372021080"+a;


    insertValues.put("day", day37);//日付用
    insertValues.put("cityname", cityname137);
    insertValues.put("weather", weather37);
    insertValues.put("icon", icon37);
    insertValues.put("temps", temp37);
    insertValues.put("pop", doublepop37);
    insertValues.put("id", id237);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day37, insertValues);//(table名,NOT NULLのもの,column)

    String day38 = "2021080"+a;
    String cityname138 = "愛媛県";
    String weather38 = "小雨";
    String icon38 = "小雨";
    String temp38 = "28.75";
    String doublepop38 = "0.36";
    String id238 = "1382021080"+a;


    insertValues.put("day", day38);//日付用
    insertValues.put("cityname", cityname138);
    insertValues.put("weather", weather38);
    insertValues.put("icon", icon38);
    insertValues.put("temps", temp38);
    insertValues.put("pop", doublepop38);
    insertValues.put("id", id238);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day38, insertValues);//(table名,NOT NULLのもの,column)


    String day39 = "2021080"+a;
    String cityname139 = "高知県";
    String weather39 = "厚い雲";
    String icon39 = "04d";
    String temp39 = "29.11";
    String doublepop39 = "0.19";
    String id239 = "1392021080"+a;


    insertValues.put("day", day39);//日付用
    insertValues.put("cityname", cityname139);
    insertValues.put("weather", weather39);
    insertValues.put("icon", icon39);
    insertValues.put("temps", temp39);
    insertValues.put("pop", doublepop39);
    insertValues.put("id", id239);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day39, insertValues);//(table名,NOT NULLのもの,column)


    String day40 = "2021080"+a;
    String cityname140 = "福岡県";
    String weather40 = "適度な雨";
    String icon40 = "10d";
    String temp40 = "29.55";
    String doublepop40 = "0.66";
    String id240 = "1402021080"+a;


    insertValues.put("day", day40);//日付用
    insertValues.put("cityname", cityname140);
    insertValues.put("weather", weather40);
    insertValues.put("icon", icon40);
    insertValues.put("temps", temp40);
    insertValues.put("pop", doublepop40);
    insertValues.put("id", id240);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day40, insertValues);//(table名,NOT NULLのもの,column)

    String day42 = "2021080"+a;
    String cityname142 = "佐賀県";
    String weather42 = "適度な雨";
    String icon42 = "10d";
    String temp42 = "31.9";
    String doublepop42 = "0.7";
    String id242 = "1412021080"+a;


    insertValues.put("day", day42);//日付用
    insertValues.put("cityname", cityname142);
    insertValues.put("weather", weather42);
    insertValues.put("icon", icon42);
    insertValues.put("temps", temp42);
    insertValues.put("pop", doublepop42);
    insertValues.put("id", id242);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day42, insertValues);//(table名,NOT NULLのもの,column)

    String day43 = "2021080"+a;
    String cityname143 = "長崎県";
    String weather43 = "小雨";
    String icon43 = "10d";
    String temp43 = "29.68";
    String doublepop43 = "0.36";
    String id243 = "1422021080"+a;


    insertValues.put("day", day43);//日付用
    insertValues.put("cityname", cityname143);
    insertValues.put("weather", weather43);
    insertValues.put("icon", icon43);
    insertValues.put("temps", temp43);
    insertValues.put("pop", doublepop43);
    insertValues.put("id", id243);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day43, insertValues);//(table名,NOT NULLのもの,column)

    String day444 = "2021080"+a;
    String cityname144 = "熊本県";
    String weather44 = "小雨";
    String icon44 = "10d";
    String temp44 = "33.71";
    String doublepop44 = "0.76";
    String id244 = "1432021080"+a;


    insertValues.put("day", day444);//日付用
    insertValues.put("cityname", cityname144);
    insertValues.put("weather", weather44);
    insertValues.put("icon", icon44);
    insertValues.put("temps", temp44);
    insertValues.put("pop", doublepop44);
    insertValues.put("id", id244);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day44, insertValues);//(table名,NOT NULLのもの,column)

    String day45 = "2021080"+a;
    String cityname145 = "大分県";
    String weather45 = "小雨";
    String icon45 = "10d";
    String temp45 = "32.06";
    String doublepop45 = "0.33";
    String id245 = "1442021080"+a;


    insertValues.put("day", day45);//日付用
    insertValues.put("cityname", cityname145);
    insertValues.put("weather", weather45);
    insertValues.put("icon", icon45);
    insertValues.put("temps", temp45);
    insertValues.put("pop", doublepop45);
    insertValues.put("id", id245);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day45, insertValues);//(table名,NOT NULLのもの,column)

    String day46 = "2021080"+a;
    String cityname146 = "宮崎県";
    String weather46 = "小雨";
    String icon46 = "04d";
    String temp46 = "29.21";
    String doublepop46 = "0.47";
    String id246 = "1452021080"+a;


    insertValues.put("day", day46);//日付用
    insertValues.put("cityname", cityname146);
    insertValues.put("weather", weather46);
    insertValues.put("icon", icon46);
    insertValues.put("temps", temp46);
    insertValues.put("pop", doublepop46);
    insertValues.put("id", id246);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day46, insertValues);//(table名,NOT NULLのもの,column)

    String day747 = "2021080"+a;
    String cityname147 = "鹿児島県";
    String weather47 = "小雨";
    String icon47 = "10d";
    String temp47 = "29.86";
    String doublepop47 = "0.66";
    String id247 = "1462021080"+a;


    insertValues.put("day", day747);//日付用
    insertValues.put("cityname", cityname147);
    insertValues.put("weather", weather47);
    insertValues.put("icon", icon47);
    insertValues.put("temps", temp47);
    insertValues.put("pop", doublepop47);
    insertValues.put("id", id247);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day747, insertValues);//(table名,NOT NULLのもの,column)

    String day48 = "2021080"+a;
    String cityname148 = "沖縄県";
    String weather48 = "小雨";
    String icon48 = "小雨";
    String temp48 = "28.75";
    String doublepop48 = "0.36";
    String id248 = "1472021080"+a;


    insertValues.put("day", day48);//日付用
    insertValues.put("cityname", cityname148);
    insertValues.put("weather", weather48);
    insertValues.put("icon", icon48);
    insertValues.put("temps", temp48);
    insertValues.put("pop", doublepop48);
    insertValues.put("id", id248);//day用
    //db3.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
    db3.insert(table_weather_day, day48, insertValues);//(table名,NOT NULLのもの,column)



}


 */





        for (int i = 0; i < list.size(); i+=4) {
            String id = list.get(i);
            String cityname = list.get(i + 1);
            Double lat = Double.valueOf(list.get(i + 2));
            Double lon = Double.valueOf(list.get(i + 3));
            //Log.v(id + cityname, lat + ":" + lon);

            JSONArray str = cur2Json(c);
            //Log.d("", String.valueOf(str));


            try {

                    dayjson = new JSONObject(str.getString(0));
                    time = dayjson.getString("day");
                    //Log.d("",time);

            }  catch (JSONException ex)  {

                try {
                    //昨日のデータを削除
                   db.delete(table_weather_day, "day <= ?", new String[]{day84});

                    Log.v("登録", "実行します");
                    new DayAsyncHttpRequest(this,id,cityname).execute(new URL("\n" +
                            "https://api.openweathermap.org/data/2.5/onecall?" +
                            "lat=" + lat + "&" +
                            "lon=" + lon + "&" +
                            "exclude=minutely,hourly,alerts,current&lang=ja&units=metric&appid=88ffedd13af1bf272241ec39f417b2f5"));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }


            //3時間おきの天気取得について

            JSONArray str1 = cur2Json(c1);

            try {

                dayjson = new JSONObject(str1.getString(0));
                time = dayjson.getString("day");

            }  catch (JSONException ex)  {

                    try {
                        //昨日のデータを削除
                        db2.delete(table_weather_hour, "time <= ?", new String[]{day11});

                        new HourAsyncHttpRequest(this,id,cityname).execute(new URL("\n" +
                                "https://api.openweathermap.org/data/2.5/forecast?" +
                                "lat=" + lat + "&" +
                                "lon=" + lon + "&" +
                                "&lang=ja&units=metric&appid=88ffedd13af1bf272241ec39f417b2f5"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }


        }
        db.close();
        db2.close();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button buttonLocation = view.findViewById(R.id.LocationButton);
        buttonLocation.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                // BackStackを設定
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.container,
                        LocationMenuFragment.newInstance());
                fragmentTransaction.commit();
            }
        });

        Button buttonAll = view.findViewById(R.id.AllButton);
        buttonAll.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                // BackStackを設定
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.container,
                        AllWeatherFragment.newInstance(47,"全国",0));
                fragmentTransaction.commit();
            }
        });
    }

    public JSONArray cur2Json(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();

        }
        return resultSet;

    }

}
