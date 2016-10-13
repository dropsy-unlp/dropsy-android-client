package com.fuentesfernandez.dropsy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.fuentesfernandez.dropsy.R;
import com.fuentesfernandez.dropsy.Service.Robot;
import com.fuentesfernandez.dropsy.Service.RobotImpl;
import com.fuentesfernandez.dropsy.Service.RobotManager;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.squareup.duktape.Duktape;

import java.util.List;

public class CodeInterpretation implements CodeGenerationRequest.CodeGeneratorCallback {
    private Context context;
    private RobotManager robotManager;
    private Duktape duktape;
    private String generatedCode;
    private VideoView vidView;
    private Dialog mVideoDialog;
    private ProgressDialog progDailog;
    private double delaySeconds;
    private long tStart;


    public CodeInterpretation(Context context){
        this.context = context;
        this.robotManager = RobotManager.getInstance();
    }

    @Override
    public void onFinishCodeGeneration(final String newCode) {
        generatedCode = newCode;
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
            CharSequence[] array = new CharSequence[robots.size()];
            for (int i=0; i< robots.size(); i++){
                RobotInfo robotInfo = robots.get(i);
                array[i] = "Robot ID: " + robotInfo.getRobot_id() + "  -  Modelo: " + robotInfo.getRobot_model();
            }

            builderSingle.setSingleChoiceItems(array,-1,new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    RobotInfo robotInfo = robots.get(which);
                    duktape = Duktape.create();
                    RobotImpl robot = new RobotImpl(robotInfo,robotManager,context);
                    duktape.bind("Robot", Robot.class, robot);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    Boolean streamingActive = preferences.getBoolean("streaming_active",false);
                    if (streamingActive) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showStream();
                            }
                        });
                    } else {
                        final AsyncTask task = new codeEvaluationTask();
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                task.execute();
                            }
                        });
                    }
                }
            });

            builderSingle.setNegativeButton(
                    "Cancelar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.create().show();

        }
    }

    private void showStream(){
        mVideoDialog = new Dialog(context);
        mVideoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mVideoDialog.setContentView(R.layout.stream_dialog);
        vidView = (VideoView) mVideoDialog.findViewById(R.id.video_view);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = preferences.getString("stream_url","https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        Uri uri = Uri.parse(url);
        vidView.setVideoURI(uri);
        vidView.setZOrderOnTop(true);
        mVideoDialog.show();
        vidView.start();
        tStart = System.currentTimeMillis();

        progDailog = ProgressDialog.show(context, "Espere por favor ...", "Cargando stream...", true);
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                progDailog.dismiss();
                final AsyncTask task = new codeEvaluationTask();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long tEnd = System.currentTimeMillis();
                        long tDelta = tEnd - tStart;
                        delaySeconds = tDelta / 1000.0;
                        task.execute();
                    }
                });
            }
        });
        vidView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progDailog.dismiss();
                mVideoDialog.hide();
                Toast.makeText(context, "Hubo un problema con el stream de video. Ejecutando codigo...", Toast.LENGTH_LONG).show();
                final AsyncTask task = new codeEvaluationTask();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task.execute();
                    }
                });
                return true;
            }
        });
    }

    private void hideStream(){
        vidView.stopPlayback();
        mVideoDialog.hide();
    }

    private class codeEvaluationTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            duktape.evaluate(generatedCode);
            return null;
        }

//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            try {
//                Thread.sleep((long)delaySeconds);
//                hideStream();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //hideStream();
//        }
    }
}
