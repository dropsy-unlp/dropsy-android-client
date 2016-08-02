package com.fuentesfernandez.dropsy.Service;

import android.util.Log;

public class RobotServiceImpl implements RobotService{

    public void connect(){
        Log.i("RobotServiceImpl", "Connecting to server");
    }

    public void disconnect(){
        Log.i("RobotServiceImpl", "Disconnecting from server");
    }

    public void left(){
        Log.i("RobotServiceImpl", "Moving robot left");
    }

    public void right(){
        Log.i("RobotServiceImpl", "Moving robot right");
    }

    public void forward(int time){
        Log.i("RobotServiceImpl", "Moving robot forward");
    }

    public void backward(int time){
        Log.i("RobotServiceImpl", "Moving robot backward");
    }
}
