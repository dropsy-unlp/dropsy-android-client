package com.fuentesfernandez.dropsy.Model;


import java.sql.Date;

public class Project {


    private Long id;
    private String name;
    private String savedDate;
    private String xmlName;

    public Project(){}

    public Project(String name, String xmlName){
        this.name = name;
        this.xmlName = xmlName;
    }

    public Long getId(){ return id; }

    public String getName() {
        return name;
    }

    public String getSavedDate() {
        return savedDate;
    }

    public String getXmlName() {
        return xmlName;
    }

    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSavedDate(String savedDate) {
        this.savedDate = savedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
