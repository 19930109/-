package jp.javadrive.openweatherapifragmentsqlite;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//APIを取得するためにURLに接続して文字列として読み込む(非同期処理)

public class HourAsyncHttpRequest extends AsyncTask<URL, Void, JSONObject> {
    private MainFragment mainFragment;
    private String cityname;
    private String id;

    public HourAsyncHttpRequest(MainFragment fragment, String id, String cityname) {
        // 呼び出し元のアクティビティ
        this.mainFragment = fragment;
        this.cityname = cityname;
        this.id = id;
    }
    /**
     * 非同期処理で天気情報を取得する.
     *
     * @param urls 接続先のURL
     * @return 取得した天気情報
     */
    @Override
    protected JSONObject doInBackground(URL... urls) {

        final URL url = urls[0];
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);
            con.connect();

            final int statusCode = con.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                System.err.println("正常に接続できていません。statusCode:" + statusCode);
                return null;
            }

            // レスポンス(JSON文字列)を読み込む準備
            final InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            if (null == encoding) {
                encoding = "UTF-8";
            }
            final InputStreamReader inReader = new InputStreamReader(in, encoding);
            final BufferedReader bufReader = new BufferedReader(inReader);
            StringBuilder response = new StringBuilder();
            String line = null;
            // 1行ずつ読み込む
            while ((line = bufReader.readLine()) != null) {
                response.append(line);
                //Log.d("q",line);
            }
            bufReader.close();
            inReader.close();
            in.close();


            // 受け取ったJSON文字列をパース
            JSONObject jsonObject = new JSONObject(response.toString());

            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * 非同期処理が終わった後の処理.
     *
     * @param result 非同期処理の結果得られる文字列
     */
    @Override
    protected void onPostExecute(JSONObject result) {

        //SQLiteに保管するため、insert用のjavaへ
        APIInsert_hourService insertService=new APIInsert_hourService();
        try {
            insertService.insertDB(this.mainFragment,this.cityname,this.id,result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
