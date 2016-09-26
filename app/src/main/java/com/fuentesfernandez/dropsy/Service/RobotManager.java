package com.fuentesfernandez.dropsy.Service;

import android.util.Log;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class RobotManager extends Observable {
    private WebSocket ws;
    private static RobotManager instance;
    private boolean connected = false;
    private List<RobotInfo> robots = new ArrayList<>();

    private RobotManager(){
    }

    public boolean isConnected(){
        return connected;
    }

    public List<RobotInfo> getRobots() {
        return robots;
    }

    private void loadRobots(String response){
        try {
            robots.clear();
            Gson gson = new Gson();
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("value");
            for (int i=0; i<array.length(); i++){
                robots.add(gson.fromJson(array.getString(i), RobotInfo.class));
            }

            ws.setStringCallback(new WebSocket.StringCallback() {
                @Override
                public void onStringAvailable(String s) {
                    Log.d("CLIENTTAG",s);
                }
            });
            notifyObservers("robots");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(String url){
        connected = false;

        if (!connected) {
            AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
                @Override
                public void onCompleted(Exception ex, WebSocket webSocket) {
                    ws = webSocket;
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    connected = true;
                    setChanged();
                    notifyObservers("connection");
                    sendMessage("global", "get_robots", new ArrayList<>());
                    ws.setStringCallback(new WebSocket.StringCallback() {
                        @Override
                        public void onStringAvailable(String s) {
                            loadRobots(s);
                            Log.d("CLIENTTAG", s);
                        }
                    });
                }

            };
            AsyncHttpClient mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
            mAsyncHttpClient.websocket(url, null, mWebSocketConnectCallback);
        }
    }

    public static RobotManager getInstance(){
        if (instance == null){
            instance = new RobotManager();
        }
        return instance;
    }

    public void disconnect(){
        ws.close();
        connected = false;
        Log.i("RobotManager", "Disconnecting from server");
    }

    public void sendMessage(String entity, String message, List<Object> args){
        try {
            JSONObject json = new JSONObject();
            json.put("entity", entity);
            json.put("method", message);
            JSONArray array = new JSONArray();
            for (Object s : args){
                array.put(s);
            }
            json.put("args", array);
            ws.send(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
