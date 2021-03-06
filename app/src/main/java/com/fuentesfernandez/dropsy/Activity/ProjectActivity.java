package com.fuentesfernandez.dropsy.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.fuentesfernandez.dropsy.Model.Project;
import com.fuentesfernandez.dropsy.Service.ProjectService;
import com.fuentesfernandez.dropsy.R;
import com.fuentesfernandez.dropsy.util.CodeInterpretation;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.ui.BlockViewFactory;
import com.google.blockly.android.ui.WorkspaceHelper;
import com.google.blockly.android.ui.vertical.VerticalBlockViewFactory;

import java.util.Arrays;
import java.util.List;

public class ProjectActivity extends AbstractBlocklyActivity {
    private ProjectService projectService;
    private Project currentProject;
    private static final List<String> BLOCK_DEFINITIONS = Arrays.asList(new String[]{
            "default/logic_blocks.json",
            "default/loop_blocks.json",
            "default/math_blocks.json",
            "default/variable_blocks.json",
            "default/colour_blocks.json",
            "default/movement_blocks.json"
    });
    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(new String[]{
            "dropsy/generators.js"
    });

    private CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback;

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "default/toolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    @Override
    public void onSaveWorkspace() {
        if (currentProject == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ingresa el nombre de tu proyecto:");
            // I'm using fragment here so I'm using getView() to provide ViewGroup
            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.save_project_dialog_2, null, false);
            // Set up the input
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated);

            // Set up the buttons
            builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String name = input.getText().toString();
                    int blocksCount = mWorkspaceFragment.getWorkspace().getRootBlocks().size();
                    Project project = new Project(name,name + ".xml", blocksCount);
                    saveWorkspaceToAppDir(name + ".xml");
                    projectService.saveProject(project);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            saveWorkspaceToAppDir(currentProject.getXmlName());
            int blocksCount = mWorkspaceFragment.getWorkspace().getRootBlocks().size();
            currentProject.setBlocksCount(blocksCount);
            projectService.saveProject(currentProject);
        }
    }

    @Override
    public void onLoadWorkspace() {
        Intent i = new Intent(getApplicationContext(), ProjectLoadActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        projectService = new ProjectService(getBaseContext());
        mCodeGeneratorCallback = new CodeInterpretation(ProjectActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public BlockViewFactory onCreateBlockViewFactory(WorkspaceHelper helper) {
        return new VerticalBlockViewFactory(this, helper);
    }

    @Override
    protected void onLoadInitialWorkspace() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getLong("PROJECT") != 0){
            Long projectId = getIntent().getExtras().getLong("PROJECT");
            currentProject = projectService.getProject(projectId);
        }
        super.onLoadInitialWorkspace();
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    protected void onRunCode() {
        super.onRunCode();
    }

    @Override
    protected void onInitBlankWorkspace() {
        getController().addVariable("item");
        if (currentProject != null){
            loadWorkspaceFromAppDir(currentProject.getXmlName());
        }
    }

}
