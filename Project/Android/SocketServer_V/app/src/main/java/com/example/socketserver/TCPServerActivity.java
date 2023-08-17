//Server
package com.example.socketserver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServerActivity extends AppCompatActivity {

    private List<String> connectedClients = new ArrayList<>();
    private List<Socket> connectedSockets = new ArrayList<>();
    private Thread mThread = null;
    private Thread oThread = null;
    private TextView tvMessages;
    private EditText etMessage;
    private int SERVER_PORT = 8100; //傳送埠號
    private String message;
    private BufferedReader reader;
    private Socket socket;
    private String tmp;
    private PrintWriter out;
    private String ServerName;
    private Button btn_ConnectingClient;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpserver);
        TextView tvIP = findViewById(R.id.tvIP);
        TextView tvPort = findViewById(R.id.tvPort);
        TextView tvServerName = findViewById(R.id.tvServerName);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btn_ConnectingClient=findViewById(R.id.conn_Client);
        btnSend = findViewById(R.id.btnSend);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ServerName=bundle.getString("ServerName");
        String SERVER_IP = null;

        try {
            SERVER_IP = getLocalIpAddress();
            tvIP.setText("IP: " + SERVER_IP);

            tvServerName.setText("Welcome Server : "+ ServerName);
            tvMessages.append("Not connected\n");
            tvPort.setText("Port: " + String.valueOf(SERVER_PORT));

            mThread = new Thread(new serverThread());
            mThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if (!message.isEmpty()) {
                    new Thread(new SendData(ServerName,message)).start();
                    etMessage.setText("");
                }
            }
        });
        btn_ConnectingClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnectedClientsDialog();
            }
        });
    }

    private void showConnectedClientsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connected Clients");

        StringBuilder stringBuilder = new StringBuilder();
        for (String clientName : connectedClients) {
            stringBuilder.append(clientName).append("\n");
        }

        builder.setMessage(stringBuilder.toString());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class serverThread implements Runnable {

        @Override
        public void run() {

            try {
                ServerSocket serverSocket ;
                // Create a list to keep track of connected clients
                serverSocket = new ServerSocket(SERVER_PORT);
                while (true) {
                    Log.d("Socket","in");
                    socket = serverSocket.accept(); // Accept new client connection

                     // Add the new client socket to the list
                    connectedSockets.add(socket);

                    // Handle client messages in a separate thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.append("Connected\n");
                        }
                    });

                    Log.d("Socket","out");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new ChatThread(socket).run();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    public class ChatThread  implements Runnable{
        Socket client;
        BufferedReader br;
        public ChatThread(Socket c) throws IOException, JSONException {
            this.client=c;
            // 取得網路輸入串流
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //取得網路輸出串流
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())),
                    true);

        }
        String clientName=null;
        String clientMessage=null;
        @Override
        public void run() {
            try{
                while ((tmp = br.readLine()) != null) {
                    try {
                        JSONObject json = new JSONObject(tmp);
                        clientName = json.getString("name");
                        clientMessage = json.getString("message");
                        if(!connectedClients.contains(clientName)) connectedClients.add(clientName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Thread(new SendData(clientName,clientMessage)).start();
                        }
                    });
            }
                // 客戶端斷開連接
                connectedClients.remove(clientName); // 從連接列表中刪除連接的 Name
                connectedSockets.remove(client);
                client.close(); // 關閉客戶端 Socket
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void broadcastMessage(String message) {
        for (Socket clientSocket : connectedSockets) {
            try {
                OutputStreamWriter outputSW = new OutputStreamWriter(clientSocket.getOutputStream());
                BufferedWriter bufferWriter = new BufferedWriter(outputSW);

                bufferWriter.write(message);
                bufferWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class SendData implements Runnable {
        private String message;
        private String name;
        SendData(String name,String message) {
            this.message = message;
            this.name=name;
        }
        @Override
        public void run() {

            try {

                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("message", message);
                String jsonString = json.toString() + "\n";
                broadcastMessage(jsonString);

            }  catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append(name + " : " + message + "\n");
                }
            });

        }
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        mThread.interrupt();
    }
}

