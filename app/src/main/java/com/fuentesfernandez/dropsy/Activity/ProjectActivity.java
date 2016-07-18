package com.fuentesfernandez.dropsy.Activity;

import android.support.annotation.NonNull;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.LoggingCodeGeneratorCallback;
import com.google.blockly.android.ui.BlockViewFactory;
import com.google.blockly.android.ui.WorkspaceHelper;
import com.google.blockly.android.ui.vertical.VerticalBlockViewFactory;

import java.util.Arrays;
import java.util.List;

public class ProjectActivity extends AbstractBlocklyActivity {
    private static final String TAG = "ProjectActivity";

    private static final List<String> BLOCK_DEFINITIONS = Arrays.asList(new String[]{
            "default/loop_blocks.json",
            "default/math_blocks.json",
            "default/variable_blocks.json",
            "default/colour_blocks.json"
    });
    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(new String[]{
            // Custom block generators go here. Default blocks are already included.
            // TODO(#99): Include Javascript defaults when other languages are supported.
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
    public BlockViewFactory onCreateBlockViewFactory(WorkspaceHelper helper) {
        return new VerticalBlockViewFactory(this, helper);
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        // Uses the same callback for every generation call.
        return mCodeGeneratorCallback;
    }

    @Override
    protected void onInitBlankWorkspace() {
        // Initialize variable names.
        // TODO: (#22) Remove this override when variables are supported properly
        getController().addVariable("item");
    }

}
