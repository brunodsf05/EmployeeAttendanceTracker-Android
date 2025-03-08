package bdisfer1410.controldepresencia.view;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.models.Tokens;


public class IncidenceActivity extends AppCompatActivity {
    //region Variables
    //region Configuración
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //endregion
    //region Views
    private EditText inputDatetime, inputDescription;
    private TextView outputError;
    private ProgressBar progressbar;
    private Button buttonSend;
    //endregion
    //region Datos
    private Tokens tokens;
    private Date datetime;
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
        buttonSend = findViewById(R.id.buttonSend);

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
                                    calendar.set(year1, month1, dayOfMonth, hourOfDay, minute1);
                                    datetime = calendar.getTime();
                                    inputDatetime.setText(DATETIME_FORMAT.format(datetime));
                                }, hour, minute, true);
                        timePickerDialog.show();
                    }, year, month, day);
            datePickerDialog.show();
        });

        buttonSend.setOnClickListener(v -> Log.d("Vaina", String.valueOf(validateIncidence())));
    }

    /**
     * Valida los datos a enviar de la incidencia.
     *
     * @return True si todo es válido False si no. */
    private boolean validateIncidence() {
        boolean valid = true;

        inputDatetime.setError(null);
        inputDescription.setError(null);

        if (datetime == null) {
            inputDatetime.setError(getString(R.string.incidence_error_input_datetime));
            valid = false;
        }

        if (inputDescription.getText().toString().isEmpty()) {
            inputDescription.setError(getString(R.string.incidence_error_input_description));
            valid = false;
        }

        return valid;
    }

}