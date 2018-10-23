package com.example.hp.wifi_monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView connectivity, state, device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivity = findViewById(R.id.view1);
        state = findViewById(R.id.view2);
        device = findViewById(R.id.view3);
        //connectivity.setMovementMethod(new ScrollingMovementMethod());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.registerReceiver(MyBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver MyBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final PendingResult pendingResult = goAsync();
            Task asyncTask = new Task(pendingResult, intent);
            asyncTask.execute();
        }

        class Task extends AsyncTask<Void, Void, String> {


            private final BroadcastReceiver.PendingResult pendingResult;
            private final Intent intent;
            TextView view;

            private Task(BroadcastReceiver.PendingResult pendingResult, Intent intent) {
                this.pendingResult = pendingResult;
                this.intent = intent;
            }

            @Override
            protected String doInBackground(Void... voids) {

                String action = intent.getAction();
                String extrasString = getExtrasString(intent);
                StringBuilder sb = new StringBuilder();
                sb.append("Action: " + action + "\n");
                sb.append("Details: " + extrasString + "\n");
                String log = sb.toString();
                if (action.equals("android.net.conn.CONNECTIVITY_CHANGE"))
                    view = connectivity;
                else if (action.equals("android.net.wifi.STATE_CHANGE"))
                    view = state;
                else if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED"))
                    view = device;
                return log;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                    view.setText(s);
                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
            }


        }

        private String getExtrasString(Intent pIntent) {
            String extrasString = "";
            Bundle extras = pIntent.getExtras();
            try {
                if (extras != null) {
                    Set<String> keySet = extras.keySet();
                    for (String key : keySet) {
                        try {
                            String extraValue = pIntent.getExtras().get(key).toString();
                            extrasString += key + ": " + extraValue + "\n";
                        } catch (Exception e) {

                            extrasString += key + ": Exception:" + e.getMessage() + "\n";
                        }
                    }
                }
            } catch (Exception e) {

                extrasString += "Exception:" + e.getMessage() + "\n";
            }

            return extrasString;
        }

    };

}
