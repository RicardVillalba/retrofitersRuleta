package com.example.retrofitersruleta;

import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_MONEDERO;
import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_NOMBRE;
import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_TURNOS;


import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.Manifest;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener, LocationListener {

    private String nombre;
    private int monedero = 100;
    private int turnos = 0;
    private int winningNumber = 0;
    private int winnings = 0;

    private Button buttonStart;
    private float lngDegrees = 0;

    private ImageView panoRuleta;
    private EditText editTextBetAmount;
    private boolean panoRuletaRotation = true;

    private boolean isRojoSelected = false;
    private boolean isNegroSelected = false;
    private boolean is1stRangeSelected = false;

    private boolean is2ndRangeSelected = false;
    private boolean is3rdRangeSelected = false;


    private TextView monederoLabel;
    private TextView turnosLabel;

    private TextView numeroGanadorTextView;

    private PartidaDatabase partidaDatabase;

    private ReproductorMusica reproductorMusica;

    private final Random random = new Random();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reproductorMusica = ReproductorMusica.getInstance(this);
        // Obtenemos el servicio de ubicación del sistema
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Solicitamos actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        partidaDatabase = new PartidaDatabase(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("nombre_jugador")) {
            nombre = intent.getStringExtra("nombre_jugador");
        }

            // Inicializar las vistas
            buttonStart = findViewById(R.id.buttonstart);
            panoRuleta = findViewById(R.id.panoruleta);
            editTextBetAmount = findViewById(R.id.editTextBetAmount);
            monederoLabel = findViewById(R.id.monederoLabel);
            turnosLabel = findViewById(R.id.turnosLabel);
            numeroGanadorTextView = findViewById(R.id.numeroGanadorTextView);

                        // Configurar el botón de inicio
            buttonStart.setOnClickListener(v -> onClickButtonRotation(v));

            // Configurar los botones de apuesta
            configureBetButtons();
        reproductorMusica.reproducirMelodiaFondo();

    }

    // Método llamado cada vez que la ubicación cambia
    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        // Aquí  guardamos la ubicación en la base de datos SQLite
        guardarUbicacionEnSQLite(latitude, longitude);
    }
    @Override
    protected void onResume() {
        super.onResume();
        reproductorMusica.reproducirMelodiaFondo();
    }


        private void configureBetButtons () {
            // Configurar los botones de apuesta
            configureBetButton(R.id.btnRojo);
            configureBetButton(R.id.btnNegro);
            configureBetButton(R.id.btn0_11);
            configureBetButton(R.id.btn12_24);
            configureBetButton(R.id.btn25_36);
        }

        private void configureBetButton ( int buttonId){
            Button betButton = findViewById(buttonId);
            betButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleBetButtonClick(betButton);
                }
            });
        }

        private void handleBetButtonClick (Button betButton){
            if (betButton.getId() == R.id.btnRojo) {
                isRojoSelected = !isRojoSelected;
            } else if (betButton.getId() == R.id.btnNegro) {
                isNegroSelected = !isNegroSelected;
            } else if (betButton.getId() == R.id.btn0_11) {
                is1stRangeSelected = !is1stRangeSelected;
            } else if (betButton.getId() == R.id.btn12_24) {
                is2ndRangeSelected = !is2ndRangeSelected;
            } else if (betButton.getId() == R.id.btn25_36) {
                is3rdRangeSelected = !is3rdRangeSelected;
            }
        }

        public void onClickButtonRotation (View v){
            if (panoRuletaRotation) {
                // Obtener el monto de la apuesta ingresado por el usuario
                int betAmount = getBetAmount();

                // Verificar si el monto de la apuesta es válido
                if (isValidBetAmount(betAmount)) {
                    // Lógica de apuesta
                    if (isRojoSelected || isNegroSelected) {
                        // Si es una apuesta de color
                        checkColorBetResult();
                    } else {
                        // Si es una apuesta a un rango de números
                        checkNumberRangeBetResult();
                    }
                    // Incrementar el contador de turnos después de cada rotación exitosa
                    turnos++;
                } else {
                    // Mostrar mensaje si el monto de la apuesta no es válido
                    Toast.makeText(this, "No tienes suficiente dinero para esta apuesta", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private boolean isValidBetAmount(int betAmount) {
        // Verificar si la apuesta es menor o igual al monedero actual
        return betAmount >= 0 && betAmount <= monedero;
    }

        private void updateNumeroGanadorTextView ( int winningNumber){
            // Actualizar el contenido del TextView con el número ganador
            numeroGanadorTextView.setText("Número Ganador: " + winningNumber);
            // Hacer visible el TextView
            numeroGanadorTextView.setVisibility(View.VISIBLE);
        }

        private void showResultMessage ( boolean isWinningBet, int winnings){
            String resultMessage;

            if (isWinningBet) {
                resultMessage = "¡Felicidades! Has ganado " + winnings + " monedas.";
                captureAndSaveScreenshot();
                NotificationHelper.showVictoryNotification( "Mensaje de victoria", this);
                guardarVictoriaEnCalendario();
            } else {
                resultMessage = "Lo siento, has perdido la apuesta.";
            }

            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        }
        private void updateUI () {
            // Actualiza las etiquetas, monedero, y turnos en la interfaz de usuario
            monederoLabel.setText("Monedero: " + Math.max(0, monedero)); // Monedero no puede ser negativo
            turnosLabel.setText("Turnos: " + turnos);

            if (turnos >= 3 || monedero <= 0) {
                // Mostrar mensaje de juego terminado
                String gameOverMessage = "";

                if (monedero <= 0) {
                    gameOverMessage = "¡No tienes saldo! Fin del juego.";
                } else {
                    gameOverMessage = "¡Has agotado los 3 turnos permitidos! Fin del juego.";
                }


                partidaDatabase.addPartidaRecord(nombre, turnos, monedero);
                setContentView(R.layout.activity_historico);
                mostrarRegistrosEnListView();

                Toast.makeText(this, gameOverMessage, Toast.LENGTH_LONG).show();


                new CountDownTimer(6000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        // Volver a InicioActivity
                        Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }.start();
            }
        }
        private int calculateWinningNumber () {
            return random.nextInt(37); // Números en la ruleta europea van de 0 a 36
        }

        private void checkColorBetResult () {
            int ran = new Random().nextInt(360) + 3600;
            RotateAnimation rotateAnimation = new RotateAnimation((float) lngDegrees, (float)
                    (lngDegrees + ran), 1, 0.5f, 1, 0.5f);

            lngDegrees = (lngDegrees + ran) % 360;
            rotateAnimation.setDuration((long) ran);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    panoRuletaRotation = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    panoRuletaRotation = true;
                    // Obtener el resultado después del giro
                    winningNumber = calculateWinningNumber();

                    // Obtener la apuesta a un rango de números seleccionada por el jugador
                    String selectedBet = getSelectedBet();

                    // Verificar el resultado de la apuesta de color
                    boolean isWinningColorBet = isWinningColorBet(winningNumber, selectedBet);

                    // Calcular las ganancias y actualizar el monedero
                    int betAmount = getBetAmount();
                    isWinningColorBet = isWinningColorBet(winningNumber, getSelectedBet());
                    winnings = calculateColorBetWinnings(isWinningColorBet, betAmount);

                    // Mostrar el número ganador en pantalla
                    updateNumeroGanadorTextView(winningNumber);

                    if (winnings == 0) {
                        // Restar la cantidad apostada si la apuesta es perdida
                        monedero -= betAmount;
                    } else {
                        monedero += winnings;
                    }

                    // Actualizar la interfaz de usuario
                    updateUI();
                    // Mostrar el resultado
                    showResultMessage(isWinningColorBet, winnings);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            panoRuleta.setAnimation(rotateAnimation);
            panoRuleta.startAnimation(rotateAnimation);
        }

        private void checkNumberRangeBetResult () {
            int ran = new Random().nextInt(360) + 3600;
            RotateAnimation rotateAnimation = new RotateAnimation((float) lngDegrees, lngDegrees + ran, 1, 0.5f, 1, 0.5f);

            lngDegrees = (lngDegrees + ran) % 360;
            rotateAnimation.setDuration(ran);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    panoRuletaRotation = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    panoRuletaRotation = true;
                    // Obtener el resultado después del giro
                    winningNumber = calculateWinningNumber();

                    // Obtener la apuesta a un rango de números seleccionada por el jugador
                    String selectedBet = getSelectedBet();

                    boolean isWinningRangeBet;

                    // Verificar el resultado de la apuesta de color
                    boolean isWinningColorBet = isWinningColorBet(winningNumber, selectedBet);

                    // Calcular las ganancias y actualizar el monedero
                    int betAmount = getBetAmount();
                    isWinningRangeBet = isWinningRangeBet(winningNumber, getSelectedBet());
                    winnings = calculateRangeBetWinnings(isWinningRangeBet, betAmount);

                    // Mostrar el número ganador en pantalla
                    updateNumeroGanadorTextView(winningNumber);

                    if (winnings == 0) {
                        // Restar la cantidad apostada si la apuesta es perdida
                        monedero -= betAmount;
                    } else {
                        monedero += winnings;
                    }

                    // Actualizar la interfaz de usuario
                    updateUI();
                    // Mostrar el resultado
                    showResultMessage(isWinningRangeBet, winnings);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            panoRuleta.setAnimation(rotateAnimation);
            panoRuleta.startAnimation(rotateAnimation);
        }

        private boolean isWinningColorBet ( int winningNumber, String selectedBet){
            // Lógica para determinar si la apuesta de color es ganadora
            boolean isRed = isNumberRed(winningNumber);

            return (selectedBet.equals("Rojo") && isRed) || (selectedBet.equals("Negro") && !isRed);
        }

        private boolean isNumberRed ( int number){
            // Números rojos en la ruleta europea
            int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};

            // Verificar si el número está en la lista de números rojos
            for (int redNumber : redNumbers) {
                if (number == redNumber) {
                    return true;
                }
            }
            return false;
        }

        private String getSelectedBet () {

            if (isRojoSelected) {
                return "Rojo";
            } else if (isNegroSelected) {
                return "Negro";
            } else if (is1stRangeSelected) {
                return "0-11";
            } else if (is2ndRangeSelected) {
                return "12-24";
            } else if (is3rdRangeSelected) {
                return "25-36";
            } else {
                return "";
            }
        }

        private boolean isWinningRangeBet ( int winningNumber, String selectedBet){
            // Lógica para comparar el número ganador con la apuesta a un rango de números seleccionada
            int selectedMin, selectedMax;

            // Obtener el rango de números seleccionado por el jugador
            switch (selectedBet) {
                case "0-11":
                    selectedMin = 0;
                    selectedMax = 11;
                    break;
                case "12-24":
                    selectedMin = 12;
                    selectedMax = 24;
                    break;
                case "25-36":
                    selectedMin = 25;
                    selectedMax = 36;
                    break;
                default:
                    // Manejar un caso no esperado o proporcionar un valor predeterminado
                    selectedMin = 0;
                    selectedMax = 0;
            }

            // Verificar si el número ganador está en el rango seleccionado
            return winningNumber >= selectedMin && winningNumber <= selectedMax;
        }

        private int calculateRangeBetWinnings ( boolean isWinningRangeBet, int betAmount){
            return isWinningRangeBet ? (betAmount) : 0;
        }

        private int calculateColorBetWinnings ( boolean isWinningColorBet, int betAmount){
            return isWinningColorBet ? (betAmount) : 0;
        }

        private int getBetAmount () {
            String betAmountStr = editTextBetAmount.getText().toString();
            if (!betAmountStr.isEmpty()) {
                return Integer.parseInt(betAmountStr);
            } else {
                // Mostrar un mensaje de error si no se ingresa ninguna cantidad
                Toast.makeText(this, "Ingresa una cantidad para apostar", Toast.LENGTH_SHORT).show();
                return -1; // Valor sentinela para indicar un error
            }
        }

        @Override
        public void onAnimationStart (Animation animation){

        }

        @Override
        public void onAnimationEnd (Animation animation){

        }

        @Override
        public void onAnimationRepeat (Animation animation){
        }

        private void mostrarRegistrosEnListView () {
            // Acceder al ListView en el diseño activity_historico.xml
            ListView listView = findViewById(R.id.lista);

            // Verificar si el ListView se encontró correctamente
            if (listView != null) {
                Cursor cursor = partidaDatabase.getTop10PartidaRecords();
                ArrayList<String> registros = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
                        int turnos = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TURNOS));
                        int monedero = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONEDERO));

                        // Convertir los valores int a String
                        String registro = "Nombre: " + nombre + ", Turnos: " + String.valueOf(turnos) + ", Monedero: " + String.valueOf(monedero);
                        registros.add(registro);
                    } while (cursor.moveToNext());
                }

                // Crear un adaptador personalizado para mostrar los datos en el ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);

                // Establecer el adaptador en el ListView
                listView.setAdapter(adapter);
            }
        }

        @Override
        public void onDestroy () {
            reproductorMusica.detenerMelodiaFondo();
            ReproductorMusica.getInstance(this).liberarRecursos();
            partidaDatabase.close();
            super.onDestroy();
        }

    private void captureAndSaveScreenshot() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Guardar la captura en el almacenamiento externo
        String filename = "screenshot_" + System.currentTimeMillis() + ".png";
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File screenshotFile = new File(directory, filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            MediaScannerConnection.scanFile(this, new String[]{screenshotFile.toString()}, null, null);

            Toast.makeText(this, "Captura de pantalla guardada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la captura de pantalla", Toast.LENGTH_SHORT).show();
        }
    }
    public static class NotificationHelper {

        private static final String CHANNEL_ID = "victory_channel";
        private static final int NOTIFICATION_ID = 1;

        public static void showVictoryNotification(String victoryMessage, MainActivity activity) {
            createNotificationChannel( activity);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("¡Victoria!")
                    .setContentText(victoryMessage)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{0, 1000}) // Vibración corta
                    .setAutoCancel(true); // La notificación se cierra al hacer clic

            NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        private static void createNotificationChannel( MainActivity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Victory Channel";
                String description = "Canal para notificaciones de victoria";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);

                NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void guardarVictoriaEnCalendario() {
        // Obtenemos el ContentResolver
        ContentResolver contentResolver = getContentResolver();

        // Creamos un nuevo evento en el calendario
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, "Victoria en Ruleta"); // Título del evento
        values.put(CalendarContract.Events.DESCRIPTION, "Ganaste en la ruleta"); // Descripción del evento
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // ID del calendario (1 es el calendario predeterminado)
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()); // Hora de inicio (actual)
        values.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 1000 * 60 * 60); // Hora de finalización (1 hora después)

        // Insertamos el evento en el calendario
        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    private void guardarUbicacionEnSQLite(double latitude, double longitude) {
        // Creamos un objeto ContentValues para insertar la ubicación en la base de datos
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        // Insertamos la ubicación en la base de datos
        partidaDatabase.insertarUbicacion(values);
    }
}