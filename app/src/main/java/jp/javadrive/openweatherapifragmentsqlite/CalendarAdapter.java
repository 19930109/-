package jp.javadrive.openweatherapifragmentsqlite;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends ArrayAdapter {
    public List<Date> dateArray = new ArrayList();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;

    // 見易さのために定義。普段は直接 getView で指定する。
    private static final int resource = R.layout.calendar;

    public CalendarAdapter(Context context) {
        super(context, 0);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
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

        TextView dateText = view.findViewById(R.id.dateText);
        TextView tempText = view.findViewById(R.id.tempText);
        TextView popText = view.findViewById(R.id.popText);
        ImageView myImage = view.findViewById(R.id.currentIcon);


            Weatherday weatherday = (Weatherday) getItem(position);
            String time =weatherday.day;

        //日付のみ表示させる
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("d", Locale.US);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("M/d", Locale.US);
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy.MM", Locale.US);
        String day3 = dateFormat3.format(dateArray.get(position));
        String day4 = dateFormat2.format(dateArray.get(position));
        String day1 = dateFormat1.format(dateArray.get(position));
        String day2 = getTitle();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
            String day = dateFormat.format(dateArray.get(position));
            if (time.equals(day)) {

                dateText.setText(day4);

                tempText.setText(weatherday.temp + "℃");
                popText.setText(weatherday.pop + "%");
                Log.d("TAG", time);

                String icon=weatherday.icon;
                switch (icon){

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
            }else{


            }

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        dateText.setTextColor(colorId);

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp, (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        view.setLayoutParams(params);



        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            view.setBackgroundColor(Color.WHITE);
        }else {
            view.setBackgroundColor(Color.LTGRAY);
        }

        return view;
    }

    //表示月を取得
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
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

    public void add(String day, String pop, String temp, String weather, String icon, String cityname) {
        Weatherday weatherday = new Weatherday(day, pop, temp, weather, icon, cityname);
        super.add(weatherday);
        //Log.d("TAG1", weatherday.day);
    }

    // 削除
    public void remove(int index) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        remove(getItem(index));
    }
}
