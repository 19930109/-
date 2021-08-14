package jp.javadrive.openweatherapifragmentsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_day;

//SQLiteでデータ作成するためのコード

public class Weather_dayOpenHelper extends SQLiteOpenHelper {
//SQLiteデータベースの中身(column)
    public Weather_dayOpenHelper(Context context) {
        super(context, table_weather_day, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE weatherapi_day2(" +
                "id text not null primary key,"+          //都道府県名
                "cityname text not null,"+          //都道府県名
                "day text not null,"+               //日付
                "weather TEXT," +                   //天気
                "icon TEXT," +                       //天気アイコン
                "temps TEXT," +                     //気温
                "pop TEXT);";                        //降水確率


        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
