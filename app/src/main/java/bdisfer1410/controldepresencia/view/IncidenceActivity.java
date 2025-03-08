package bdisfer1410.controldepresencia.view;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.api.ApiClient;
import bdisfer1410.controldepresencia.api.ApiService;
import bdisfer1410.controldepresencia.api.ProCallback;
import bdisfer1410.controldepresencia.api.incidence.IncidenceErrorResponse;
import bdisfer1410.controldepresencia.api.incidence.IncidenceRequest;
import bdisfer1410.controldepresencia.api.incidence.IncidenceResponse;
import bdisfer1410.controldepresencia.models.Tokens;
import bdisfer1410.controldepresencia.tools.Messages;


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
    private ApiService service;
    private Date datetime;
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

        service = ApiClient.retrofit.create(ApiService.class);

        // Cargar el token
        Intent intent = getIntent();

        if (intent == null) {
            //TEMP
            tokens = new Tokens("", "");
            /*
            Log.e("TOKEN", "No se recibió ningún token");
            return;
             */
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
        outputError = findViewById(R.id.outputError);
        progressbar = findViewById(R.id.progressbar);

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

        buttonSend.setOnClickListener(v -> {
            boolean canSendIncidence = validateIncidence();

            if (canSendIncidence) {
                sendIncidence();
            }
            else {
                Log.e("API", "La incidencia no es válida para enviarse.");
            }
        });
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

    /**
     * Intenta enviar una incidencia.
     */
    private void sendIncidence() {
        // Preparar la entrada
        String description = inputDescription.getText().toString().trim();

        // Iniciar animación de carga
        buttonSend.setEnabled(false);
        outputError.setText("");
        progressbar.setVisibility(View.VISIBLE);

        // Cuando el servidor responda...
        String header = tokens.access.getHeader();
        IncidenceRequest request = new IncidenceRequest(datetime, description);
        service.sendIncidence(header, request).enqueue(new ProCallback<IncidenceResponse, IncidenceErrorResponse>() {
            @Override
            protected Class<IncidenceErrorResponse> getErrorClass() {
                return IncidenceErrorResponse.class;
            }

            @Override
            public void beforeResponse() {
                /* No hay preparación */
            }

            @Override
            public void afterResponse() {
                progressbar.setVisibility(View.GONE);
                buttonSend.setEnabled(true);
            }

            @Override
            public void onOkResponse(@NonNull IncidenceResponse okBody) {
                Log.d("API", "Se envió con éxito");
                finish();
            }

            @Override
            public void onErrorResponse(@NonNull IncidenceErrorResponse errorBody) {
                Log.e("API", String.format("Error: %s", errorBody.getShortError()));

                outputError.setText(
                        Messages.fromKey(
                                IncidenceActivity.this,
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
}