package bdisfer1410.controldepresencia;


import android.annotation.SuppressLint;
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


public class MainActivity extends AppCompatActivity {
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
        Button buttonClock = findViewById(R.id.btnClock);

        // Configurar la toolbar
        toolbar.setTitle(R.string.main_title);
        setSupportActionBar(toolbar);

        // Configurar acciones
        buttonClock.setOnClickListener(v -> {
            Log.d("CLICK", "Fichar");
        });
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

        if (id == R.id.mnu_history) {
            Log.d("CLICK", "Historial");
        }
        else if (id == R.id.mnu_config) {
            Log.d("CLICK", "Configuración");
        }
        else if (id == R.id.mnu_logout) {
            Log.d("CLICK", "Cerrar sesión");
        }
        else {
            Log.e("CLICK", "MenuItem sin implementar");
        }

        return super.onOptionsItemSelected(item);
    }
}