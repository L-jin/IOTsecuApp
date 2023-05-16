package com.example.myapplication11;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;
    private ListView listViewIp;

    ArrayList<String> ipList;
    ArrayAdapter<String> adapter;

    String subnet = "";
    static final int lower = 0;
    static final int upper = 110;
    static final int timeout = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.scan);
        listViewIp = findViewById(R.id.listviewip);
        TextView textView;
        textView = (TextView) findViewById(R.id.ipAdd);

        ipList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ipList);
        listViewIp.setAdapter(adapter);



        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ip 주소 받아오기
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                textView.setText("Your Device IP Address: " + ipAddress);
                int flag =0;
                for (int i =0; i<ipAddress.length(); i++){
                    char c = ipAddress.charAt(i);

                    if (c == '.') flag++;
                    if(flag ==3) {flag =i+1; break;}
                }
                flag = ipAddress.length()-flag;
                subnet= ipAddress.substring(0,ipAddress.length() - flag);

                Toast.makeText(MainActivity.this, subnet, Toast.LENGTH_LONG).show();

                new ScanIpTask().execute();
            }
        });

    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            ipList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Scanning IP addresses...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;

                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(timeout)) {
                        publishProgress(inetAddress.getHostAddress());
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Done scanning IP addresses", Toast.LENGTH_LONG).show();
        }





    }
}
