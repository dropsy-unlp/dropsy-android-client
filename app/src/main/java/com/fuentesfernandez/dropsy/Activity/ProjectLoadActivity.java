package com.fuentesfernandez.dropsy.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fuentesfernandez.dropsy.Model.Project;
import com.fuentesfernandez.dropsy.Service.ProjectService;
import com.fuentesfernandez.dropsy.R;

import java.util.List;

public class ProjectLoadActivity extends AppCompatActivity {

    private ProjectService projectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectService = new ProjectService(getBaseContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_project_load2);
        ListView listView = (ListView) findViewById(R.id.project_list);
        ProjectListAdapter adapter = new ProjectListAdapter(this,0);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ProjectActivity.class);
                List<Project> savedProjects = projectService.getAllProjects();
                i.putExtra("PROJECT",savedProjects.get(position).getId());
                startActivity(i);
                finish();
            }
        });

    }

    public class ProjectListAdapter extends ArrayAdapter {

        private List<Project> projects;

        ProjectListAdapter(Context context, int resource) {
            super(context, resource);
            projects = projectService.getAllProjects();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View projectListItem = getLayoutInflater().inflate(R.layout.project_list_item,null);
            TextView project_name = (TextView) projectListItem.findViewById(R.id.project_name);
            ImageButton delete_button = (ImageButton) projectListItem.findViewById(R.id.delete_button);
            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProjectLoadActivity.this);
                    builder.setTitle("Esta seguro que desea eliminar el proyecto?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Project project = projects.get(position);
                            ProjectLoadActivity.this.deleteFile(project.getXmlName());
                            projectService.deleteProject(project);
                            projects = projectService.getAllProjects();
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            project_name.setText(projects.get(position).getName());
            TextView project_date = (TextView) projectListItem.findViewById(R.id.id);
            TextView block_count = (TextView) projectListItem.findViewById(R.id.blocks_count);
            block_count.setText(Integer.toString(projects.get(position).getBlocksCount()));
            if (projects.get(position).getSavedDate() != null)  project_date.setText(projects.get(position).getSavedDate());
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
