package com.fuentesfernandez.dropsy.Service;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.util.AsyncListUtil;
import android.util.Log;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.squareup.duktape.Duktape;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RobotServiceImpl implements RobotService{
    private String generatedCode;
    private WebSocket ws;
    public void connect(){
        AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                ws = webSocket;
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                Duktape duktape = Duktape.create();
                duktape.bind("Robot", RobotService.class, RobotServiceImpl.this);
                duktape.evaluate(generatedCode);
                ws.close();
                ws.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("CLIENTTAG",s);
                    }
                });
            }

        };
        AsyncHttpClient mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
        mAsyncHttpClient.websocket("ws://192.168.0.105:8000/api", null, mWebSocketConnectCallback);
    }

    public void disconnect(){

        Log.i("RobotServiceImpl", "Disconnecting from server");
    }

    private void move(String direction, int speed, int time){
        if (speed == 0) speed = 50;
        if (time == 0) time = 500;
        else time = time * 1000;
        sendMessageToRobot(direction,speed);
        Log.i("RobotServiceImpl", "Moving robot " + direction);
        delayedStop(time);
    }

    public void left(int speed, int time){
        if (speed == 0) speed = 50;
        move("turnLeft",speed,time);
    }

    public void right(int speed, int time){
        if (speed == 0) speed = 50;
        move("turnRight",speed,time);
    }

    public void forward(int speed, int time){
        if (speed == 0) speed = 50;
        move("forward",speed,time);
    }

    public void backward(int speed, int time){
        if (speed == 0) speed = 50;
        move("backward",speed,time);
    }

    private void delayedStop(int time){
        SystemClock.sleep(time);
        stop();
    }

    private void stop(){
        sendMessageToRobot("stop");
    }

    private void sendMessage(String entity, String message, int... args){
        try {
            JSONObject json = new JSONObject();
            json.put("entity", entity);
            json.put("method", message);
            JSONArray array = new JSONArray();
            array.put(getRobotJSONObject());
            for (int s : args){
                array.put(s);
            }
            json.put("args", array);
            ws.send(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGeneratedCode(String code){
        generatedCode = code;
    }

    private void sendMessageToRobot(String msg, int... args){
        sendMessage("robot",msg,args);
    }

    private JSONObject getRobotJSONObject() throws JSONException {
        JSONObject robot = new JSONObject();
        robot.put("robot_model","n6");
        robot.put("robot_id",9);
        return robot;
    }
}
