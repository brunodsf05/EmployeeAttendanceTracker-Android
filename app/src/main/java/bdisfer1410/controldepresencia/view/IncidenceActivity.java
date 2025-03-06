package bdisfer1410.controldepresencia.view;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import bdisfer1410.controldepresencia.api.clock.ClockResponse;
import bdisfer1410.controldepresencia.api.clock.action.ClockActionErrorResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendErrorResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendRequest;
import bdisfer1410.controldepresencia.models.ClockAction;
import bdisfer1410.controldepresencia.models.Tokens;
import bdisfer1410.controldepresencia.tools.Hour;
import bdisfer1410.controldepresencia.tools.Messages;


public class IncidenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incidence);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}