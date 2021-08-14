package jp.javadrive.openweatherapifragmentsqlite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Fragmentクラスを継承します
public class LocationMenuFragment extends Fragment  {

    private FusedLocationProviderClient fusedLocationClient;

    String locality;

    String areaname;
    double lat;
    double lon;
    static LocationMenuFragment newInstance() {
        // インスタンス生成
        LocationMenuFragment locationMenufragment = new LocationMenuFragment();
        return locationMenufragment;
    }

    /**
     * 位置情報取得開始メソッド
     */
    private void startUpdateLocation() {
        // 位置情報取得権限の確認
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 権限がない場合、許可ダイアログ表示
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(getActivity(), permissions, 2000);
            return;
        }

        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        //locationRequest.setInterval(10000);       // 位置情報更新間隔の希望
        //locationRequest.setFastestInterval(5000); // 位置情報更新間隔の最速値
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // この位置情報要求の優先度

        fusedLocationClient.requestLocationUpdates(locationRequest,  new MyLocationCallback(), null);
    }


    /**
     * 位置情報受取コールバッククラス
     */
    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            // 現在値を取得
            Location location = locationResult.getLastLocation();

           Log.v("結果","緯度:" + location.getLatitude() + " \n経度:" + location.getLongitude());


            lat = location.getLatitude();
            lon = location.getLongitude();

            final Handler handler = new Handler();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<Address> addresses = null;
                    try {
                        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                        addresses = gcd.getFromLocation(lat, lon, 1);
                        if (addresses.size() > 0) {
                            String ret = addresses.get(0).toString();
                            Log.d("結果", ret);
                            areaname = addresses.get(0).getAdminArea();
                            Log.d("都道府県名", areaname);
                            locality = addresses.get(0).getLocality();
                            Log.d("市町村名", locality);
                            handler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    TextView locality1 = getView().findViewById(R.id.locality);
                                    locality1.setText("現在地は、\n" +
                                            areaname +  locality + "\nです。");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("DEBUG", "失敗しました");

                    }

                }
            }).start();
        };
    }

    /**
     * 許可ダイアログの結果受取
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }



    // FragmentのViewを生成して返す
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location,
                container, false);

        //位置情報を取得していなければ、ダイアログを表示して、位置情報取得
        if(locality == null) {
            ((MainActivity) getActivity()).Test();
            // LocationClientクラスのインスタンスを生成
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        }
            // 位置情報取得開始
            startUpdateLocation();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonDay = view.findViewById(R.id.CurrentButton);
        buttonDay.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                // BackStackを設定
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.container,
                        LocalAreaCurrentFragment.newInstance(areaname,6,lon,lat));
                fragmentTransaction.commit();
            }
        });

        Button buttonWeek = view.findViewById(R.id.WeekButton);
        buttonWeek.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                // BackStackを設定
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.container,
                        LocalAreaWeekFragment.newInstance(areaname,6));
                fragmentTransaction.commit();
            }
        });

        Button buttonData = view.findViewById(R.id.HourButton);
        buttonData.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                // BackStackを設定
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.container,
                        LocalAreaHourFragment.newInstance(areaname,6));
                fragmentTransaction.commit();
            }
        });
    }
}