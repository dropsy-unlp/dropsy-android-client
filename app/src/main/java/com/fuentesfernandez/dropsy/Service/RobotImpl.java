package com.fuentesfernandez.dropsy.Service;

import android.os.SystemClock;
import android.util.Log;

import com.fuentesfernandez.dropsy.Model.RobotInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RobotImpl implements Robot{

    private RobotInfo info;
    private RobotManager manager;

    public RobotImpl(RobotInfo info, RobotManager manager){
        this.info = info;
        this.manager = manager;
    }

    private void move(String direction, int speed, int time){
        Log.i("RobotManager", "time = " + time);
        if (speed == 0) speed = 50;
        if (time == 0) time = 5;
        List<Object> args = new ArrayList<>();
        args.add(getRobotJSONObject());
        args.add(speed);
        sendMessageToRobot(direction,args);
        Log.i("RobotManager", "Moving robot " + direction);
        delayedStop(time);
    }

    public void left(int speed, int time){
        move("turnLeft",speed,time);
    }

    public void right(int speed, int time){
        move("turnRight",speed,time);
    }

    public void forward(int speed, int time){
        move("forward",speed,time);
    }

    public void backward(int speed, int time){
        move("backward",speed,time);
    }

    private void stop(){
        List<Object> args = new ArrayList<>();
        args.add(getRobotJSONObject());
        sendMessageToRobot("stop",args);
    }

    private void delayedStop(int time){
        SystemClock.sleep(time*1000);
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
