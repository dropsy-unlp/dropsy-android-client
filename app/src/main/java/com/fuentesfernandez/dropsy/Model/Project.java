package com.fuentesfernandez.dropsy.Model;


public class Project {


    private Long id;
    private String name;
    private String savedDate;
    private String xmlName;
    private int blocksCount = 0;

    public Project(){}

    public Project(String name, String xmlName, int blocksCount){
        this.name = name;
        this.xmlName = xmlName;
        this.blocksCount = blocksCount;
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

    public int getBlocksCount() {
        return blocksCount;
    }

    public void setBlocksCount(int blocksCount) {
        this.blocksCount = blocksCount;
    }

}
