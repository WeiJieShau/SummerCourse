package com.example.socketclient;

import java.net.Socket;

public class SocketManager {
    private static Socket socket;

    public static void setSocket(Socket s) {
        socket = s;
    }

    public static Socket getSocket() {
        return socket;
    }
}
