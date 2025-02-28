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

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.api.ApiClient;
import bdisfer1410.controldepresencia.api.ApiService;
import bdisfer1410.controldepresencia.api.ProCallback;
import bdisfer1410.controldepresencia.api.clock.action.ClockActionErrorResponse;
import bdisfer1410.controldepresencia.api.clock.action.ClockActionResponse;
import bdisfer1410.controldepresencia.models.ClockAction;
import bdisfer1410.controldepresencia.models.Tokens;


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
    private SwipeRefreshLayout swipeRefreshLayout;
    //endregion

    //region Estado
    private ApiService service;
    private FusedLocationProviderClient fusedLocationClient;
    //endregion

    //region Datos
    private Tokens tokens;
    private ClockAction latestClockAction;
    private Location latestLocation;
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
                configureClockInterface();
                return;
            }
            else {
                Log.w("RESTORATION", "latestClockAction era null en savedInstanceState.");
            }
        }

        // Verificar acción de fichaje
        // TODO: MOVERLO A UN MÉTODO APARTE
        service = ApiClient.retrofit.create(ApiService.class);

        service.getClockAction(tokens.access.getHeader()).enqueue(new ProCallback<ClockActionResponse, ClockActionErrorResponse>() {
            @Override
            protected Class<ClockActionErrorResponse> getErrorClass() {
                return ClockActionErrorResponse.class;
            }

            @Override
            public void beforeResponse() {
                /* No hay preparación */
            }

            @Override
            public void afterResponse() {
                /* No hay finalización */
            }

            @Override
            public void onOkResponse(@NonNull ClockActionResponse okBody) {
                Log.d("API", String.format("¡Se recibió la acción %s!", okBody.getActionString()));

                latestClockAction = okBody.getAction();
                configureClockInterface();
            }

            @Override
            public void onErrorResponse(@NonNull ClockActionErrorResponse errorBody) {
                Log.e("API", String.format("ErrorResponse: %s", errorBody.getShortError()));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
    }

    private void configureViews() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("SWIPEREFRESH", "Actualizando ubicación y estado de fichaje");
            updateLocation();
        });

        toolbar.setTitle(R.string.clock_title);
        setSupportActionBar(toolbar);

        setFeedbackError("");

        buttonClock.setOnClickListener(v -> {
            Log.d("CLICK", "Fichar");
        });
    }
    //endregion

    //region Estado de fichaje
    private void configureClockInterface() {
        Log.d("GUI", String.format("Configurando la interfaz con: %s", latestClockAction.name()));

        feedbackWarning.setVisibility(GONE);
        buttonClock.setEnabled(latestClockAction.canClock());
        buttonClock.setText(latestClockAction.getButtonText());
        feedbackTitle.setText(latestClockAction.getFeedbackTitle());
        feedbackDescription.setText(latestClockAction.getFeedbackDescription());

        boolean canApplyNoLocationStyiling = latestLocation == null && latestClockAction.doesLocationMatter();

        if (canApplyNoLocationStyiling) {
            buttonClock.setEnabled(ClockAction.TOBEIN_WORK.canClock());
            feedbackWarning.setVisibility(VISIBLE);
        }

        swipeRefreshLayout.setRefreshing(false);
    }
    //endregion
    //endregion

    //region Geolocalización
    private void updateLocation() {
        latestLocation = null;

        boolean doesntHasLocationPermissions = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (doesntHasLocationPermissions) {
            Log.e("LOCATION", "No se tienen permisos de ubicación.");
            Log.d("LOCATION", "Preguntando permisos de ubicación...");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            configureClockInterface();
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

                    configureClockInterface();
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

             if (id == R.id.mnu_history) onMenuHistoryClick();
        else if (id == R.id.mnu_config ) onMenuConfigurationClick();
        else if (id == R.id.mnu_logout ) onMenuLogoutClick();
        else {
            Log.e("CLICK", "MenuItem sin implementar");
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region OnClicks
    private void onMenuHistoryClick() {
        Log.d("CLICK", "Historial");

        switch (latestClockAction) {
            case WAIT: latestClockAction = ClockAction.FREEDAY; break;
            case START: latestClockAction = ClockAction.WAIT; break;
            case WORK: latestClockAction = ClockAction.START; break;
            case EXIT: latestClockAction = ClockAction.WORK; break;
            case RECOVER: latestClockAction = ClockAction.EXIT; break;
            case NOTIFY_AUSENCE: latestClockAction = ClockAction.RECOVER; break;
            case TOBEIN_WORK: latestClockAction = ClockAction.NOTIFY_AUSENCE; break;
            case FREEDAY: latestClockAction = ClockAction.TOBEIN_WORK; break;
        }
        Log.d("ACTION", latestClockAction.name());

        buttonClock.setEnabled(latestClockAction.canClock());
        buttonClock.setText(latestClockAction.getButtonText());
    }

    private void onMenuConfigurationClick() {
        Log.d("CLICK", "Configuración");
        debugCycleClockAction(); // TODO: Quitarlo debug temporal
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
    }
    //endregion
    //endregion

    private void debugCycleClockAction() {
        latestClockAction = latestClockAction.getNext();
        Log.d("DEBUG", String.format("Cambiando latestClockAction a: %s", latestClockAction.name()));
        toolbar.setTitle(latestClockAction.name());
        configureClockInterface();
    }
}