package com.fuentesfernandez.dropsy.util;

import android.content.Context;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Service.RobotService;
import com.fuentesfernandez.dropsy.Service.RobotServiceImpl;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.squareup.duktape.Duktape;

public class CodeInterpretation implements CodeGenerationRequest.CodeGeneratorCallback {
    private Context context;
    private RobotService robotService;

    public CodeInterpretation(Context context){
        this.context = context;
        this.robotService = new RobotServiceImpl();
    }

    @Override
    public void onFinishCodeGeneration(String generatedCode) {
        if(generatedCode.isEmpty()) {
            Toast.makeText(this.context, "Hubo un problema con la generacion de codigo.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.context, generatedCode, Toast.LENGTH_LONG).show();
            robotService.setGeneratedCode(generatedCode);
            robotService.connect();
        }
    }
}
