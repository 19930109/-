package jp.javadrive.openweatherapifragmentsqlite;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HourAdapter extends ArrayAdapter {
    private GridView gridView;
    private HourCellAdapter cellAdapter;
    AllWeatherFragment allWeatherfragment = new AllWeatherFragment();


    // 見易さのために定義。普段は直接 getView で指定する。
    private static final int resource = R.layout.custom_table_listview_weather;

    public HourAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // super.getView() は 呼ばない(カスタムビューにしているため)
        View view;

        Bundle args = allWeatherfragment.getArguments();
        assert args != null;
        int cnt = args.getInt("cnt");
        String area = args.getString("area");
        int cnt0 = args.getInt("cnt2");


        // テンプレート処理。
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        gridView = view.findViewById(R.id.GridView);
        cellAdapter = new HourCellAdapter(getContext());
        gridView.setAdapter(cellAdapter);
        TextView cityName = view.findViewById(R.id.cityname);


            String str = (String) getItem(position);
            try {
                JSONArray json = new JSONArray(str);

            String pop = null;
            String temp = null;
            String time = null;
            String weather = null;
            String Icon = null;
                //

        for (int i = 8*(position + cnt0); i < 8*(position + cnt0)+8; i++) {
            //1日ごとにデータを取得する
            JSONObject dayjson = new JSONObject(json.getString(i));

            String cityname = dayjson.getString("cityname");
            cityName.setText(cityname);

            //時間をセット
            time = dayjson.getString("day");

            //weather(天気)の取り出し・セット
            weather = dayjson.getString("weather");

            //tempの取り出し
            temp = String.valueOf(Double.valueOf(dayjson.getString("temps")));
            //小数点第二位を四捨五入
            double doubletemp = Double.parseDouble(temp);
            temp = String.format("%.1f", doubletemp);

            //pop(降水確率)の取り出し
            pop = String.valueOf(Double.valueOf(dayjson.getString("pop")));
            //*100で％の単位に変換
            double doublepop = Double.parseDouble(pop);
            pop = String.valueOf(doublepop);

            //アイコンの取り出し
            Icon = dayjson.getString("icon");

            // Handlerを使用してメイン(UI)スレッドに処理を依頼する
            String finalPop = pop;
            String finalTemp = temp;
            String finalTime = time;
            String finalWeather = weather;
            String finalIcon = Icon;

            cellAdapter.add(finalTime, finalPop, finalTemp, finalWeather, finalIcon, cityname);
        }
        }catch (JSONException e) {
                e.printStackTrace();
            }




        //TextView cityName = (TextView) view.findViewById(R.id.cityname);
          // cityName.setText(weatherday.cityname);

          // cellAdapter.add(weatherday.weather, weatherday.pop, weatherday.temp, weatherday.weather, weatherday.icon, weatherday.cityname);

        return view;
    }

    // 設定されている CustomListItem の ArrayList を返す。
// 縦横切替などでデータを移行するために使う。
    public ArrayList<Weatherday> getItemList() {
        // 今回は Bundle#putParcelableArrayList() を使うことを想定する。
        // 必要に応じて Bundle#putSparseParcelableArray() を使ってもいい。

        int size = getCount();
        ArrayList<Weatherday> weatherdayList = new ArrayList<Weatherday>(size);
        for (int index = 0; index < size; index++) {
            weatherdayList.add((Weatherday) getItem(index));
        }
        return weatherdayList;
    }

    // Bundleから復元するときに必要になるはず。
    public void addAll(ArrayList<Weatherday> parcelableArrayList) {
// 強制でキャスト。落ちる場合は、設計か実装が間違っている。
        @SuppressWarnings("unchecked")
        ArrayList<Weatherday> weatherdayList = (ArrayList<Weatherday>) parcelableArrayList;
        super.addAll(weatherdayList);
    }

    public void add(String str,int cnt,int cnt1) {
        super.add(str);


        Bundle barg = new Bundle();
        barg.putInt("cnt", cnt);
        barg.putInt("cnt2", cnt1);
        allWeatherfragment.setArguments(barg);
        //Log.d("qq01", str);
    }

    // 削除
    public void remove(int index) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        remove(getItem(index));
    }
}

