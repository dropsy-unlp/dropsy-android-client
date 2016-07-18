package com.fuentesfernandez.dropsy.Model;

import java.util.Date;

public class Project {
    
    private String name;
    private Date savedDate;
    private int blocksAmount;

    public Project(String name, Date savedDate, int blocksAmount){
        this.name = name;
        this.savedDate = savedDate;
        this.blocksAmount = blocksAmount;
    }

    public String getName() {
        return name;
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public int getBlocksAmount() {
        return blocksAmount;
    }
}
