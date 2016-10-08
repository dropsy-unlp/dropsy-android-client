package com.fuentesfernandez.dropsy.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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
import java.util.regex.Pattern;

public class RobotManager extends Observable {
    private WebSocket ws;
    private static RobotManager instance;
    private boolean connected = false;
    private List<RobotInfo> robots = new ArrayList<>();
    private Context context;
    private String url;

    private RobotManager(Context context){
        this.context = context;
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
            if (json.has("value")) {
                JSONArray array = json.getJSONArray("value");
                for (int i = 0; i < array.length(); i++) {
                    robots.add(gson.fromJson(array.getString(i), RobotInfo.class));
                }
            } else {
                Log.d("ERROR","Hubo un problema al obtener los robots.");
            }

            ws.setStringCallback(new WebSocket.StringCallback() {
                @Override
                public void onStringAvailable(String s) {
                    Log.d("CLIENTTAG",s);
                }
            });
            setChanged();
            notifyObservers("robots");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        if (!connected || connectionSettingsChanged()) {
            loadConnectionUrl();
            if (url == null) return;
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
            try {
                mAsyncHttpClient.websocket(url, null, mWebSocketConnectCallback);
            } catch (Exception e) {
                Toast.makeText(context, "ubo un problema al conectarse al servidor.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static RobotManager getInstance(Context context){
        if (instance == null){
            instance = new RobotManager(context);
        }
        return instance;
    }

    public void disconnect(){
        robots.clear();
        setChanged();
        notifyObservers("robots");
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

    private void loadConnectionUrl(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = preferences.getString("ip","192.168.0.1");
        String port = preferences.getString("port","8000");
        String path = preferences.getString("path","dropsy");
        url = "ws://" + ip + ":" + port + "/" + path;
        if (Integer.valueOf(port) > 65535 || Integer.valueOf(port) < 1){
            Toast.makeText(context, "Numero de puerto invalido.", Toast.LENGTH_LONG).show();
            url = null;
        }
        if (!validate(ip)){
            Toast.makeText(context, "Direccion de IP invalida.", Toast.LENGTH_LONG).show();
            url = null;
        }

    }

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private boolean connectionSettingsChanged(){
        String oldUrl = url;
        loadConnectionUrl();
        return oldUrl == null || !oldUrl.equals(url);
    }


}
