package com.fuentesfernandez.dropsy.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fuentesfernandez.dropsy.Model.RobotInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RobotImpl implements Robot{

    private RobotInfo info;
    private RobotManager manager;
    private float rotationSensitivity;
    private float speedSensitivity;
    private static final int rotationSpeed = 40;
    private static final int defaultSpeed = 50;

    public RobotImpl(RobotInfo info, RobotManager manager,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        rotationSensitivity = Float.valueOf(preferences.getString("rotation_sensitivity","2"));
        speedSensitivity = Float.valueOf(preferences.getString("speed","1"));
        this.info = info;
        this.manager = manager;
    }

    private void move(String direction, float speed, float time){
        Log.i("RobotManager", "time = " + time);
        if (speed == 0) speed = 50;
        if (time == 0) time = 5000;
        List<Object> args = new ArrayList<>();
        args.add(getRobotJSONObject());
        args.add(speed);
        args.add(time/(float)1000);
        sendMessageToRobot(direction,args);
        Log.i("RobotManager", "Moving robot " + direction);
        waitForReply();
        //delayedStop(time);
    }

    private void waitForReply(){
        String result;
        while ((result = RobotManager.getInstance().getLastReceivedMesssage()) == null){
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("ServerResponse", result);
    }

    public void left(int time){
        move("turnLeft",rotationSpeed,rotationSensitivity*(float)time);
    }

    public void right(int time){
        move("turnRight",rotationSpeed,rotationSensitivity*(float)time);
    }

    public void forward(int time){
        move("forward",defaultSpeed * speedSensitivity,time);
    }

    public void backward(int time){
        move("backward",defaultSpeed * speedSensitivity,time);
    }

    private void stop(){
        List<Object> args = new ArrayList<>();
        args.add(getRobotJSONObject());
        sendMessageToRobot("stop",args);
    }

    private void delayedStop(int time){
        SystemClock.sleep(time);
        stop();
    }

    private void sendMessageToRobot(String msg, List<Object> args){
        manager.sendMessage("robot",msg,args);
    }

    private JSONObject getRobotJSONObject()  {
        JSONObject robot = new JSONObject();
        try {
            robot.put("robot_model",info.getRobot_model());
            robot.put("robot_id",info.getRobot_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return robot;
    }



}
