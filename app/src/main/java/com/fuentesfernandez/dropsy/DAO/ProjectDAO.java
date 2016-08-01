package com.fuentesfernandez.dropsy.DAO;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.fuentesfernandez.dropsy.Model.Project;

import java.util.List;

public class ProjectDAO {
    private SqlAdapter adapter;

    public ProjectDAO(Context context){
        adapter = Persistence.getAdapter(context);
    }

    public Project getProject(Long id){
        Project project = new Project();
        project.setId(id);
        return adapter.findFirst(project);
    }

    public void saveProject(Project project){
        adapter.store(project);
    }

    public List<Project> getProjects(){
        return adapter.findAll(new Project());
    }
}
