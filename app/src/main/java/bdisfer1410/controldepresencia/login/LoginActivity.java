package bdisfer1410.controldepresencia.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import bdisfer1410.controldepresencia.clockin.MainActivity;
import bdisfer1410.controldepresencia.login.api.AuthRequest;
import bdisfer1410.controldepresencia.login.api.AuthResponse;
import bdisfer1410.controldepresencia.login.api.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Variables
    //region Views
    private ScrollView scrollview;
    private LinearLayout layoutInput;
    private EditText inputUser, inputPassword;
    private TextView outputError;
    private CheckBox checkboxRemember;
    //endregion

    //region Estado
    private SharedPreferences sharedPreferences;
    private AuthService authService;
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
        inputUser = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        outputError = findViewById(R.id.outputError);
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

        inputUser.setOnFocusChangeListener(onInputFocusScrollToIt);
        inputPassword.setOnFocusChangeListener(onInputFocusScrollToIt);

        // Cargar datos
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean autoLogin = sharedPreferences.getBoolean("remember", false);

        if (autoLogin) {
            checkboxRemember.setChecked(true);
            loadCredentialsFromStorage();
            attemptLogIn();
        }
    }

    // Funciones
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

        authService.login(credentials).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                boolean isResponseSuccessful = response.isSuccessful() && response.body() != null;

                if (!isResponseSuccessful) {
                    outputError.setText("ResponseNotSuccesfull"); // TODO: CHANGE
                    return;
                }

                AuthResponse auth = response.body();
                // TODO: TEMPORAL
                String a = String.valueOf(auth.isSuccess());
                a += " ";
                a += auth.isSuccess() ? auth.getToken() : auth.getMessage();
                outputError.setText(a);
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                outputError.setText("ERRORFAILURE");
            }
        });
    }

    /**
     * Reemplaza el valor de {@code credentials} con uno cuyos valores fueron sacados del formulario.
     */
    private void loadCredentialsFromFormulary() {
        String name = inputUser.getText().toString();
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
            return;
        }

        inputUser.setText(name);
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

        if (!credentials.isNameValid()) {
            inputUser.setError(getString(R.string.login_error_input_username));
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
        editor.putString("username", credentials.getUser());
        editor.putString("password", credentials.getPassword());

        editor.apply();
    }
}