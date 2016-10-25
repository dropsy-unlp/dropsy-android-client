package com.fuentesfernandez.dropsy.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Exception.ConnectionLostException;
import com.fuentesfernandez.dropsy.Exception.InterruptionRequestedException;
import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
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
    private String lastReceivedMesssage;
    private boolean interruptionRequested = false;

    private RobotManager(Context context){
        this.context = context;
    }

    public boolean isConnected(){
        return connected;
    }

    public List<RobotInfo> getRobots(){
        return robots;
    }

    public List<RobotInfo> loadRobots(){
        sendMessage("global", "get_robots", new ArrayList<>());
        String result = waitForReply();
        return loadRobots(result);
    }

    private List<RobotInfo> loadRobots(String response){
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
            setChanged();
            notifyObservers("robots");
            return robots;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void connect(){
        loadConnectionUrl();
        if (url == null) return;
        AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                ws = webSocket;
                if (ex != null) {
                    ex.printStackTrace();
                    setChanged();
                    notifyObservers("disconnection");
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
                        ws.setStringCallback(new ServerMessageCallback());
                    }
                });

                ws.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        disconnect();
                    }
                });

                ws.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        disconnect();
                    }
                });
            }

        };
        AsyncHttpClient mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
        try {
            mAsyncHttpClient.websocket(url, null, mWebSocketConnectCallback);
        } catch (Exception e) {
            Toast.makeText(context, "Hubo un problema al conectarse al servidor.", Toast.LENGTH_LONG).show();
        }
    }

    public static RobotManager newInstance(Context context){
        instance = new RobotManager(context);
        return instance;
    }

    public static RobotManager getInstance(){
        return instance;
    }

    public void disconnect(){
        robots.clear();
        setChanged();
        notifyObservers("robots");
        setChanged();
        notifyObservers("disconnection");
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

    public Long reserveRobot(String robotModel, int robotId){
        try {
            List<Object> args = new ArrayList<>();
            args.add(robotModel);
            args.add(robotId);
            args.add(300);
            lastReceivedMesssage = null;
            sendMessage("global","reserve",args);
            String result = waitForReply();
            JSONObject json;
            json = new JSONObject(result);
            if (json.has("value")){
                JSONObject value = json.getJSONObject("value");
                return value.getLong("reservation_id");
            }
        } catch (JSONException | ConnectionLostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void releaseRobot(Long reservationId){
        List<Object> args = new ArrayList<>();
        args.add(reservationId);
        sendMessage("global","release",args);
        waitForReply();
    }

    public String waitForReply(){
        String result;
        while ((result = getLastReceivedMesssage()) == null){
            try {
                Thread.sleep(250);
                if (!isConnected()){
                    throw new ConnectionLostException();
                } else if (isInterruptionRequested()){
                    throw new InterruptionRequestedException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
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

    public boolean connectionSettingsChanged(){
        String oldUrl = url;
        loadConnectionUrl();
        return oldUrl == null || !oldUrl.equals(url);
    }

    public String getLastReceivedMesssage(){
        String s;
        if ((s = lastReceivedMesssage) == null) return null;
        lastReceivedMesssage = null;
        return s;
    }

    public boolean isInterruptionRequested(){
        boolean aux = interruptionRequested;
        interruptionRequested = false;
        return aux;
    }

    public void requestInterruption(){
        interruptionRequested = true;
    }

    private class ServerMessageCallback implements WebSocket.StringCallback {
        @Override
        public void onStringAvailable(String s) {
            lastReceivedMesssage = s;
        }
    }


}
