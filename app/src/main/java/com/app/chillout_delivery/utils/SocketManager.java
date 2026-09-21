package com.app.chillout_delivery.utils;

import com.app.chillout_delivery.listener.OrderReceivedListener;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private Socket mSocket;
    private OrderReceivedListener listener;

    public SocketManager(OrderReceivedListener listener) {
        this.listener = listener;
    }

    public void connectSocket() {
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"}; // important
            mSocket = IO.socket("https://socketv2.theyellowgame.com?clientKey=chillout", options);
            mSocket.connect();
            /*mSocket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("✅Check_JK Connected to server");
            });*/
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("systemMessage", getRoomJoin);
            mSocket.on(Socket.EVENT_DISCONNECT, args -> {
                System.out.println("❌Check_JK Disconnected");
            });
            // Listening to custom event
            mSocket.on("user_1", onPaymentStatus);

            // Custom event from server
            mSocket.on("message", args -> {
                System.out.println("📩Check_JK Message: " + args[0]);
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit("message", msg);
        }
    }

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    // Listener function
    private Emitter.Listener onPaymentStatus = args -> {
        try {
            JSONObject data = (JSONObject) args[0];
            String messageStr = data.getString("message");
            JSONObject jsonObject = new JSONObject(messageStr);
            String name = jsonObject.getString("name");
            String app = jsonObject.getString("app");
            System.out.println("Check_JK data: "+data.toString());
            System.out.println("App: " + app + "\n" + "Name: " + name);
            listener.onOrderReceived();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                // Prepare JSON data
                JSONObject data = new JSONObject();
                data.put("room", 1);     // Room name
                data.put("clientKey", "chillout"); // User ID

                System.out.println("✅Check_JK Connected to server Room: "+data.getString("room")+" clientKey: "+data.getString("clientKey"));

                // Emit joinRoom event
                if (mSocket != null) {
                    mSocket.emit("joinRoom", data);
                }
                System.out.println("✅Check_JK Connected to server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener getRoomJoin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                String data = args[0].toString();
                System.out.println("✅Check_JK getRoomJoin : "+data);
            } catch (ClassCastException exp) {
                System.out.println("✅Check_JK getRoomJoin Catch1: "+exp.getMessage());
            } catch (Exception exp) {
                System.out.println("✅Check_JK getRoomJoin Catch2: "+exp.getMessage());
            }
        }
    };

}