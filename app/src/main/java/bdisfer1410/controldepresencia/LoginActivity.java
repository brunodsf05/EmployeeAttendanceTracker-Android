package bdisfer1410.controldepresencia;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    // Views
    private EditText inputUser, inputPassword;
    private TextView outputError;
    private CheckBox checkboxRemember;

    // Datos
    private UserCredentials credentials;



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
        inputUser = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        outputError = findViewById(R.id.outputError);
        checkboxRemember = findViewById(R.id.checkboxRemember);

        // Configurar acciones
        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            loadCredentialsFromFormulary();
            attemptLogIn();
        });
    }



    private void attemptLogIn() {
        boolean isCredentialsValid = validateCredentials();

        if (isCredentialsValid) {
            outputError.setText("");
        }
        else {
            outputError.setText(R.string.login_error_credentials);
        }
    }



    private void loadCredentialsFromFormulary() {
        String name = inputUser.getText().toString();
        String password = inputPassword.getText().toString();

        credentials = new UserCredentials(name, password);
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
            inputUser.setError(getString(R.string.login_error_user));
            valid = false;
        }

        if (!credentials.isPasswordValid()) {
            inputPassword.setError(getString(R.string.login_error_password));
            valid = false;
        }

        return valid;
    }
}