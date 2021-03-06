package utn.frgp.tusi.tpintegrador_grupo7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import utn.frgp.tusi.tpintegrador_grupo7.AccesoDatos.ConfiguracionDao;
import utn.frgp.tusi.tpintegrador_grupo7.AccesoDatos.HistorialDao;
import utn.frgp.tusi.tpintegrador_grupo7.Dominio.Configuracion;
import utn.frgp.tusi.tpintegrador_grupo7.Dominio.Formato;
import utn.frgp.tusi.tpintegrador_grupo7.Dominio.Operacion;
import utn.frgp.tusi.tpintegrador_grupo7.Interface.ChangeCalculator;
import utn.frgp.tusi.tpintegrador_grupo7.Utilidades.AyudaAuditiva;
import utn.frgp.tusi.tpintegrador_grupo7.Utilidades.ComandosVoz;
import utn.frgp.tusi.tpintegrador_grupo7.Utilidades.Vibracion;

public class CalculadoraCientifica extends AppCompatActivity implements ChangeCalculator {
    private TextView resultado;
    private EditText operacion;
    private String Signo = "";
    private String Numero= "";
    private Integer posActual;
    private String[] funciones;
    private AyudaAuditiva audio;
    private Button alertaGrabando, alertaProcesando;
    private ComandosVoz voz;
    private ConstraintLayout fondoProcesando;
    private LinearLayout layout;
    private String MuestraVieja = "";
    private String buttonText = "";
    private String NumeroViejo = "";
    private ConfiguracionDao config;
    private Configuracion cfgActual;
    private ArrayList<View> botones, botonesTam, botonesImg;
    private Vibracion vibra;
    private Vibrator vibrator;
    private boolean vibrarBoton = true;
    private HistorialDao hist;
    private DecimalFormat formatter;
    private int decimales;
    private int format;

    @Override
    public void onResume() {
        super.onResume();
        cargarConfig();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cientifica);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        funciones= new String[]{"arctan(", "arcsin(", "arccos(", "tan(", "sin(", "cos(", "lg(", "ln("};
        alertaGrabando = findViewById(R.id.alerta_grabando);
        alertaProcesando = findViewById(R.id.alerta_procesando);
        layout = findViewById(R.id.layout_Cientifica);
        fondoProcesando = findViewById(R.id.background_procesando);
        operacion = findViewById(R.id.txtOperacion);
        resultado = findViewById(R.id.txtResultado);
        resultado.setAlpha((float) 0.5);
        resultado.setText("0");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibra = new Vibracion();
        vibra = vibra.getManager(this);
        hist = new HistorialDao();
        formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            operacion.setShowSoftInputOnFocus(false);
        }
        operacion.requestFocus();

        operacion.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                posActual = operacion.getSelectionEnd();
            }
        });

        String opHistorial = getIntent().getStringExtra("operacion");
        if(opHistorial != null && !opHistorial.isEmpty()){
            operacion.setText(opHistorial.substring(0,opHistorial.indexOf("=")));
            resultado.setText(opHistorial.substring(opHistorial.indexOf("=")+1));
            operacion.setSelection(operacion.length());
        }else{
            
        }
        //Consultar configuración para cambiar aspecto.
        //formatearAspecto();

        //Cargar configuración
        cargarConfig();

        audio = new AyudaAuditiva(this);
    }


    //Coloca el dígito seleccionado en pantalla.
    public void ingresarDigito(View view){
        Button digit = (Button)view;
        buttonText = digit.getText().toString();

        if (resultado.getAlpha() == 1f) {
            if(resultado.getText().equals("Error matemático") || resultado.getText().equals("Error de sintaxis")){
                operacion.setText("");
                resultado.setText("0");
            }
            resultado.setAlpha(0.5f);
        }
        posActual = operacion.getSelectionEnd();
        MuestraVieja = operacion.getText().toString();
        NumeroViejo = Numero;
        if(!ObtenerOperador()) {
            if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
                audio.emitirAudio(buttonText);
            }
            switch (view.getId()) {
                case R.id.btn0:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("0".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "0";
                    break;
                case R.id.btn1:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("1".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "1";
                    break;
                case R.id.btn2:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("2".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "2";
                    break;
                case R.id.btn3:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("3".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "3";
                    break;
                case R.id.btn4:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("4".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "4";
                    break;
                case R.id.btn5:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("5".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "5";
                    break;
                case R.id.btn6:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("6".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "6";
                    break;
                case R.id.btn7:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("7".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "7";
                    break;
                case R.id.btn8:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("8".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "8";
                    break;
                case R.id.btn9:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("9".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "9";
                    break;
                case R.id.btnPunto:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat(".".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + ".";
                    break;
                case R.id.btnSumar:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("+".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "+";
                    break;
                case R.id.btnResta:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("-".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "-";
                    break;
                case R.id.btnMultiplicar:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("x".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "x";
                    break;
                case R.id.btnDividir:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("/".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "/";
                    break;
                case R.id.btnCerrarParent:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat(")".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + ")";
                    break;
                case R.id.btnAbrirParent:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("(".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "(";
                    break;
                case R.id.btntan:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("tan()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "tan()";
                    posActual = posActual + 3;
                    break;
                case R.id.btnsin:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("sin()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "sin()";
                    posActual = posActual + 3;
                    break;
                case R.id.btncos:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("cos()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "cos()";
                    posActual = posActual + 3;
                    break;
                case R.id.btnTanInverso:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("arctan()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "arctan()";
                    posActual = posActual + 6;
                    break;
                case R.id.btnSinInverso:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("arcsin()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "arcsin()";
                    posActual = posActual + 6;
                    break;
                case R.id.btnCosInverso:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("arccos()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "arccos()";
                    posActual = posActual + 6;
                    break;
                case R.id.btnLogDecimal:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("lg()".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "lg()";
                    posActual = posActual + 2;
                    break;
                case R.id.btnLogNatural:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("ln()".concat(MuestraVieja.substring(posActual))));
                    posActual = posActual + 2;
                    Numero = NumeroViejo + "ln()";
                    break;
                case R.id.btnPi:
                    if(posActual > 0){
                        String[] A = {"+","-","/","*","√","^","("};
                        String[] Numeros = {"0","1","2","3","4","5","6","7","8","9"};
                        String valor;
                        boolean Entro= false;
                        boolean Anterior = false;
                        String XD = MuestraVieja.substring(posActual - 1, posActual);
                        for(String a:A)
                        {
                            if (MuestraVieja.substring(posActual - 1, posActual).equals(a)) {
                                valor = MuestraVieja.substring(0, posActual).concat(String.valueOf(Math.PI).substring(0, 10).concat(MuestraVieja.substring(posActual)));
                                if(MuestraVieja.substring(posActual - 1, posActual).equals("("))
                                {
                                    posActual = posActual + 1;
                                }
                                operacion.setText(valor);
                                posActual = posActual + 9;
                                Entro = true;
                                break;
                            }
                        }
                        if(Entro == false)
                        {
                            for(String n:Numeros){
                                if (MuestraVieja.substring(posActual - 1, posActual).equals(n)) {
                                    valor = MuestraVieja.substring(0, posActual).concat("x" + String.valueOf(Math.PI).substring(0, 10).concat(MuestraVieja.substring(posActual)));
                                    operacion.setText(valor);
                                    posActual = posActual + 10;
                                    Anterior = true;
                                }
                                if(Anterior == true){
                                    break;
                                }
                            }
                            if(!Anterior){
                            valor = MuestraVieja.substring(0, posActual).concat("(" + String.valueOf(Math.PI).substring(0, 10).concat(MuestraVieja.substring(posActual) + ")"));
                            operacion.setText(valor);
                            posActual = posActual + 1;
                            posActual = posActual + 10;
                            }
                        }
                    }
                    else{
                        operacion.setText(MuestraVieja.substring(0, posActual).concat(String.valueOf(Math.PI).substring(0, 10).concat(MuestraVieja.substring(posActual))));
                        posActual = posActual + 9;
                    }
                    Numero = NumeroViejo + String.valueOf(Math.PI);
                    operacion.setSelection(posActual);
                    break;
                case R.id.btnRaiz:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("√".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "√";
                    break;
                case R.id.btnPotencia:
                    operacion.setText(MuestraVieja.substring(0, posActual).concat("^".concat(MuestraVieja.substring(posActual))));
                    Numero = NumeroViejo + "^";
                    break;
                case R.id.btnPorcentaje:
                    porcentajeNumero(MuestraVieja);
                    break;
            }
            if (posActual < operacion.getText().length() && view.getId() != R.id.btnPorcentaje) {
                posActual++;
                operacion.setSelection(posActual);
            }

            calcular();
            if(vibrarBoton){
                vibra.VibracionBoton();
            }
        }else{
            if(posActual < operacion.getText().length() && view.getId() != R.id.btnPorcentaje){
                posActual++;
                operacion.setSelection(posActual);
            }
        }
    }

    public boolean ObtenerOperador(){
        String[] Operadores = { "+", "-", "x", "/", "="};
        boolean Error = false;
//        if (Arrays.asList(Operadores).contains(buttonText)) {
//            if(MuestraVieja.isEmpty()){
//                if(buttonText.equals("-")) {
//                    Error = false;
//                }
//                else{
//                    Error = true;
//                }
//            }
//            else {
//                String UltimoCaracter;
//                if(operacion.getSelectionEnd() <= operacion.length()-1){
//                    UltimoCaracter = MuestraVieja.substring(operacion.getSelectionEnd()-1,operacion.getSelectionEnd());
//                }else{
//                    UltimoCaracter= MuestraVieja.substring(MuestraVieja.length()-1);
//                }
//                if(UltimoCaracter.equals("x") || UltimoCaracter.equals("/")){
//                    if(!buttonText.equals("-") && !buttonText.equals("+")){
//                        Error = true;
//                    }
//                }else if(UltimoCaracter.equals(buttonText)){
//                    Error = true;
//                }
//            }
//        }
//        else{
//            Error = false;
//        }
        return  Error;
    }

    //Coloca en pantalla el último número ingresado dividido 100
    public void porcentajeNumero(String muestra){
        if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
            audio.emitirAudio("%");
        }
        String opActual = operacion.getText().toString().substring(0, posActual);
        Integer ultimaPos=0, numeroInt = 0, posNumero;
        Float charCode, numeroFloat;
        for (int x=0; x<opActual.length(); x++){
            charCode = (float) opActual.charAt(x);
            if(charCode != 46 && (charCode < 48 || charCode > 57)){
                    ultimaPos=x+1;
            }
        }
        try{
            numeroFloat = Float.parseFloat(opActual.substring(ultimaPos))/100;
            posNumero = muestra.lastIndexOf(opActual.substring(ultimaPos));
            String finalOperacion = muestra.substring(posNumero + opActual.substring(ultimaPos).length());
            if(numeroFloat%1 == 0){
                numeroInt = Math.round(numeroFloat);
                operacion.setText(muestra.substring(0, posNumero).concat(numeroInt.toString()).concat(finalOperacion));
                posActual = operacion.getText().toString().lastIndexOf(numeroInt.toString())+numeroFloat.toString().length()-2;
            }else{
                if(numeroFloat.toString().contains(("E"))){
                    NumberFormat formatter = new DecimalFormat("###########.##########");
                    String numFloatExp = formatter.format(numeroFloat);
                    operacion.setText(muestra.substring(0, posNumero).concat(numFloatExp).concat(finalOperacion));
                    posActual = operacion.getText().toString().lastIndexOf(numFloatExp)+numFloatExp.length();
                }else{
                    operacion.setText(muestra.substring(0, posNumero).concat(numeroFloat.toString()).concat(finalOperacion));
                    posActual = operacion.getText().toString().lastIndexOf(numeroFloat.toString())+numeroFloat.toString().length();
                }
            }
            operacion.setSelection(posActual);
        }catch (NumberFormatException e){

        }
        calcular();
        if(vibrarBoton){
            vibra.VibracionBoton();
        }
    }

    //Mueve el cursor hacia la izquierda o derecha.
    public void moverCursor(View view){
        AppCompatImageButton flecha = (AppCompatImageButton) view;
        posActual = operacion.getSelectionEnd();
        switch(flecha.getId()){
            case R.id.btnDerecha:
                if(posActual < operacion.getText().length()){
                    posActual++;
                    operacion.setSelection(posActual);
                }
                if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
                    audio.emitirAudio("derecha");
                }
                break;
            case R.id.btnIzquierda:
                if(posActual-1 >= 0){
                    posActual--;
                    operacion.setSelection(posActual);
                }
                if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
                    audio.emitirAudio("izquierda");
                }
                break;
        }
        if(vibrarBoton){
            vibra.VibracionBoton();
        }
    }

    //Borra un dígito a la izquierda de la posición actual del cursor.
    public void borrarDigito(View view){
        if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
            audio.emitirAudio("borrar");
        }
        String opActual = operacion.getText().toString();
        if(opActual.length() > 0 && posActual > 0){
            int posFuncion = contieneOperadores(opActual);
            if(posFuncion >= 0 && (posActual >= posFuncion && posActual <= (opActual.indexOf("(")+1))){
                if(opActual.charAt(posActual) == ')'){
                    operacion.setText(opActual.substring(0,posFuncion).concat(opActual.substring(posActual+1)));
                }else{
                operacion.setText(opActual.substring(0,posFuncion).concat(opActual.substring(opActual.indexOf("(")+1)));
                }
                posActual = posFuncion;
            }else{
                operacion.setText(opActual.substring(0, posActual-1).concat(opActual.substring(posActual)));
                posActual--;
            }
            operacion.setSelection(posActual);
        }
        if(operacion.getText().length() <= 0){
            resultado.setText("0");
        }
        calcular();
        if(vibrarBoton){
            vibra.VibracionBoton();
        }
    }

    //Chequea si la operacion contiene funciones de tipo trigonométricas y logaritmicas.
    public Integer contieneOperadores(String op){
        for (String funcion : funciones) {
            if (op.contains(funcion)) {
                return op.indexOf(funcion);
            }
        }
        return -1;
    }

    //Borra toda la operación en pantalla y resetea la calculadora en 0.
    public void eliminarOperacion(View view){
        if(cfgActual.getSonido().getDescripcion().equals("Siempre")){
            audio.emitirAudio("borrar todo");
        }
        operacion.setText("");
        resultado.setText("0");
        resultado.setAlpha((float) 0.5);
        if(vibrarBoton){
            vibra.VibracionBoton();
        }
    }

    //Realiza el cálculo de la operación.
    @SuppressLint("SetTextI18n")
    public void calcularOperacion(View view){
        calcular();
        if(vibra == null){
            vibra = vibra.getManager(this);
        }

        if (!resultado.getText().equals("Error matemático") && !resultado.getText().equals("Error de sintaxis"))
        {
            hist.cargarOperacion(operacion.getText() + "=" + resultado.getText(), this);

            if (cfgActual.getVibracion().getDescripcion().equals("Siempre") || cfgActual.getVibracion().getDescripcion().equals("Solo resultados"))
            {
                vibra.VibracionResultado();
            }

            if (cfgActual.getSonido().getDescripcion().equals("Siempre") || cfgActual.getSonido().getDescripcion().equals("Solo resultados"))
            {
                audio.emitirAudio("Resultado: " + resultado.getText() );
            }
        }
        else
        {
            if (cfgActual.getVibracion().getDescripcion().equals("Siempre") || cfgActual.getVibracion().getDescripcion().equals("Solo errores"))
            {
                vibra.VibracionError();
            }

            if (cfgActual.getSonido().getDescripcion().equals("Siempre") || cfgActual.getSonido().getDescripcion().equals("Solo errores"))
            {
                audio.emitirAudio(resultado.getText().toString());
            }
        }
        resultado.setAlpha(1f);

    }

    @Override
    public void cambiarPantalla(String pantalla) {
        Intent intent;
        switch (pantalla){
            case "calculadoraBasica":
                intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.CalculadoraBasica.class);
                startActivity(intent);
                break;
            case "configuracion":
                intent = new Intent(this, ConfiguracionActivity.class);
                startActivity(intent);
                break;
            case "historial":
                intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.HistorialOperaciones.class);
                startActivity(intent);
                break;
        }
    }


    public class DecimalDigitsInputFilter  implements InputFilter {
        private Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    public void calcular(){
        String operacionACalcular = operacion.getText().toString();

        //Agregar multiplicación entre parentesis--
        operacionACalcular = Operacion.agregarMultiplicaciones(operacionACalcular);

        //Resolución de funciones
        operacionACalcular = Operacion.calcularOperacionCientifica(cfgActual.getDecimales().getCantidad() , operacionACalcular , format);

        //Detección de posición de paréntisis
        operacionACalcular = Operacion.sacarParentesis(operacionACalcular);



        //Calculo de operación.
        Float resultadoOp = Operacion.calcularOperacionBasica(operacionACalcular,operacion.getText().toString());
        if(resultadoOp != null && resultadoOp%1 == 0){
            if(resultadoOp.toString().contains("E")){
                resultado.setText(formatter.format(new BigDecimal(resultadoOp)));
                resultado.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
            }else{
                resultado.setText(formatter.format(Math.round(resultadoOp)));
                resultado.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
            }
        }else if(resultadoOp != null && resultadoOp.isNaN()){
            resultado.setText("Error matemático");
        }else if(resultadoOp != null && resultadoOp.isInfinite()){
            resultado.setText("Infinito");
        }else if (resultadoOp != null){
            resultado.setText(formatter.format(resultadoOp));
        }else{
            resultado.setText("Error de sintaxis");
        }

    }


    public void comandoDeVoz(View view){
        ImageView micButton = (ImageView) view;
        if(voz == null){
            voz = new ComandosVoz(this, this, operacion, resultado, alertaGrabando, alertaProcesando, fondoProcesando, layout , decimales , format , CalculadoraCientifica.this);
        }
            voz.startStop();
    }

    public void cambiarCalculadoraCientifica(boolean cambiar){
        boolean cambiarCalculadora = cambiar;
        if(cambiarCalculadora  == true ){
            Intent intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.CalculadoraBasica.class);
            SharedPreferences preferences = this.getSharedPreferences("calculadora", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ultima", "basica");
            editor.apply();
            startActivity(intent);
        }
    }

    //Configura la calculadora visualmente.
    public void formatearAspecto(){

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item= menu.findItem(R.id.action_cientifica);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_configuracion) {
            Intent intent = new Intent(this, ConfiguracionActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_basica){
            Intent intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.CalculadoraBasica.class);
            SharedPreferences preferences = this.getSharedPreferences("calculadora", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ultima", "basica");
            editor.apply();
            startActivity(intent);
            return true;
        } else if(id == R.id.action_cientifica){
            Intent intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.CalculadoraCientifica.class);
            startActivity(intent);
        } else if(id == R.id.action_historial){
            Intent intent = new Intent(this, utn.frgp.tusi.tpintegrador_grupo7.HistorialOperaciones.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Agregar botones a lista modificables en tamaño

    public ArrayList<View> agregarBotones ()
    {
        ArrayList<View> listaCompleta = new ArrayList<View>();
        listaCompleta.add(findViewById(R.id.btn0));
        listaCompleta.add(findViewById(R.id.btn1));
        listaCompleta.add(findViewById(R.id.btn2));
        listaCompleta.add(findViewById(R.id.btn3));
        listaCompleta.add(findViewById(R.id.btn4));
        listaCompleta.add(findViewById(R.id.btn5));
        listaCompleta.add(findViewById(R.id.btn6));
        listaCompleta.add(findViewById(R.id.btn7));
        listaCompleta.add(findViewById(R.id.btn8));
        listaCompleta.add(findViewById(R.id.btn9));
        listaCompleta.add(findViewById(R.id.btnPunto));
        listaCompleta.add(findViewById(R.id.btnIgual));
        listaCompleta.add(findViewById(R.id.btnSumar));
        listaCompleta.add(findViewById(R.id.btnResta));
        listaCompleta.add(findViewById(R.id.btnLogNatural));
        listaCompleta.add(findViewById(R.id.btnLogDecimal));
        listaCompleta.add(findViewById(R.id.btnPotencia));
        listaCompleta.add(findViewById(R.id.btnEliminar));
        listaCompleta.add(findViewById(R.id.btnBorrar));
        listaCompleta.add(findViewById(R.id.btnDividir));
        listaCompleta.add(findViewById(R.id.btnMultiplicar));
        listaCompleta.add(findViewById(R.id.btnRaiz));
        listaCompleta.add(findViewById(R.id.btnPi));
        listaCompleta.add(findViewById(R.id.btnPorcentaje));

        return listaCompleta;
    }

    //Agregar image buttons a lista

    public ArrayList<View> agregarBotonesImg ()
    {
        ArrayList<View> listaCompleta = new ArrayList<View>();
        listaCompleta.add(findViewById(R.id.btnIzquierda));
        listaCompleta.add(findViewById(R.id.btnDerecha));
        listaCompleta.add(findViewById(R.id.btnMic));

        return listaCompleta;
    }

    //Carga configuracion actual en las listas de todos los botones

    public void cargarConfig() {
        config = new ConfiguracionDao();
        cfgActual = new Configuracion();
        cfgActual = config.traerConfiguracion(this);
        decimales = cfgActual.getDecimales().getCantidad();
        format = cfgActual.getFormato().getId();
        formatter.setMaximumFractionDigits(cfgActual.getDecimales().getCantidad());
        botonesImg = agregarBotonesImg();
//        botonesTam = agregarBotones();
        botones = layout.getTouchables();

        for (View v : botones) {
            if (v.getId() != R.id.txtOperacion && v.getId() != R.id.btnDerecha && v.getId() != R.id.btnIzquierda && v.getId() != R.id.btnMic) {
                Button boton = (Button) v;
                boton.setBackgroundColor(config.setearColorBoton(this));
                boton.setTextColor(config.setearColorTexto(this));
                boton.setTypeface(config.setearTipografia(this));
            }
        }

        for (View i : botonesImg) {
            ImageButton boton = (ImageButton) i;
            boton.setColorFilter(config.setearColorTexto(this));
            boton.setBackgroundColor(config.setearColorBoton(this));
        }

        for (View b : botones) {
                if(b.getId() == R.id.btnDerecha || b.getId() == R.id.btnIzquierda || b.getId() == R.id.btnMic){
                    ImageButton img = (ImageButton) b;
                    int tamano = config.setearTamano(this)-2;
                    if(b.getId() == R.id.btnDerecha){
                        switch (tamano){
                            case 8:
                                img.setImageResource(R.drawable.ic_flecha_der_8);
                                break;
                            case 12:
                                img.setImageResource(R.drawable.ic_flecha_der_12);
                                break;
                            case 16:
                                img.setImageResource(R.drawable.ic_flecha_der_16);
                                break;
                            case 24:
                                img.setImageResource(R.drawable.ic_flecha_der_24);
                                break;
                            default:
                                break;
                        }
                    }else if(b.getId() == R.id.btnIzquierda){
                        switch (tamano){
                            case 8:
                                img.setImageResource(R.drawable.ic_flecha_izq_8);
                                break;
                            case 12:
                                img.setImageResource(R.drawable.ic_flecha_izq_12);
                                break;
                            case 16:
                                img.setImageResource(R.drawable.ic_flecha_izq_16);
                                break;
                            case 24:
                                img.setImageResource(R.drawable.ic_flecha_izq_24);
                                break;
                            default:
                                break;
                        }
                    }else{
                        switch (tamano){
                            case 8:
                                img.setImageResource(R.drawable.ic_mic_8);
                                break;
                            case 12:
                                img.setImageResource(R.drawable.ic_mic_12);
                                break;
                            case 16:
                                img.setImageResource(R.drawable.ic_mic_16);
                                break;
                            case 24:
                                img.setImageResource(R.drawable.ic_mic_24);
                                break;
                            default:
                                break;
                        }
                    }
                }else if(b.getId() != R.id.txtOperacion){
                    Button boton = (Button) b;
                   int tamano = config.setearTamano(this);
                   if(tamano > 18){
                       boton.setTextSize(tamano - 6);
                   }
                   else{
                       boton.setTextSize(tamano);
                   }
                    /*if(b.getId() == R.id.btnEliminar || b.getId() == R.id.btnTanInverso || b.getId() == R.id.btnCosInverso || b.getId() == R.id.btnSinInverso){
                    }*/
            }
        }
        if(cfgActual.getVibracion().getDescripcion().equals("Siempre")){
            vibrarBoton = true;
        }else{
            vibrarBoton = false;
        }
    }

}
