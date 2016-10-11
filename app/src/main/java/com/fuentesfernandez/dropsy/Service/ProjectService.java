package com.fuentesfernandez.dropsy.Service;

import android.content.Context;

import com.fuentesfernandez.dropsy.DAO.ProjectDAO;
import com.fuentesfernandez.dropsy.Model.Project;

import java.util.Date;
import java.util.List;

public class ProjectService {
    private ProjectDAO projectDAO;

    public ProjectService(Context context){
        projectDAO = new ProjectDAO(context);
    }

    public Project getProject(Long id){
        return projectDAO.getProject(id);
    }

    public void saveProject(Project project){
        project.setSavedDate(calculateDate());
        projectDAO.saveProject(project);
    }

    public List<Project> getAllProjects(){
        return projectDAO.getProjects();
    }

    private String calculateDate(){
        Date date = new Date();
        StringBuilder builder = new StringBuilder();
        builder.append(date.getYear() + 1900).append("-");
        builder.append(date.getMonth() + 1).append("-");
        builder.append(date.getDate());
        return builder.toString();
    }
}
