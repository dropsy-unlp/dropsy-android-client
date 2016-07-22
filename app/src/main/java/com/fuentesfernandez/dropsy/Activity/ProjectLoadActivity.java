package com.fuentesfernandez.dropsy.Activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fuentesfernandez.dropsy.Model.Project;
import com.fuentesfernandez.dropsy.ProjectService;
import com.fuentesfernandez.dropsy.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectLoadActivity extends AppCompatActivity {

    private ProjectService projectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_load2);
        ListView listView = (ListView) findViewById(R.id.project_list);
        List<Project> savedProjects = mockProjects();
        listView.setAdapter(new ProjectListAdapter(savedProjects));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ProjectActivity.class);
                i.putExtra("PROJECT",mockProjects().get(position).getXmlName());
                startActivity(i);
            }
        });
    }

    private List<Project> mockProjects(){
        List<Project> projects = new ArrayList<>();
        Project one = new Project("Test",new Date(),"workspace");
        Project two = new Project("Nuevo",new Date(),"workspace");
        projects.add(one);
        projects.add(two);
        return projects;
    }

    public class ProjectListAdapter implements ListAdapter {

        private List<Project> projects;

        public ProjectListAdapter(List<Project> projects){
            this.projects = projects;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return projects.size();
        }

        @Override
        public Object getItem(int position) {
            return projects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View projectListItem = getLayoutInflater().inflate(R.layout.project_list_item,null);
            TextView project_name = (TextView) projectListItem.findViewById(R.id.project_name);
            project_name.setText(projects.get(position).getName());
            TextView project_date = (TextView) projectListItem.findViewById(R.id.project_date);
            project_date.setText(projects.get(position).getSavedDate().toString());
            return projectListItem;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
