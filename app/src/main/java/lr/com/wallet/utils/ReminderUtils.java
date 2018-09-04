package lr.com.wallet.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.hunter.wallet.service.SecurityService;

import lr.com.wallet.R;
import lr.com.wallet.activity.ImportActivity;
import lr.com.wallet.activity.MainFragmentActivity;

/**
 * Created by DT0814 on 2018/9/4.
 */

public class ReminderUtils {
    public static void showReminder(Activity activity) {
        AlertDialog.Builder reminderBuilder = new AlertDialog.Builder(activity);
        View reminderView = activity.getLayoutInflater().inflate(R.layout.reminder_layout, null);
        reminderBuilder.setView(reminderView);
        AlertDialog show = reminderBuilder.show();
        show.setCancelable(false);
        reminderView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainFragmentActivity.class);
                intent.putExtra("position", 1);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        reminderView.findViewById(R.id.agreeBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityService.shutdownOtherApp(activity);
                show.dismiss();
            }
        });
    }
}
