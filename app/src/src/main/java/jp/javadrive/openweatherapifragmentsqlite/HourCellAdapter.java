package jp.javadrive.openweatherapifragmentsqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HourCellAdapter extends ArrayAdapter {
    // 見易さのために定義。普段は直接 getView で指定する。
    private static final int resource = R.layout.custom_table_cell_weather;

    public HourCellAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // super.getView() は 呼ばない(カスタムビューにしているため)
        View view;

        // テンプレート処理。
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        // データをgetItemで取る
        Weatherday weatherday = (Weatherday) getItem(position);

        //for(int i=0;i<8;i++) {
        // カスタムビューの場合はViewが確実にあるtry-catch は不要ためか。

        TextView tv2 = (TextView) view.findViewById(R.id.currentTemp);
        TextView tv3 = (TextView) view.findViewById(R.id.currentPop);


        //List.xmlに表示する情報をセット

        tv2.setText(weatherday.temp + "℃");
        tv3.setText(weatherday.pop + "%");

        ImageView myImage = view.findViewById(R.id.currentIcon);

        String icon = weatherday.icon;
        switch (icon) {

            case "01d":
                myImage.setImageResource(R.drawable._1d);
                break;

            case "01n":
                myImage.setImageResource(R.drawable._01n);
                break;

            case "02d":
                myImage.setImageResource(R.drawable._2d);
                break;

            case "02n":
                myImage.setImageResource(R.drawable._02n);
                break;

            case "03d":
                myImage.setImageResource(R.drawable._3d);
                break;

            case "03n":
                myImage.setImageResource(R.drawable._03n);
                break;

            case "04d":
                myImage.setImageResource(R.drawable._4d);
                break;

            case "04n":
                myImage.setImageResource(R.drawable._04n);
                break;

            case "09d":
                myImage.setImageResource(R.drawable._9d);
                break;

            case "09n":
                myImage.setImageResource(R.drawable._9n);
                break;

            case "10d":
                myImage.setImageResource(R.drawable._10d);
                break;

            case "10n":
                myImage.setImageResource(R.drawable._10n);
                break;

            case "11d":
                myImage.setImageResource(R.drawable._11d);
                break;

            case "11n":
                myImage.setImageResource(R.drawable._11n);
                break;

            case "13d":
                myImage.setImageResource(R.drawable._13d);
                break;

            case "13n":
                myImage.setImageResource(R.drawable._13n);
                break;

            case "50d":
                myImage.setImageResource(R.drawable._50d);
                break;

            case "50n":
                myImage.setImageResource(R.drawable._50n);
                break;

        }
        //}

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

    public void add(String time, String pop, String temp, String weather, String icon, String cityname) {
        Weatherday weatherday = new Weatherday(time, pop, temp, weather, icon, cityname);
        super.add(weatherday);
    }

    // 削除
    public void remove(int index) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        remove(getItem(index));
    }
}
