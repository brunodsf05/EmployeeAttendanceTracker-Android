package bdisfer1410.controldepresencia.login;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.RetrofitClient;
import bdisfer1410.controldepresencia.Util;
import bdisfer1410.controldepresencia.clockin.MainActivity;
import bdisfer1410.controldepresencia.login.api.AuthRequest;
import bdisfer1410.controldepresencia.login.api.AuthResponse;
import bdisfer1410.controldepresencia.login.api.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    // Variables
    //region Configuración
    private static final int MAX_ATTEMPTS_TO_AWAKE_SERVER = 2;
    //endregion

    //region Views
    private ScrollView scrollview;
    private LinearLayout layoutInput;
    private EditText inputUsername, inputPassword;
    private TextView outputError;
    private ProgressBar progressbar;
    private CheckBox checkboxRemember;
    //endregion

    //region Estado
    private SharedPreferences sharedPreferences;
    private AuthService authService;
    private int times_attempted_to_awake_server = 0;
    //endregion

    //region Datos
    private AuthRequest credentials;
    //endregion

    // Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincular vistas a variables
        scrollview = findViewById(R.id.scrollview);
        layoutInput = findViewById(R.id.layoutInput);
        inputUsername = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        outputError = findViewById(R.id.outputError);
        progressbar = findViewById(R.id.progressbar);
        checkboxRemember = findViewById(R.id.checkboxRemember);

        // Configurar acciones
        authService = RetrofitClient.getInstance().create(AuthService.class);

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            loadCredentialsFromFormulary();

            if (checkboxRemember.isChecked()) {
                saveCredentials();
            }

            attemptLogIn();
        });

        // Centrar linearLayout en el espacio disponible dejado por el teclado
        View.OnFocusChangeListener onInputFocusScrollToIt = (v, hasFocus) -> {
            if (hasFocus) {
                v.postDelayed(() -> scrollview.smoothScrollTo(0, layoutInput.getTop()), 100);
            }
        };

        inputUsername.setOnFocusChangeListener(onInputFocusScrollToIt);
        inputPassword.setOnFocusChangeListener(onInputFocusScrollToIt);

        // Cargar datos
        Intent intent = getIntent();
        boolean cancelAutoLogin = intent.getBooleanExtra("CANCEL_AUTO_LOGIN", false);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean loadCredentials = sharedPreferences.getBoolean("remember", false);

        if (loadCredentials) {
            checkboxRemember.setChecked(true);
            loadCredentialsFromStorage();

            if (cancelAutoLogin) return;

            attemptLogIn();
        }
    }

    // Inicio de sesión
    /**
     * Intenta pasar al {@link MainActivity} mediante un inicio de sesión con {@code credentials}.
     */
    private void attemptLogIn() {
        boolean isCredentialsValid = validateCredentials();

        if (!isCredentialsValid) {
            outputError.setText(R.string.login_error_input_credentials);
            return;
        }

        outputError.setText("");
        progressbar.setVisibility(VISIBLE);

        authService.login(credentials).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                // Puede que el servidor estuviese inactivo, por lo que intentamos de nuevo
                // No es infinito, esta limitado
                if (response.code() == 500) {
                    new Handler(Looper.getMainLooper()).post(
                            () -> tryAgainAttemptLogInBecauseServerWasSleeping()
                    );
                    return;
                }

                // El servidor está despierto
                progressbar.setVisibility(GONE);

                // Verificar la respuesta JSON
                boolean isResponseSuccessful = response.isSuccessful() && response.body() != null;
                Log.i("API", String.format("El código de la respuesta fue succesful: %b", response.isSuccessful()));
                Log.i("API", String.format("El código de la respuesta es el número: %d", response.code()));
                Log.i("API", String.format("El cuerpo de la respuesta es null: %b", response.body() == null));

                if (!isResponseSuccessful) {
                    outputError.setText(R.string.login_error_authservice_response);
                    return;
                }

                // Manejar la respuesta del servidor
                AuthResponse auth = response.body();

                if (auth.isSuccess()) {
                    Log.d("API", String.format("Token: %s...", auth.getToken().substring(0,3)));

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("TOKEN", auth.getToken());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
                else {
                    Log.e("API", String.format("Error: %s", auth.getError()));

                    outputError.setText(
                            Util.getMessage(
                                    LoginActivity.this,
                                    auth.getErrorKey(),
                                    R.string.app_error_anyservice_unknownkey
                            )
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.e("API", "Error de conexión con el servidor");
                progressbar.setVisibility(GONE);
                outputError.setText(R.string.login_error_authservice_connection);
            }
        });
    }

    /**
     * Reintenta el inicio de sesión si es que el servidor estuviese en reposo.
     *
     * <h1>Contexto</h1>
     * Si el servidor de PythonAnywhere no recibe durante mucho tiempo una petición, se pondrá en reposo.
     * Lo que hará que {@code attemptLogIn()} falle, recibiendo como respuesta el código 500 (inactividad).
     * Esto de hecho se soluciona solo ya que la llamada ocasiona que el servidor vuelva a ponerse en marcha.
     */
    private void tryAgainAttemptLogInBecauseServerWasSleeping() {
        if (times_attempted_to_awake_server > MAX_ATTEMPTS_TO_AWAKE_SERVER) {
            Log.d("API", String.format(
                    "Parece ser que el servidor está inactivo, se repite la petición %d/%d",
                    times_attempted_to_awake_server,
                    MAX_ATTEMPTS_TO_AWAKE_SERVER
            ));
            attemptLogIn();
            times_attempted_to_awake_server++;
        }
        else {
            Log.e("API", "No se reintento la petición de login, se alcanzo el límite de intentos");
        }
    }

    // Credenciales
    /**
     * Reemplaza el valor de {@code credentials} con uno cuyos valores fueron sacados del formulario.
     */
    private void loadCredentialsFromFormulary() {
        String name = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        credentials = new AuthRequest(name, password);
    }

    /**
     * Carga los {@code credentials} guardados localmente.
     */
    private void loadCredentialsFromStorage() {
        String name = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);

        if (name == null || password == null) {
            outputError.setText(R.string.login_error_load_credentials);
        }

        inputUsername.setText(name);
        inputPassword.setText(password);

        credentials = new AuthRequest(name, password);
    }

    /**
     * Verifica que los valores de las credenciales sean válidos.
     * Si alguno no lo es, se indicará en el campo inválido mediante un símbolo de error.
     *
     * @return True si las credenciales son validas, False si hay algún fallo.
     */
    private boolean validateCredentials() {
        boolean valid = true;

        if (!credentials.isUsernameValid()) {
            inputUsername.setError(getString(R.string.login_error_input_username));
            valid = false;
        }

        if (!credentials.isPasswordValid()) {
            inputPassword.setError(getString(R.string.login_error_input_password));
            valid = false;
        }

        return valid;
    }

    /**
     * Guarda las {@code credentials} actuales en el almacenamiento.
     */
    private void saveCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("remember", true);
        editor.putString("username", credentials.getUsername());
        editor.putString("password", credentials.getPassword());

        editor.apply();
    }
}