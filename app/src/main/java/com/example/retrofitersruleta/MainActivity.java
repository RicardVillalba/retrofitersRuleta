package com.example.retrofitersruleta;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;




import androidx.appcompat.app.AppCompatActivity;


import java.util.Random;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {


   private int id;
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


   private WalletDatabase walletDatabase;




   private final Random random = new Random();


   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);


       // Initialize the WalletDatabase
       walletDatabase = new WalletDatabase(this);


       WalletDbHelper dbHelper = new WalletDbHelper(this); // "this" es un contexto válido
       dbHelper.copyDatabaseFromAssets();


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
   }


      private void configureBetButtons() {
       // Configurar los botones de apuesta
       configureBetButton(R.id.btnRojo);
       configureBetButton(R.id.btnNegro);
       configureBetButton(R.id.btn0_11);
       configureBetButton(R.id.btn12_24);
       configureBetButton(R.id.btn25_36);
   }


   private void configureBetButton(int buttonId) {
       Button betButton = findViewById(buttonId);
       betButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               handleBetButtonClick(betButton);
           }
       });
   }


   private void handleBetButtonClick(Button betButton) {
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


   public void onClickButtonRotation(View v) {
       if (panoRuletaRotation) {
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


           // Insertar datos en la base de datos
       }
   }


   private void updateNumeroGanadorTextView(int winningNumber) {
       // Actualizar el contenido del TextView con el número ganador
       numeroGanadorTextView.setText("Número Ganador: " + winningNumber);
       // Hacer visible el TextView
       numeroGanadorTextView.setVisibility(View.VISIBLE);
   }


   private void showResultMessage(boolean isWinningBet, int winnings) {
       String resultMessage;


       if (isWinningBet) {
           resultMessage = "¡Felicidades! Has ganado " + winnings + " monedas.";
       } else {
           resultMessage = "Lo siento, has perdido la apuesta.";
       }


       Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
   }
   private void updateUI() {
       // Actualiza las etiquetas, monedero, y turnos en la interfaz de usuario
       monederoLabel.setText("Monedero: " + monedero);
       turnosLabel.setText("Turnos: " + turnos);


       if (turnos >= 3 || monedero <= 0) {
           // Mostrar mensaje de juego terminado
           String gameOverMessage = "";


           if (monedero <= 0) {
               gameOverMessage = "¡No tienes saldo! Fin del juego.";
           } else {
               gameOverMessage = "¡Has agotado los 3 turnos permitidos! Fin del juego.";
           }


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
   private int calculateWinningNumber() {
       return random.nextInt(37); // Números en la ruleta europea van de 0 a 36
   }


   private void checkColorBetResult() {
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


   private void checkNumberRangeBetResult() {
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


               boolean isWinningRangeBet = isWinningRangeBet(winningNumber, selectedBet);


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


   private boolean isWinningColorBet(int winningNumber, String selectedBet) {
       // Lógica para determinar si la apuesta de color es ganadora
       boolean isRed = isNumberRed(winningNumber);


       return (selectedBet.equals("Rojo") && isRed) || (selectedBet.equals("Negro") && !isRed);
   }


   private boolean isNumberRed(int number) {
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


       private String getSelectedBet() {
           // Puedes adaptar esta lógica según la implementación específica de tu interfaz de usuario.


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


   private boolean isWinningRangeBet(int winningNumber, String selectedBet) {
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


   private int calculateRangeBetWinnings(boolean isWinningRangeBet, int betAmount) {
       return isWinningRangeBet ? (betAmount) : 0;
   }


   private int calculateColorBetWinnings(boolean isWinningColorBet, int betAmount) {
       return isWinningColorBet ? (betAmount) : 0;
   }


   private int getBetAmount() {
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
   public void onAnimationStart(Animation animation) {


   }


   @Override
   public void onAnimationEnd(Animation animation) {


   }


   @Override
   public void onAnimationRepeat(Animation animation) {
   }


   @Override
   protected void onDestroy() {
       walletDatabase.close();
       super.onDestroy();
   }
}
