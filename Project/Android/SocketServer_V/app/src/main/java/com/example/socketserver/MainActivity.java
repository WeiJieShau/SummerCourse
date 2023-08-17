package com.example.socketserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button btn_tp_server;
    EditText server_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_tp_server = findViewById(R.id.btn_tp_server);
        server_name=findViewById(R.id.etServerName);

        btn_tp_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server_name_str = server_name.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("ServerName",server_name_str);
                Intent i = new Intent(MainActivity.this,TCPServerActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

    }





}