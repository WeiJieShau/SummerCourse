//Client
package com.example.socketclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class TCPClientActivity extends AppCompatActivity {
    private TextView tvMessages,tvName;
    private EditText etMessage;
    private String message;
    private Thread mThread = null;

    private String SERVER_IP;
    private String Client_Name;
    private int SERVER_PORT;

    private BufferedReader reader;
    private Socket socket;
    private String tmp;
    private PrintWriter out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        Button btnLeave;
        tvName=findViewById(R.id.tvClientName);
        btnLeave=findViewById(R.id.btnLeave);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        SERVER_IP=bundle.getString("ServerIp");
        SERVER_PORT=bundle.getInt("ServerPort");
        Client_Name=bundle.getString("ClientName");
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        tvMessages.setText("");
        tvName.setText("Welcome Client : "+Client_Name);

        mThread = new Thread(new clientThread());
        mThread.start();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if (!message.isEmpty()) {
                    new Thread(new TCPClientActivity.SendData(message)).start();
                    etMessage.setText("");
                }
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(socket != null){
                    String leaveMessage =  "left the chat";
                    new Thread(new SendData(leaveMessage)).start();

                    try {
                        Thread.sleep(1000);
                        socket.shutdownInput();
                        socket.shutdownOutput();

                        InputStream in = socket.getInputStream();
                        OutputStream ou = socket.getOutputStream();

                        in.close();
                        ou.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    SocketManager.setSocket(socket);

                    Intent i =new Intent();
                    i.setClass(TCPClientActivity.this,MainActivity.class);
                    startActivity(i);

                }


            }
        });
    }

    class clientThread implements Runnable {
        public void run() {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                if(socket.isConnected()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.setText("Connected\n");
                            String conn_msg="Connected\n";
                            new Thread(new TCPClientActivity.SendData(conn_msg)).start();

                        }
                    });
                    // 取得網路輸入串流
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //取得網路輸出串流
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    while ((tmp = reader.readLine()) != null) {
                        JSONObject json = new JSONObject(tmp);
                        String clientName = json.getString("name");
                        String clientMessage = json.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append(clientName + ": " + clientMessage + "\n");
                            }
                        });
                    }

                }
            } catch (IOException e) {
                tvMessages.setText(e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class SendData implements Runnable {
        private String message;
        SendData(String message) {
            this.message = message;
        }
        @Override
        public void run() {

            try {
                JSONObject json = new JSONObject();
                json.put("name", Client_Name);
                json.put("message", message);

                String jsonString = json.toString() + "\n";

                OutputStreamWriter outputSW = new OutputStreamWriter(socket.getOutputStream());
                BufferedWriter bufferWriter = new BufferedWriter(outputSW);

                bufferWriter.write(jsonString);
                bufferWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}