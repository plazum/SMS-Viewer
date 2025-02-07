package org.zrtech.sms_viewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    public static String password = "sms";
    public static int count = 20;

    private static final int SMS_PERMISSION_CODE = 100;
    private TextView smsTextView;
    private ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        smsTextView = findViewById(R.id.sms);
        smsTextView.setKeyListener(null);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);

        serverManager = new ServerManager(this);
        serverManager.startServer();

        updateAddress();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        WifiReceiver wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, intentFilter);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                querySMS();
            }
        }
    }

    private void querySMS() {
        sms = SmsGetter.text(this);
        smsTextView.setText(sms);
    }*/

    public void setPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置密码");
        builder.setMessage("请输入新密码：");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            final String text = input.getText().toString();
            password = text;
            Toast.makeText(this, "新密码已设置：" + text, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    public void setCount(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置短信显示条数");
        builder.setMessage("请输入需要查看的短信条数：");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(Integer.toString(count));
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            try {
                final int newCount = Integer.parseInt(input.getText().toString());
                if (newCount <= 0) {
                    throw new RuntimeException("数字必须大于0");
                }
                count = newCount;
                final TextView countTextView = findViewById(R.id.count);
                countTextView.setText("短信显示条数：" + count);
            } catch (RuntimeException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void updateAddress() {
        String address = "（暂无Wi-Fi网络下的IP地址，请将手机连接到Wi-Fi网络。）";
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (!networkInterface.getName().contains("wlan"))
                continue;
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    address = "http://" + inetAddress.getHostAddress() + ":8080";
                }
            }
        }

        smsTextView.setText(address);
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAddress();
        }
    }
}