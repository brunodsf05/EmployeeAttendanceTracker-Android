package bdisfer1410.controldepresencia.view;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import bdisfer1410.controldepresencia.api.ApiClient;
import bdisfer1410.controldepresencia.api.ProCallback;
import bdisfer1410.controldepresencia.api.auth.AuthErrorResponse;
import bdisfer1410.controldepresencia.api.auth.AuthResponse;
import bdisfer1410.controldepresencia.models.Tokens;
import bdisfer1410.controldepresencia.tools.Messages;
import bdisfer1410.controldepresencia.api.auth.AuthRequest;
import bdisfer1410.controldepresencia.api.ApiService;


public class LoginActivity extends AppCompatActivity {
    // Variables
    //region Views
    private ScrollView scrollview;
    private LinearLayout layoutInput;
    private EditText inputUsername, inputPassword, inputServer;
    private TextView outputError;
    private ProgressBar progressbar;
    private Button buttonLogin;
    private CheckBox checkboxRemember;
    //endregion

    //region Estado
    private SharedPreferences sharedPreferences;
    private ApiService authService;
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
        inputServer = findViewById(R.id.inputServer);
        outputError = findViewById(R.id.outputError);
        progressbar = findViewById(R.id.progressbar);
        buttonLogin = findViewById(R.id.buttonLogin);
        checkboxRemember = findViewById(R.id.checkboxRemember);

        // Configurar acciones
        buttonLogin.setOnClickListener(v -> {
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
        inputServer.setOnFocusChangeListener(onInputFocusScrollToIt);

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
     * Intenta pasar al {@link ClockActivity} mediante un inicio de sesión con {@code credentials}.
     */
    private void attemptLogIn() {
        // Validar entrada del usuario
        boolean isCredentialsValid = validateCredentials();

        if (!isCredentialsValid) {
            outputError.setText(R.string.login_error_input_credentials);
            return;
        }

        // Create api client
        try {
            ApiClient.url = credentials == null ? "" : credentials.getServer();
            authService = ApiClient.getRetrofit().create(ApiService.class);
        }
        catch (Exception e) {
            Log.e("API", "Couldn't instantiate retrofit possibly due to invalid url", e);
            outputError.setText(R.string.login_error_invalid_server);
            return;
        }

        // Iniciar animación de carga
        buttonLogin.setEnabled(false);
        outputError.setText("");
        progressbar.setVisibility(View.VISIBLE);

        // Cuando el servidor responda...
        authService.login(credentials).enqueue(new ProCallback<AuthResponse, AuthErrorResponse>() {
            @Override
            protected Class<AuthErrorResponse> getErrorClass() {
                return AuthErrorResponse.class;
            }

            @Override
            public void beforeResponse() {
                /* No hay preparación */
            }

            @Override
            public void afterResponse() {
                progressbar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
            }

            @Override
            public void onOkResponse(@NonNull AuthResponse okBody) {
                Tokens tokens = okBody.getTokens();

                Log.d("API", "¡Se recibieron los tokens!");
                Log.d("TOKEN", String.format("El servidor devolvió el token de %s", tokens.access.getDebug()));
                Log.d("TOKEN", String.format("El servidor devolvió el token de %s", tokens.refresh.getDebug()));

                Intent intent = new Intent(LoginActivity.this, ClockActivity.class);
                tokens.intoIntent(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onErrorResponse(@NonNull AuthErrorResponse errorBody) {
                Log.e("API", String.format("Error: %s", errorBody.getShortError()));

                outputError.setText(
                        Messages.fromKey(
                                LoginActivity.this,
                                errorBody.getError(),
                                R.string.app_error_anyservice_unknownkey
                        )
                );
            }

            @Override
            public void onNullResponse() {
                outputError.setText(R.string.app_error_anyservice_response);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API", "Error de conexión con el servidor", t);
                outputError.setText(R.string.app_error_anyservice_connection);
            }
        });
    }

    // Credenciales
    /**
     * Reemplaza el valor de {@code credentials} con uno cuyos valores fueron sacados del formulario.
     */
    private void loadCredentialsFromFormulary() {
        String name = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String server = inputServer.getText().toString();

        credentials = new AuthRequest(name, password, server);
    }

    /**
     * Carga los {@code credentials} guardados localmente.
     */
    private void loadCredentialsFromStorage() {
        String name = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
        String server = sharedPreferences.getString("server", null);

        if (name == null || password == null || server == null) {
            outputError.setText(R.string.login_error_load_credentials);
        }

        inputUsername.setText(name);
        inputPassword.setText(password);
        inputServer.setText(server);

        credentials = new AuthRequest(name, password, server);
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

        if (!credentials.isServerValid()) {
            inputServer.setError(getString(R.string.login_error_input_server));
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
        editor.putString("server", credentials.getServer());

        editor.apply();
    }
}