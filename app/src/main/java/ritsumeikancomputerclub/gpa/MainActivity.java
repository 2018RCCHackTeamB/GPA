package ritsumeikancomputerclub.gpa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private InputMethodManager inputMethodManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button start = findViewById(R.id.button);
        start.setOnClickListener(view -> startService(new Intent(getApplicationContext(), BackgroundService.class)));

        inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText searchText = findViewById(R.id.editText);
        searchText.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            // enter button is pushed
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                // hide keyboard
                inputMethodManager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                ArrayList result = getApiResult(searchText.getText().toString());

                return true;
            }
            return false;
        });
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

    private ArrayList<JSONObject> getApiResult(String query){
        ArrayList<JSONObject> result = new ArrayList<>();

        return result;
    }
}
