package com.fuentesfernandez.dropsy.Service;

public interface Robot {

    public void left(int time);

    public void right(int time);

    public void forward(int time);

    public void backward(int time);

    public boolean getObstacle();
}
