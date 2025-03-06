package bdisfer1410.controldepresencia.view;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.api.ApiClient;
import bdisfer1410.controldepresencia.api.ApiService;
import bdisfer1410.controldepresencia.api.ProCallback;
import bdisfer1410.controldepresencia.api.clock.ClockResponse;
import bdisfer1410.controldepresencia.api.clock.action.ClockActionErrorResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendErrorResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendRequest;
import bdisfer1410.controldepresencia.models.ClockAction;
import bdisfer1410.controldepresencia.models.Tokens;
import bdisfer1410.controldepresencia.tools.Hour;
import bdisfer1410.controldepresencia.tools.Messages;


public class IncidenceActivity extends AppCompatActivity {
    //region Variables
    //region Views
    private EditText inputDatetime, inputDescription;
    private TextView outputError;
    private ProgressBar progressbar;
    private Button buttonLogin;
    //endregion
    //region Datos
    private Tokens tokens;
    private ClockAction latestClockAction;
    private Location latestLocation;
    private LocalTime startTime;
    private LocalTime exitTime;
    //endregion
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configuración inicial
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incidence);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargar el token
        Intent intent = getIntent();

        if (intent == null) {
            Log.e("TOKEN", "No se recibió ningún token");
            return;
        }
        else {
            tokens = new Tokens(intent);
            Log.d("TOKEN", String.format("El Intent recibio el de %s", tokens.access.getDebug()));
            Log.d("TOKEN", String.format("El Intent recibio el de %s", tokens.refresh.getDebug()));
        }

        // Configurar views
        inputDatetime = findViewById(R.id.inputDatetime);
        inputDescription = findViewById(R.id.inputDescription);

        inputDatetime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Crear un DatePickerDialog para la fecha
            DatePickerDialog datePickerDialog = new DatePickerDialog(IncidenceActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Crear un TimePickerDialog para la hora
                        TimePickerDialog timePickerDialog = new TimePickerDialog(IncidenceActivity.this,
                                (view1, hourOfDay, minute1) -> {
                                    // Mostrar fecha y hora en el formato deseado
                                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    calendar.set(year1, month1, dayOfMonth, hourOfDay, minute1);
                                    inputDatetime.setText(dateTimeFormat.format(calendar.getTime()));
                                }, hour, minute, true);
                        timePickerDialog.show();
                    }, year, month, day);
            datePickerDialog.show();
        });

    }

    /**
     * Valida los datos a enviar de la incidencia.
     *
     * @return True si todo es válido False si no.
    private boolean validateCredentials() {
        boolean valid = true;

        if (!inputDatetime.get()) {
            inputUsername.setError(getString(R.string.login_error_input_username));
            valid = false;
        }

        if (!credentials.isPasswordValid()) {
            inputPassword.setError(getString(R.string.login_error_input_password));
            valid = false;
        }

        return valid;
    }
     */

}