package com.fuentesfernandez.dropsy.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.fuentesfernandez.dropsy.Model.Project;
import com.fuentesfernandez.dropsy.ProjectService;
import com.fuentesfernandez.dropsy.R;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.LoggingCodeGeneratorCallback;
import com.google.blockly.android.ui.BlockViewFactory;
import com.google.blockly.android.ui.WorkspaceHelper;
import com.google.blockly.android.ui.vertical.VerticalBlockViewFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProjectActivity extends AbstractBlocklyActivity {
    private static final String TAG = "Nuevo Proyecto";
    private ProjectService projectService;
    private String currentProject = "";
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

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new LoggingCodeGeneratorCallback(this, TAG);

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
        CustomDialogClass cdd=new CustomDialogClass(ProjectActivity.this, currentProject);
        cdd.show();
    }

    @Override
    public void onLoadWorkspace() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("LOAD_PROJECT",true);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public BlockViewFactory onCreateBlockViewFactory(WorkspaceHelper helper) {
        return new VerticalBlockViewFactory(this, helper);
    }

    @Override
    protected void onLoadInitialWorkspace() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("PROJECT") != null){
            currentProject = getIntent().getExtras().getString("PROJECT");
        } else {
            currentProject = "";
        }
        super.onLoadInitialWorkspace();
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        // Uses the same callback for every generation call.
        return mCodeGeneratorCallback;
    }

    @Override
    protected void onInitBlankWorkspace() {
        getController().addVariable("item");
        if (!Objects.equals(currentProject, "")){
            loadWorkspaceFromAppDir(currentProject + ".xml");
        }
    }

    public class CustomDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        private String name;
        public Dialog d;
        public Button yes, no;
        public EditText projectName;

        public CustomDialogClass(Activity a, String name) {
            super(a);
            this.c = a;
            this.name = name;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.save_project_dialog);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            projectName = (EditText) findViewById(R.id.project_name);
            projectName.setText(name);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    String name = projectName.getText().toString();
                    Date date = new Date();
                    Project project = new Project(name,new Date(),name + ".xml");
                    saveWorkspaceToAppDir(name + ".xml");
                    //projectService.saveProject(project);
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
