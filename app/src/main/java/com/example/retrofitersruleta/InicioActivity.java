package com.example.retrofitersruleta;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;


public class InicioActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Button buttonIniciarJuego;
    private Button buttonVerHistorico;
    private Switch switchAudio;
    private ReproductorMusica reproductorMusica;


    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity);
        switchAudio = findViewById(R.id.switchAudio);
        reproductorMusica = ReproductorMusica.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        Button autenticacion = findViewById(R.id.btnAutenticacion);
        autenticacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });


        // Inicializar las vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        buttonIniciarJuego = findViewById(R.id.buttonIniciarJuego);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);

        // Configurar el botón de inicio de juego
        buttonIniciarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();                // Obtener el nombre ingresado por el usuario
                String nombreJugador = editTextNombre.getText().toString();

                // Verificar si se ingresó un nombre
                if (!TextUtils.isEmpty(nombreJugador)) {
                    // Crear un intent para pasar el nombre a la actividad principal (MainActivity)
                    Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                    intent.putExtra("nombre_jugador", nombreJugador);

                    // Iniciar la actividad principal
                    startActivity(intent);

                    // Finalizar esta actividad para que el usuario no pueda regresar a ella
                    finish();
                } else {
                    editTextNombre.setError("Ingresa tu nombre");
                }
            }

        });

        buttonVerHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();                // Obtener el nombre ingresado por el usuario
                // Utilizar la clase correcta
                Intent intent = new Intent(InicioActivity.this, HistoricoActivity.class);
                startActivity(intent);
            }
        });
        // Configurar el Switch para activar/desactivar audio
        switchAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si el Switch está activado, reproducir el sonido del botón y la melodía de fondo
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();
                ReproductorMusica.getInstance(InicioActivity.this).reproducirMelodiaFondo();
            } else {
                // Si el Switch está desactivado, detener la reproducción del sonido del botón y la melodía de fondo
                ReproductorMusica.getInstance(InicioActivity.this).detenerTodo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reproductorMusica.reproducirMelodiaFondo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reproductorMusica.detenerMelodiaFondo();
        // Liberar recursos del ReproductorMusica cuando la actividad se destruye
        ReproductorMusica.getInstance(this).liberarRecursos();
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_api_key)) // Cambia a tu clave de API
                .requestEmail()
                .build();


        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado de iniciar sesión con Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(InicioActivity.this, "Inicio de sesión exitoso como " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InicioActivity.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
