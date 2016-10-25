package com.fuentesfernandez.dropsy.Model;

public class RobotInfo {

    private String robot_model;
    private int robot_id;

    public RobotInfo(String model, int id){
        this.robot_model = model;
        this.robot_id = id;
    }

    public String getRobot_model() {
        return robot_model;
    }
    public int getRobot_id() {
        return robot_id;
    }

}
