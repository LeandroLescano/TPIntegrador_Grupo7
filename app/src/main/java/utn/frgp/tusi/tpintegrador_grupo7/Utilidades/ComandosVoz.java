package utn.frgp.tusi.tpintegrador_grupo7.Utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.RECORD_AUDIO;

public class ComandosVoz implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private EditText operacion;
    private AyudaAuditiva audio;
    private Button grabando, procesando;
    private ConstraintLayout fondoProcesando;

    public ComandosVoz(Context context, Activity activity, EditText operacion, Button grabando, Button procesando, ConstraintLayout fondoProcesando){
        this.operacion = operacion;
        this.grabando = grabando;
        this.procesando = procesando;
        this. fondoProcesando = fondoProcesando;
        audio = new AyudaAuditiva(context);
        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i("tag", "Ready For Speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        grabando.setVisibility(Button.VISIBLE);
        Log.i("Msg", "Grabando...");
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
        grabando.setVisibility(Button.INVISIBLE);
        fondoProcesando.setVisibility(ConstraintLayout.VISIBLE);
        procesando.setVisibility(Button.VISIBLE);
        Log.i("Msg", "Procesando...");
    }

    @Override
    public void onError(int i) {
        if(!isListening){
            if (i == SpeechRecognizer.ERROR_AUDIO) {
                Log.i("error", "error AUDIO");
            }
            if (i == SpeechRecognizer.ERROR_NO_MATCH) {
                Log.i("error match", "error NO MATCH");
            }
            audio.emitirAudio("Ingreso inentendible");
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        fondoProcesando.setVisibility(ConstraintLayout.INVISIBLE);
        procesando.setVisibility(Button.INVISIBLE);
        if(isListening){
            isListening = false;
        }
        Log.i("Msg", "Terminó");
        ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        boolean encontrado = false, textLetras = true;
        if (matchesFound != null) {
            for(String match : matchesFound){
                if(textLetras && !soloLetras(match)){
                    textLetras = false;
                }
                String opTraducida = match.toLowerCase();
                if(opTraducida.contains("√") || opTraducida.contains("/") || opTraducida.contains("-") || opTraducida.contains("+") || opTraducida.contains("menos")){
                    if(opTraducida.contains("menos")){
                        opTraducida = opTraducida.replace("menos","-");
                    }
                    opTraducida = opTraducida.replace(" ", "").replace("--", "+")
                            .replace("+-", "-")
                            .replace("-+", "-")
                            .replace("++","+");
                    operacion.setText(opTraducida);
                    encontrado = true;
                }
                if(opTraducida.contains("*")){
                    operacion.setText(opTraducida.replace("*","x").replace(" ", ""));
                    encontrado = true;
                }
                if(opTraducida.contains("por")){
                    operacion.setText(opTraducida.replace("por","x").replace(" ", ""));
                    encontrado = true;
                }
                if(encontrado){
                    operacion.setSelection(operacion.length());
                    audio.emitirAudio("El resultado de " + operacion.getText() + " es");
                    break;
                }
            }
            if(textLetras){
                audio.emitirAudio("Ingreso incorrecto");
            }else if(!encontrado){
                operacion.setText(matchesFound.get(0).replace(" ", ""));
                operacion.setSelection(matchesFound.get(0).replace(" ", "").length());
                audio.emitirAudio("El resultado de " + matchesFound.get(0) + " es");
            }
            Log.e("onResults", matchesFound.get(0));
        }
        Log.e("onResults", String.valueOf(bundle));
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.e("onPartialResults", String.valueOf(bundle));
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public void startStop() {
        if (isListening) {
            speechRecognizer.stopListening();
            Log.i("onClick", "stopListening");
            isListening = false;
        } else {
            speechRecognizer.startListening(speechIntent);
            Log.i("onClick", "startListening");
            isListening = true;
        }
    }

    public boolean soloLetras(String text){
        char[] chars = text.trim().toCharArray();

        for (char c : chars) {
            if(c >= 48 && c <= 57) {
                return false;
            }
        }
        return true;
    }
}
