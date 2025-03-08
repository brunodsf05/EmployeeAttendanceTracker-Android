package bdisfer1410.controldepresencia.view;


import static android.view.View.VISIBLE;
import static android.view.View.GONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

import java.time.LocalTime;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.api.ApiClient;
import bdisfer1410.controldepresencia.api.ApiService;
import bdisfer1410.controldepresencia.api.ProCallback;
import bdisfer1410.controldepresencia.api.clock.action.ClockActionErrorResponse;
import bdisfer1410.controldepresencia.api.clock.ClockResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendErrorResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendRequest;
import bdisfer1410.controldepresencia.models.ClockAction;
import bdisfer1410.controldepresencia.models.Tokens;
import bdisfer1410.controldepresencia.tools.Hour;
import bdisfer1410.controldepresencia.tools.Messages;


public class ClockActivity extends AppCompatActivity {
    //region Variables
    //region Configuración
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private static final String KEY_LATESTCLOCKSTATE = "LATEST_CLOCK_STATE";
    //endregion
    //region Views
    private Toolbar toolbar;
    private Button buttonClock;
    private TextView feedbackTitle, feedbackDescription, feedbackError, feedbackWarning;
    private TextView infoStartHour, infoExitHour;
    private SwipeRefreshLayout swipeRefreshLayout;
    //endregion
    //region Estado
    private ApiService service;
    private FusedLocationProviderClient fusedLocationClient;
    //endregion
    //region Semáforos
    private boolean isUpdatingLocation = false;
    private boolean isUpdatingClockAction = false;
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
        setContentView(R.layout.activity_clock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViews();
        configureViews();
        service = ApiClient.retrofit.create(ApiService.class);

        // Manejar la geolocalización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        updateLocation();

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

        // Restaurar pantalla tras rotar
        if (savedInstanceState != null) {
            latestClockAction = (ClockAction) savedInstanceState.getSerializable(KEY_LATESTCLOCKSTATE);
            if (latestClockAction != null) {
                Log.d("RESTORATION", String.format("Se restauró latestClockAction a: %s", latestClockAction.name()));
                configureInterface();
                return;
            }
            else {
                Log.w("RESTORATION", "latestClockAction era null en savedInstanceState.");
            }
        }

        // Verificar acción de fichaje
        updateClockAction();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("LOCATION", "¡Se consiguió el permiso de ubicación precisa!");
                    updateLocation();
                }
                else {
                    Log.e("LOCATION", "Se denegó el permiso de ubicación precisa.");
                    setFeedbackError(getString(R.string.clock_error_location_preciserequired));
                }
            }
            else {
                Log.e("LOCATION", "No se recibieron resultados del permiso.");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_LATESTCLOCKSTATE, latestClockAction);
    }

    private void setFeedbackError(String message) {
        feedbackError.setText(message);
        feedbackError.setVisibility((message.isEmpty()) ? GONE : VISIBLE);
    }

    //region Configuración
    //region Inicial
    private void findViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        toolbar = findViewById(R.id.toolbar);
        buttonClock = findViewById(R.id.btnClock);
        feedbackTitle = findViewById(R.id.feedbackTitle);
        feedbackDescription = findViewById(R.id.feedbackDescription);
        feedbackError = findViewById(R.id.feedbackError);
        feedbackWarning = findViewById(R.id.feedbackWarning);
        infoStartHour = findViewById(R.id.infoStartHour);
        infoExitHour = findViewById(R.id.infoExitHour);
    }

    private void configureViews() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("SWIPEREFRESH", "Actualizando ubicación y estado de fichaje");
            updateLocation();
            updateClockAction();
        });

        toolbar.setTitle(R.string.clock_title);
        setSupportActionBar(toolbar);

        setFeedbackError("");

        buttonClock.setOnClickListener(v -> {
            Log.d("CLICK", "Fichar");
            updateClockSend();
        });
    }
    //endregion

    //region Estado de fichaje
    private void configureInterface() {
        // Cambios principales
        boolean canApplyClockActionStyling = latestClockAction != null;

        Log.d("GUI", String.format("Configurando la interfaz con: \"latestClockAction\"=%s", (canApplyClockActionStyling) ? latestClockAction.name() : "null"));

        if (canApplyClockActionStyling) {
            feedbackWarning.setVisibility(GONE);
            buttonClock.setEnabled(latestClockAction.canClock());
            buttonClock.setText(latestClockAction.getButtonText());
            feedbackTitle.setText(latestClockAction.getFeedbackTitle());
            feedbackDescription.setText(latestClockAction.getFeedbackDescription());


        }

        // Parches de ubicación
        boolean canApplyNoLocationStyling = latestLocation == null && latestClockAction != null && latestClockAction.doesLocationMatter();

        Log.d("GUI", String.format("¿Se aplicarán los parches de GPS no válido? %b", canApplyNoLocationStyling));

        if (canApplyNoLocationStyling) {
            buttonClock.setEnabled(ClockAction.TOBEIN_WORK.canClock());
            feedbackWarning.setVisibility(VISIBLE);
        }

        // Configurar las horas de entrada y salida
        infoStartHour.setText(Hour.format(startTime));
        infoExitHour.setText(Hour.format(exitTime));

        // Manejo del símbolo de progreso
        boolean isUpdating = isUpdatingLocation || isUpdatingClockAction;

        Log.d("GUI", String.format("¿Se mantendrá el spinner de refresh? %b", isUpdating));
        Log.d("REFRESHPROGRESS", "INICIO");
        Log.d("REFRESHPROGRESS", String.format("¿isUpdatingLocation? %b", isUpdatingLocation));
        Log.d("REFRESHPROGRESS", String.format("¿isUpdatingClockAction? %b", isUpdatingClockAction));

        swipeRefreshLayout.setRefreshing(isUpdating);

    }
    //endregion
    //endregion

    //region API

    /**
     * Le pregunta al servidor si debe o no fichar y los horario de entrada y salida.
     */
    private void updateClockAction() {
        //Preparar interfaz
        isUpdatingClockAction = true;
        configureInterface();

        // Crear solicitud
        service.getClockAction(tokens.access.getHeader()).enqueue(new ProCallback<ClockResponse, ClockActionErrorResponse>() {
            @Override
            protected Class<ClockActionErrorResponse> getErrorClass() {
                return ClockActionErrorResponse.class;
            }

            @Override
            public void beforeResponse() {
                /* No hay inicialización */
            }

            @Override
            public void afterResponse() {
                // Configurar la interfaz según la respuesta
                isUpdatingClockAction = false;
                configureInterface();
            }

            @Override
            public void onOkResponse(@NonNull ClockResponse okBody) {
                Log.d("API", String.format("¡Se recibió la acción %s!", okBody.getActionString()));

                latestClockAction = okBody.getAction();
                startTime = okBody.getStartHour();
                exitTime = okBody.getExitHour();
            }

            @Override
            public void onErrorResponse(@NonNull ClockActionErrorResponse errorBody) {
                Log.e("API", String.format("ErrorResponse: %s", errorBody.getShortError()));
            }

            @Override
            public void onNullResponse() {
                Log.e("API", String.format("NullResponse: %s", getString(R.string.app_error_anyservice_response)));
                latestClockAction = ClockAction.ERROR_UNKNOWN;
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API", String.format("FailedResponse: %s", getString(R.string.app_error_anyservice_connection)));
                latestClockAction = ClockAction.ERROR_CONNECTION;
            }
        });
    }

    /**
     * Le manda al servidor una petición de fichaje y recibe el siguiente estado a estar:
     * Ejemplo: Ficho desde START. Estaré en WORK.
     */
    private void updateClockSend() {
        //Preparar interfaz
        isUpdatingClockAction = true;
        configureInterface();
        buttonClock.setEnabled(false);

        // Crear solicitud
        double latitude = latestLocation.getLatitude();
        double longitude = latestLocation.getLongitude();
        Log.d("API", String.format("Consiguiendo latitud: %f", latitude));
        Log.d("API", String.format("Consiguiendo longitud: %f", longitude));

        ClockSendRequest clockSendRequest = new ClockSendRequest(latitude, longitude);

        // Recibir respuesta
        service.sendClockRequest(tokens.access.getHeader(), clockSendRequest).enqueue(new ProCallback<ClockResponse, ClockSendErrorResponse>() {
            @Override
            protected Class<ClockSendErrorResponse> getErrorClass() {
                return ClockSendErrorResponse.class;
            }

            @Override
            public void beforeResponse() {
                // Ya se recibió la respuesta
                isUpdatingClockAction = false;
            }

            @Override
            public void afterResponse() {
                updateClockAction();
            }

            @Override
            public void onOkResponse(@NonNull ClockResponse okBody) {
                Log.d("API", String.format("¡Se recibió la acción %s!", okBody.getActionString()));

                latestClockAction = okBody.getAction();

                setFeedbackError("");
            }

            @Override
            public void onErrorResponse(@NonNull ClockSendErrorResponse errorBody) {
                Log.e("API", String.format("ErrorResponse: %s", errorBody.getShortError()));
                setFeedbackError(                        Messages.fromKey(
                        ClockActivity.this,
                        errorBody.getError(),
                        R.string.app_error_anyservice_unknownkey
                ));
            }

            @Override
            public void onNullResponse() {
                Log.e("API", String.format("NullResponse: %s", getString(R.string.app_error_anyservice_response)));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API", String.format("FailedResponse: %s", getString(R.string.app_error_anyservice_connection)));
            }
        });
    }
    //endregion

    //region Geolocalización
    private void updateLocation() {
        isUpdatingLocation = true;
        latestLocation = null;

        boolean doesntHasLocationPermissions = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (doesntHasLocationPermissions) {
            Log.e("LOCATION", "No se tienen permisos de ubicación.");
            Log.d("LOCATION", "Preguntando permisos de ubicación...");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            isUpdatingLocation = false;
            configureInterface();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        setFeedbackError(getString(R.string.clock_error_location_couldntfetch));
                        Log.e("LOCATION", "No se pudo leer la ubicación");
                    }
                    else {
                        Log.d("LOCATION", "¡Se consiguió la ubicación!");
                        Log.d("LOCATION", String.format("Latitud/Longitud %f/%f", location.getLatitude(), location.getLongitude() ));
                        setFeedbackError("");
                        latestLocation = location;
                    }
                    isUpdatingLocation = false;
                    configureInterface();
                });
    }

    //endregion

    //region Menú de tres puntos

    //region Inicialización
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Añade las opciones a la Toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Muestra los íconos
        if (menu instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Obtener el ID del elemento seleccionado
        int id = item.getItemId();
        boolean hasCalledFunction = true;

             if (id == R.id.mnu_incidence) onMenuIncidenceClick();
        else if (id == R.id.mnu_logout   ) onMenuLogoutClick();
        else if (id == R.id.mnu_history  ) onMenuHistoryClick();
        else if (id == R.id.mnu_config   ) onMenuConfigurationClick();
        else {
            Log.e("CLICK", "MenuItem sin implementar");
            hasCalledFunction = false;
        }

        return hasCalledFunction || super.onOptionsItemSelected(item);
    }
    //endregion

    //region OnClicks
    private void onMenuHistoryClick() {
        Log.d("CLICK", "Historial");
    }

    private void onMenuConfigurationClick() {
        Log.d("CLICK", "Configuración");
    }

    private void onMenuIncidenceClick() {
        Log.d("CLICK", "Crear incidencia");
    }

    private void onMenuLogoutClick() {
        Log.d("CLICK", "Cerrar sesión");

        // Definir popup
        AlertDialog.Builder builder = new AlertDialog.Builder(ClockActivity.this)
                .setMessage("¿Seguro que quieres cerrar la sesión?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) -> logout());


        // Mostrar popup
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

    //region Lógica

    /**
     * Regresa a la pantalla de inicio de sesión.
     */
    private void logout() {
        Intent intent = new Intent(ClockActivity.this, LoginActivity.class);
        intent.putExtra("CANCEL_AUTO_LOGIN", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
    //endregion
    //endregion

    private void debugCycleClockAction() {
        latestClockAction = latestClockAction.getNext();
        Log.d("DEBUG", String.format("Cambiando latestClockAction a: %s", latestClockAction.name()));
        toolbar.setTitle(latestClockAction.name());
        configureInterface();
    }
}