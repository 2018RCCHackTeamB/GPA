package ritsumeikancomputerclub.gpa;

import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Noth on 2018/03/07.
 */

public class ApiClient extends AsyncTask<Void, Void, String> {
    private String query;
    private Listener listener;

    public ApiClient(String query){
        this.query = query;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection con = null;
        URL url = null;
        StringBuilder result = new StringBuilder();
        String urlSt = "http://rcc.kanon-k4.com/api/search.php?search_name=";

        try {
            urlSt += URLEncoder.encode(query, "utf-8");
            Log.d("url", urlSt);

            // URLの作成
            url = new URL(urlSt);
            // 接続用HttpURLConnectionオブジェクト作成
            con = (HttpURLConnection)url.openConnection();
            // リクエストメソッドの設定
            con.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);
            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);

            // 接続
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // テキストを取得する
                final InputStream in = con.getInputStream();
                final String encoding = con.getContentEncoding();
                final InputStreamReader inReader = new InputStreamReader(in, (encoding == null) ? "utf-8" : encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while ((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("response", result.toString());

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null){
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener){
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String response);
    }
}
