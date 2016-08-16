package com.fuentesfernandez.dropsy.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
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
    private static final String TAG = "Nuevo Proyecto";
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
            CustomDialogClass cdd = new CustomDialogClass(ProjectActivity.this);
            cdd.show();
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
        mCodeGeneratorCallback = new CodeInterpretation(getBaseContext());
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

    public class CustomDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText projectName;

        public CustomDialogClass(Activity a) {
            super(a);
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.save_project_dialog);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            projectName = (EditText) findViewById(R.id.project_name);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    String name = projectName.getText().toString();
                    int blocksCount = mWorkspaceFragment.getWorkspace().getRootBlocks().size();
                    Project project = new Project(name,name + ".xml", blocksCount);
                    saveWorkspaceToAppDir(name + ".xml");
                    projectService.saveProject(project);
                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }



}
