package jp.javadrive.openweatherapifragmentsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static jp.javadrive.openweatherapifragmentsqlite.MainFragment.table_weather_hour;

//SQLiteでデータ作成するためのコード

public class Weather_hourOpenHelper extends SQLiteOpenHelper {
    //SQLiteデータベースの中身(column)
    public Weather_hourOpenHelper(Context context) {
        super(context, table_weather_hour, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE weatherapi_hour2(" +
                "id text not null primary key,"+          //都道府県名
                "cityname text not null,"+          //都道府県名
                "time text not null,"+               //時間
                "day text not null,"+               //日付
                "weather TEXT," +                   //天気
                "temps TEXT," +                     //気温
                "pop TEXT," +                       //降水確率
                "icon TEXT);";                       //天気アイコン

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
