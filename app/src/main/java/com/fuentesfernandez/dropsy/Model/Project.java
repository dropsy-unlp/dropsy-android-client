package com.fuentesfernandez.dropsy.Model;

import java.util.Date;

public class Project {
    
    private String name;
    private Date savedDate;
    private String xmlName;

    public Project(String name, Date savedDate, String xmlName){
        this.name = name;
        this.savedDate = savedDate;
        this.xmlName = xmlName;
    }

    public String getName() {
        return name;
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public String getXmlName() {
        return xmlName;
    }
}
