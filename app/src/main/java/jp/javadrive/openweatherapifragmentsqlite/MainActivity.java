package jp.javadrive.openweatherapifragmentsqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements LocalAreaDataFragment.OnCurrentListener,
        LocalAreaCurrentFragment.OnCurrentListener,  LocalAreaWeekFragment.OnCurrentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            // コードからFragmentを追加

            // Fragmentを作成します
            MainFragment fragment1 = new MainFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            // Fragmentの追加や削除といった変更を行う際は、Transactionを利用します
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // BackStackを設定
            transaction.addToBackStack(null);

            // 新しく追加を行うのでaddを使用します
            // 他にも、よく使う操作で、replace removeといったメソッドがあります
            // メソッドの1つ目の引数は対象のViewGroupのID、2つ目の引数は追加するfragment
            transaction.replace(R.id.container, fragment1.newInstance());

            // 最後にcommitを使用することで変更を反映します
            transaction.commit();
        }

    }



    @Override
    public void onDaialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("時間を開けて再度、取得してください")
                .setTitle(message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.show();
    }

}