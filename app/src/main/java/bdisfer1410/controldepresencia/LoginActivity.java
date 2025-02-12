package bdisfer1410.controldepresencia;

import android.os.Bundle;
import android.util.Log;
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
    private EditText inputUser, inputPassword;
    private TextView outputError;
    private CheckBox checkboxRemember;

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
        findViewById(R.id.buttonLogin).setOnClickListener(v -> attemptLogIn());
    }

    private void attemptLogIn() {
        //TODO: Necesita refactorizar. ¿Crear clase Credentials?

        // Validar
        if (inputUser.getText().toString().isBlank()) {
            inputUser.setError(getString(R.string.err_user));
        }

        if (inputPassword.getText().toString().isBlank()) {
            inputPassword.setError(getString(R.string.err_password));
        }

        // ...
        boolean isValid = (inputUser.getError() == null) && (inputPassword.getError() == null);

        if (isValid) {
            Toast.makeText(this, "Valido", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}