package com.fuentesfernandez.dropsy.Service;

import android.util.Log;

import com.fuentesfernandez.dropsy.Model.RobotInfo;

import java.util.List;

public interface RobotManager {

    public void connect(String url);

    public void disconnect();

    public boolean isConnected();

    public List<RobotInfo> getRobots();

    public void sendMessage(String entity, String message, List<Object> args);

}
