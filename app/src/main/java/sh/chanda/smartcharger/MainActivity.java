package sh.chanda.smartcharger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView webview;

    EditText ip;

    Button connect, smart;

    TextView notification, charge;

    String url;

    boolean isCharging = false;

    void buttonLabel(){
        if(isCharging){
            smart.setText("Stop Charging");
        }
        else{
            smart.setText("Start Charging");
        }
    }

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        charge = findViewById(R.id.battery);
        notification = findViewById(R.id.notification_label);

        ip = findViewById(R.id.ipAddress);

        smart = findViewById(R.id.button);
        connect = findViewById(R.id.connectButton);

        webview = findViewById(R.id.browser);
        webview.setWebViewClient(new WebViewClient());

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url =  "http://" + ip.getText().toString()+ "/";
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Integer batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                charge.setText(batteryLevel.toString()+"%");
                Log.d("log",batteryLevel.toString()+"%");

                if(batteryLevel > 99){
                    webview.loadUrl(url+"LED=OFF");
                    isCharging = false;
                    notification.setText("Battery Fully Charged!");
                    buttonLabel();
                }

                else if(status == BatteryManager.BATTERY_STATUS_CHARGING){
                    webview.loadUrl(url+"LED=ON");
                    isCharging = true;
                    notification.setText("Battery is Charging...");
                    buttonLabel();
                }

                else if(status == BatteryManager.BATTERY_STATUS_DISCHARGING){
                    webview.loadUrl(url+"LED=DC");
                    isCharging = false;
                    notification.setText("Device is Disconnected!");
                    buttonLabel();
                }

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        smart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCharging){
                    webview.loadUrl(url+"LED=DC");
                    isCharging = false;
                    notification.setText("Device is Disconnected!");
                    buttonLabel();
                }

                else {
                    webview.loadUrl(url+"LED=ON");
                    buttonLabel();
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
}
