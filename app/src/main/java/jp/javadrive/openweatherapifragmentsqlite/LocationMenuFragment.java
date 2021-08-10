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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Fragmentクラスを継承します
public class LocationMenuFragment extends Fragment implements LocationListener {

    private LocationManager mLocationManager;
    private String bestProvider;

    String areaname;
    double lat;
    double lon;
    static LocationMenuFragment newInstance() {
        // インスタンス生成
        LocationMenuFragment locationMenufragment = new LocationMenuFragment();
        return locationMenufragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        initLocationManager();
        locationStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationStop();
    }
    private void initLocationManager() {
        // インスタンス生成

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 詳細設定
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        bestProvider = mLocationManager.getBestProvider(criteria, true);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可を取得する
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
    }

    private void locationStart() {
        checkPermission();
        mLocationManager.requestLocationUpdates(bestProvider, 60000, 3, this);
    }

    private void locationStop() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("DEBUG", "called onLocationChanged");
        Log.d("DEBUG", "lat : " + location.getLatitude());
        Log.d("DEBUG", "lon : " + location.getLongitude());

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
                        String locality = addresses.get(0).getLocality();
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
                    String finalmessage = "位置情報取得に失敗しました";
                    handler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            String finalMessage = finalmessage;
                            Optional.ofNullable(getActivity())
                                    .filter(activity -> activity instanceof LocalAreaWeekFragment.OnCurrentListener)
                                    .map(activity -> (LocalAreaWeekFragment.OnCurrentListener) activity)
                                    .orElseThrow(() -> new IllegalStateException("ActivityにOnListenerを実装してください"))
                                    .onDaialog(finalMessage);
                        }
                    });
                }

            }
        }).start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("DEBUG", "called onStatusChanged");
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("DEBUG", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("DEBUG", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("DEBUG", "TEMPORARILY_UNAVAILABLE");
                break;
            default:
                Log.d("DEBUG", "DEFAULT");
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("DEBUG", "called onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("DEBUG", "called onProviderEnabled");
    }

    // FragmentのViewを生成して返す
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_location,
                container, false);
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