package com.example.retrofitersruleta;

import android.os.Bundle;
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

    private int monedero = 100;
    private int turnos = 0;
    private Button buttonStart;
    private float lngDegrees = 0;

    private ImageView panoRuleta;
    private EditText editTextBetAmount;
    private boolean panoRuletaRotation = true;

    private boolean isRojoSelected = false;
    private boolean isNegroSelected = false;

    private TextView monederoLabel;
    private TextView turnosLabel;

    private TextView numeroGanadorTextView;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar las vistas
        buttonStart = findViewById(R.id.buttonstart);
        panoRuleta = findViewById(R.id.panoruleta);
        editTextBetAmount = findViewById(R.id.editTextBetAmount);
        monederoLabel = findViewById(R.id.monederoLabel);
        turnosLabel = findViewById(R.id.turnosLabel);
        numeroGanadorTextView = findViewById(R.id.numeroGanadorTextView);


        // Configurar el botón de inicio
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonRotation(v);
            }
        });

        // Configurar los botones de apuesta
        configureBetButtons();

        // Otras configuraciones que puedas necesitar...
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
        // Lógica para manejar el clic en un botón de apuesta
        // Eliminar el código que cambia el estado de los botones al ser pulsados
        // ...

        if (betButton.getId() == R.id.btnRojo) {
            isRojoSelected = !isRojoSelected;
        } else if (betButton.getId() == R.id.btnNegro) {
            isNegroSelected = !isNegroSelected;
        }
        // Puedes agregar lógica adicional para otros botones si es necesario
        // No necesitas cambiar el estado visual de los botones de rango 0-11, 12-24, 25-36
    }

    public void onClickButtonRotation(View v) {
        if (panoRuletaRotation) {
            // Resto del código de animación...

            // Lógica de apuesta
            int betAmount = getBetAmount();
            if (isRojoSelected || isNegroSelected) {
                // Si es una apuesta de color
                checkColorBetResult();
            } else {
                // Si es una apuesta a un rango de números
                checkNumberRangeBetResult();
            }
            // Incrementar el contador de turnos después de cada rotación exitosa
            turnos++;

            // Actualizar la interfaz de usuario
            updateUI();
        }
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
                int winningNumber = calculateWinningNumber();
                updateNumeroGanadorTextView(winningNumber);

                // Verificar el resultado de la apuesta de color
                boolean isWinningColorBet = isWinningColorBet(winningNumber);

                // Calcular las ganancias y actualizar el monedero
                int betAmount = getBetAmount();
                int winnings = calculateColorBetWinnings(isWinningColorBet, getBetAmount());
                monedero += winnings;

                // Restar la cantidad apostada si la apuesta es perdida
                if (winnings == 0) {
                    monedero -= betAmount;
                } else {
                    monedero += winnings;
                }

                // Actualizar la interfaz de usuario
                updateUI();
                // Mostrar el resultado
                showResultMessage(isWinningColorBet, winnings);


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
        // Actualiza las etiquetas, monedero, y turnos en tu interfaz de usuario
        monederoLabel.setText("Monedero: " + monedero);
        turnosLabel.setText("Turnos: " + turnos);

        // Restablece el estado de los botones después de 3 turnos
        if (turnos % 3 == 0) {
            resetButtons();

        // Verificar si el monedero es menor que 0
        if (monedero <= 0) {
            // Mostrar mensaje y cerrar la aplicación
            Toast.makeText(this, "No tienes saldo, fin del juego", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad actual (la aplicación en este caso)
        }
    }

    }
    private void resetButtons() {
        // Lógica para restablecer el estado de los botones
        isRojoSelected = false;
        isNegroSelected = false;
    }

    private int calculateColorBetWinnings(boolean isWinningColorBet, int betAmount) {
        // Lógica para calcular las ganancias en función de la apuesta de color y la cantidad apostada
        // Duplica la cantidad apostada si la apuesta es ganadora, de lo contrario, la pierde.
        return isWinningColorBet ? (betAmount) : 0;
    }




    private int calculateWinningNumber() {
        // Lógica para obtener el número ganador después del giro en una ruleta europea

        // El orden de los números en una ruleta europea
        int[] europeanRouletteNumbers = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34,
                6, 27, 13, 36, 11, 30, 8, 23, 10, 5,
                24, 16, 33, 1, 20, 14, 31, 9, 22, 18,
                29, 7, 28, 12, 35, 3, 26};

        // El rango de grados por número en una ruleta europea
        int degreesPerNumber = 360 / europeanRouletteNumbers.length;

        // Ajustar el ángulo de inicio (90 grados en tu caso)
        int adjustedStartAngle = 90;

        // Calcular el número ganador en función del ángulo actual (lngDegrees)
        int winningNumber = (int) ((lngDegrees + adjustedStartAngle) / degreesPerNumber);

        // Asegurarse de que el número ganador esté en el rango correcto
        winningNumber = (winningNumber + europeanRouletteNumbers.length) % europeanRouletteNumbers.length;

        // Obtener el número ganador de la ruleta europea
        winningNumber = europeanRouletteNumbers[winningNumber];

        return winningNumber;
    }
    private boolean isWinningColorBet(int winningNumber) {
        // Lógica para determinar si la apuesta de color es ganadora
        boolean isRed = isNumberRed(winningNumber);

        // Verificar si la apuesta de color seleccionada por el jugador es rojo
        if (isRojoSelected) {
            return isRed;
        }
        // Verificar si la apuesta de color seleccionada por el jugador es negro
        else if (isNegroSelected) {
            return !isRed;
        }
        // Si no se ha seleccionado ningún color, la apuesta no puede ser ganadora
        return false;
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
                int winningNumber = calculateWinningNumber();

                // Obtener la apuesta a un rango de números seleccionada por el jugador
                String selectedBet = getSelectedBet();

                // Verificar el resultado de la apuesta a un rango de números
                boolean isWinningRangeBet = isWinningRangeBet(winningNumber, selectedBet);

                // Calcular las ganancias y actualizar el monedero
                int winnings = calculateRangeBetWinnings(isWinningRangeBet, getBetAmount());
                monedero += winnings;

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
    private String getSelectedBet() {
        // Lógica para obtener la opción de apuesta seleccionada por el jugador (Rojo, Negro, 0-11, 12-24, 25-36)
        // Aquí asumo que tienes algún mecanismo para determinar la opción seleccionada.
        // Puedes adaptar esta lógica según la implementación específica de tu interfaz de usuario.

        // Si prefieres utilizar botones de opción (por ejemplo, RadioButton), puedes obtener la selección de esa manera.

        // En este ejemplo, estoy asumiendo que tienes botones de apuesta en tu interfaz y cada botón tiene un identificador único.
        // Debes ajustar esta lógica según cómo estén implementados tus botones.

        if (isRojoSelected) {
            return "Rojo";
        } else if (isNegroSelected) {
            return "Negro";
        } else {
            // Aquí puedes manejar otras opciones de apuesta, como 0-11, 12-24, 25-36, según la implementación de tu interfaz.
            // En este ejemplo, devolveré una cadena vacía, pero debes adaptarlo a tu lógica real.
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
        // Lógica para calcular las ganancias en función de la apuesta a un rango de números y la cantidad apostada
        // Duplica la cantidad apostada si la apuesta es ganadora, de lo contrario, la pierde.
        return isWinningRangeBet ? (betAmount * 2) : 0;
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
        // Código para manejar la repetición de la animación
    }
}