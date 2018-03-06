package ritsumeikancomputerclub.gpa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Noth on 2018/03/06.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent notification = new Intent(context, AlarmActivity.class);
        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notification);
    }
}
