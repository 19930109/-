package jp.javadrive.openweatherapifragmentsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_day;

//AsyncHttpRequestで取得した文字列を取り出す。 + WeatherOpenHelperで作成したSQLiteにinsertする。

public class APIInsert_dayService {


    public void insertDB(MainFragment fragment, String cityname, String id, JSONObject jsonObject) throws JSONException {

        //serviceObjectでやっていた処理

        JSONArray dailyJsonArray = jsonObject.getJSONArray("daily");

        //sqliteの読み込み
        Weather_dayOpenHelper helper = new Weather_dayOpenHelper(fragment.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();



        //jsonArrayを回す
        for (int i = 0; dailyJsonArray.length() > i; i++) {
            //日付の取り出し
            JSONObject dailyjsonObject = new JSONObject(dailyJsonArray.getString(i));
            Long d1=Long.valueOf(dailyjsonObject.getString("dt"));
            Date date = new Date(d1*1000);
            TimeZone timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo");

            SimpleDateFormat fmt1 = new SimpleDateFormat("yyyyMMdd");
            fmt1.setTimeZone(timeZoneJP);
            String day=fmt1.format(date);
            String id1= fmt1.format(date);



            insertValues.put("day",day);//日付用
            Log.v("確認:day", day);

            //都道府県名の取り出し
            insertValues.put("cityname",cityname);
          //  Log.v("確認:cityname", cityname);

            //weather(天気)の取り出し
            JSONArray weatherjsonObject = dailyjsonObject.getJSONArray("weather");
            JSONObject wjsonObject = new JSONObject(weatherjsonObject.getString(0));
            String weather=wjsonObject.getString("description");
            insertValues.put("weather",weather);
          //  Log.v("確認:weather", weather);

            //アイコンの取り出し
            String icon=wjsonObject.getString("icon");
            insertValues.put("icon",icon);
           // Log.v("確認:icon", icon);


            //tempの取り出し
            JSONObject tjsonObject = dailyjsonObject.getJSONObject("temp");
            Double temp= Double.valueOf(tjsonObject.getString("day"));
            //小数点第二位を四捨五入

            temp= Double.valueOf(String.format("%.1f", temp));
            insertValues.put("temps",temp);
            //Log.v("確認:temp", String.valueOf(temp));

            //pop(降水確率)の取り出し
            String pop=dailyjsonObject.getString("pop");
            //*100で％の単位に変換
            double doublepop = Double.parseDouble(pop);
            doublepop=doublepop*100;
            insertValues.put("pop",doublepop);
            //Log.v("確認:pop", String.valueOf(doublepop));

            String id2 = id+id1 ;
            insertValues.put("id",id2);//day用
           // Log.v("確認:id", id2);

            //dbにinsert


            Cursor c = db.query(table_weather_day, new String[]{"id", "cityname", "day", "weather", "icon", "temps", "pop"}, "id=?",
                    new String[]{id2}, null, null, null, null);
            String kakuninn;
            try {
            JSONArray str = cur2Json(c);
            JSONObject dayjson = new JSONObject(str.getString(0));
            kakuninn = dayjson.getString("id");
            Log.v("確認:id", kakuninn);
            }catch (Exception ex ){
                kakuninn = "0";
                Log.v("確認:id", kakuninn);
            }

            if(kakuninn.equals(id2)) {
                db.update(table_weather_day, insertValues, "id = ?", new String[]{id2});
                Log.d("更新", "しました");
            }else {
                db.insert(table_weather_day, day, insertValues);//(table名,NOT NULLのもの,column)
                Log.d("登録", "しました");
            }

        }
        db.close();
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
