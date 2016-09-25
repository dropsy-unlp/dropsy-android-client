package com.fuentesfernandez.dropsy.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.fuentesfernandez.dropsy.Service.Robot;
import com.fuentesfernandez.dropsy.Service.RobotImpl;
import com.fuentesfernandez.dropsy.Service.RobotManager;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.squareup.duktape.Duktape;

import java.util.List;

public class CodeInterpretation implements CodeGenerationRequest.CodeGeneratorCallback {
    private Context context;
    private RobotManager robotManager;

    public CodeInterpretation(Context context){
        this.context = context;
        this.robotManager = RobotManager.getInstance();
    }

    @Override
    public void onFinishCodeGeneration(final String generatedCode) {
        final List<RobotInfo> robots = robotManager.getRobots();
        if(generatedCode.isEmpty()) {
            Toast.makeText(this.context, "Hubo un problema con la generacion de codigo.", Toast.LENGTH_LONG).show();
        } else if (!robotManager.isConnected()){
            Toast.makeText(this.context, "Hubo un problema al conectarse al servidor.", Toast.LENGTH_LONG).show();
        } else if (robots.size()==0){
            Toast.makeText(this.context, "No hay robots disponibles.", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setTitle("Selecciona un robot: ");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.select_dialog_singlechoice);
            for (RobotInfo robotInfo : robots){
                arrayAdapter.add("ID: " + robotInfo.getRobot_id() + "  -  Model: " + robotInfo.getRobot_model());
            }
            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RobotInfo robotInfo = robots.get(which);
                            Toast.makeText(context, generatedCode, Toast.LENGTH_LONG).show();
                            Duktape duktape = Duktape.create();
                            RobotImpl robot = new RobotImpl(robotInfo,robotManager);
                            duktape.bind("Robot", Robot.class, robot);
                            dialog.dismiss();
                            duktape.evaluate(generatedCode);
                        }
                    });
            builderSingle.show();

        }
    }
}
