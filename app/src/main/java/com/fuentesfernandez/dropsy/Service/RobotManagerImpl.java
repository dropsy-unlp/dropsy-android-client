package com.fuentesfernandez.dropsy.Service;

import android.util.Log;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RobotManagerImpl implements RobotManager {
    private WebSocket ws;
    private static RobotManagerImpl instance;
    private boolean connected = false;
    private List<RobotInfo> robots = new ArrayList<>();

    private RobotManagerImpl(){
    }

    public boolean isConnected(){
        return connected;
    }

    @Override
    public List<RobotInfo> getRobots() {
        return robots;
    }

    private void loadRobots(String robots){
        try {
            Gson gson = new Gson();
            List<RobotInfo> robotInfos = new ArrayList<>();
            JSONObject json = new JSONObject(robots);
            JSONArray array = json.getJSONArray("value");
            for (int i=0; i<array.length(); i++){
                robotInfos.add(gson.fromJson(array.getString(i), RobotInfo.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(String url){
        AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                ws = webSocket;
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                connected = true;
                ws.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("CLIENTTAG",s);
                        loadRobots(s);
                    }
                });
            }

        };
        AsyncHttpClient mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
        mAsyncHttpClient.websocket(url, null, mWebSocketConnectCallback);
    }

    public static RobotManagerImpl getInstance(){
        if (instance == null){
            instance = new RobotManagerImpl();
        }
        return instance;
    }

    public void disconnect(){
        ws.close();
        Log.i("RobotManagerImpl", "Disconnecting from server");
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
