package ritsumeikancomputerclub.gpa;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private InputMethodManager inputMethodManager = null;
    private Realm realm;
    private SpotModel[] spotModels = {};
    private BackgroundService mService;
    private boolean serviceEnable = false;
    private JSONObject goal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningServiceInfo> serviceInfoList = am.getRunningServices(Integer.MAX_VALUE);
            for(ActivityManager.RunningServiceInfo s : serviceInfoList){
                if(s.service.getClassName().equals(BackgroundService.class.getName())){
                    serviceEnable = true;
                    break;
                }
            }
        }

        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Button button = findViewById(R.id.button);
        if (serviceEnable){
            button.setText("STOP");
        } else {
            button.setText("START");
        }
        button.setOnClickListener(view -> {
            if (serviceEnable){
                mService.removeAlarm(true);
                unbindService(connection);
                stopService(new Intent(getApplicationContext(), BackgroundService.class));
                serviceEnable = false;
                button.setText("START");
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                if (goal != null) {
                    try {
                        String goalName = goal.getString("name");
                        double latitude = goal.getDouble("latitude");
                        double longitude = goal.getDouble("longitude");

                        startService(intent);
                        serviceEnable = true;
                        button.setText("STOP");
                        mService.setAlarm(goalName, latitude, longitude);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        realm = Realm.getDefaultInstance();
        RealmResults<SpotModel> realmResults = realm.where(SpotModel.class).findAll();


//        realmSampleMethod();
//        spotModels = realmResults.toArray(new SpotModel[1]);
//        Log.d("tag",spotModels[0].getUpdatedAt());


        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText searchText = findViewById(R.id.editText);
        searchText.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            // enter button is pushed
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // hide keyboard
                inputMethodManager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                ArrayList result = getApiResult(searchText.getText().toString());

                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        unbindService(connection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));

            return true;
        }

        return false;
    }

    // コネクション作成
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // サービス接続時に呼ばれる
            Log.i("ServiceConnection", "onServiceConnected");
            // BinderからServiceのインスタンスを取得
            mService = ((BackgroundService.ServiceBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // サービス切断時に呼ばれる
            Log.i("ServiceConnection", "onServiceDisconnected");
            mService = null;
        }
    };

    private ArrayList<JSONObject> getApiResult(String query) {
        ArrayList<JSONObject> result = new ArrayList<>();

        ApiClient client = new ApiClient(query);
        client.setListener(createListener());
        client.execute();

        return result;
    }

    private ApiClient.Listener createListener() {
        return (String response) -> {
            try {
                goal = new JSONArray(response).getJSONObject(0);
                Log.d("goal", goal.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        };
    }

    //realmのサンプルメソッド
    public void realmSampleMethod() {

        String updatedAtText  = android.text.format.DateFormat.format("yyyy-MM-dd-kk-mm-ss", Calendar.getInstance()).toString();

        //データの新規登録
        realm.beginTransaction();
        SpotModel spot = realm.createObject(SpotModel.class); // 新たなオブジェクトを作成
        spot.setUuId(1);
        spot.setPrefectureId(0);
        spot.setTransportId(0);
        spot.setName("南草津");
        spot.setLatitude(40.0f);
        spot.setLongitude(130.0f);
        spot.setUpdatedAt(updatedAtText);
        realm.commitTransaction();

    }


}
