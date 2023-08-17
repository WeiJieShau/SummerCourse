package com.example.socketclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
//1131

    private EditText etIP, etPort,etName;
    private String SERVER_IP;
    private int SERVER_PORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        etName = findViewById(R.id.etName);
        Socket receivedSocket = SocketManager.getSocket();
        if (receivedSocket != null) {
            try {
                receivedSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            receivedSocket = null;
            SocketManager.setSocket(null);
        }

        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SERVER_IP = etIP.getText().toString();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("ServerIp",SERVER_IP);
                bundle.putInt("ServerPort",SERVER_PORT);
                bundle.putString("ClientName",etName.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this,TCPClientActivity.class);
                startActivity(intent);


            }
        });

    }


}