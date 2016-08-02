package com.fuentesfernandez.dropsy.Service;

import android.util.Log;

public interface RobotService {

    public void connect();

    public void disconnect();

    public void left();

    public void right();

    public void forward(int time);

    public void backward(int time);
}
