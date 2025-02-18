package bdisfer1410.controldepresencia.clockin;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bdisfer1410.controldepresencia.R;
import bdisfer1410.controldepresencia.login.LoginActivity;


public class MainActivity extends AppCompatActivity {
    // Variables
    //region Views
    private Button buttonClock;
    //endregion

    //region Datos
    private String token;
    //endregion

    // Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincular vistas a variables
        Toolbar toolbar = findViewById(R.id.toolbar);
        buttonClock = findViewById(R.id.btnClock);

        // Configurar la toolbar
        toolbar.setTitle(R.string.main_title);
        setSupportActionBar(toolbar);

        // Configurar acciones
        buttonClock.setOnClickListener(v -> {
            Log.d("CLICK", "Fichar");
        });

        // Cargar el token
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("TOKEN")) {
            token = intent.getStringExtra("TOKEN");
            Log.d("TOKEN", String.format("Recibido un token de sesión %s...", token.substring(0,3)));
        }
        else {
            Log.e("TOKEN", "No se recibió un token de sesión");
        }
    }

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

    // Acciones: Menú tres puntos
    private void onMenuHistoryClick() {
        Log.d("CLICK", "Historial");
    }

    private void onMenuConfigurationClick() {
        Log.d("CLICK", "Configuración");
    }

    private void onMenuLogoutClick() {
        Log.d("CLICK", "Cerrar sesión");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("CANCEL_AUTO_LOGIN", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}