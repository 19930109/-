package jp.javadrive.openweatherapifragmentsqlite;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class MyProgressDialog extends DialogFragment {

    public MyProgressDialog(){} //空のコンストラクタ（DialogFragmentのお約束）

    //インスタンス作成
    public static MyProgressDialog newInstance() {
        return new MyProgressDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ブロードキャストを受信するように登録
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_FINISH_UPDATING);
        getActivity().registerReceiver(br, intentFilter);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        dialog.setTitle("更新中");
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(br);   //ブロードキャストの登録解除
    }

    //ダイアログを閉じる
    private void closeDialog() {
        dismissAllowingStateLoss(); //ダイアログを強制的に閉じる
    }

    //ブロードキャストレシーバー
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action!=null && action.equals(MainActivity.ACTION_FINISH_UPDATING)) {
                closeDialog();
            }
        }
    };
}
