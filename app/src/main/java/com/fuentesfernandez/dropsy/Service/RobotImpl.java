package com.fuentesfernandez.dropsy.Service;

import android.os.SystemClock;
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

    public RobotImpl(RobotInfo info, RobotManager manager){
        this.info = info;
        this.manager = manager;
    }

    private void move(String direction, int speed, int time){
        Log.i("RobotManager", "time = " + time);
        if (speed == 0) speed = 50;
        if (time == 0) time = 5000;
        List<Object> args = new ArrayList<>();
        args.add(getRobotJSONObject());
        args.add(speed);
        args.add((double)time/(double)1000);
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
        System.out.println(result);
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
