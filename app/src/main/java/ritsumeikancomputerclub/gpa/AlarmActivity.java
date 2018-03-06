package ritsumeikancomputerclub.gpa;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmActivity extends AppCompatActivity {
    private Vibrator vibrator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000};
        if(vibrator != null) {
            vibrator.vibrate(pattern, 0);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button stop = findViewById(R.id.stop_button);
        stop.setOnClickListener(view -> {
            if(vibrator != null){
                vibrator.cancel();
                vibrator = null;
            }
            finish();
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }
}
