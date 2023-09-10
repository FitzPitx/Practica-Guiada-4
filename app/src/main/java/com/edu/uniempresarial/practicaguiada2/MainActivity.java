package com.edu.uniempresarial.practicaguiada2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.TotalCaptureResult;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;

    //Version android
    private TextView versionAndroid;
    private int versionSDK;

    //Bateria
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
    IntentFilter batteryFilter;

    //Camera
    private CameraManager cameraManager;
    private String cameraId;
    private Button btnOnLight;
    private Button btnOffLight;

    //Archivo
    private EditText etNameFile;
    private Archivo archivo;
    private ImageButton btnCreateFile;

    //Conexión

    private TextView tvConnection;
    private ConnectivityManager connectivityManager;

    //Bluetooth
    private ImageView ivBluetooth;
    private TextView tvBluetoothState;
    private Button btnOnBluetooth;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //registerReceiver(); //pendiente
        btnOnLight.setOnClickListener(this::onLight);
        btnOffLight.setOnClickListener(this::onOffLight);

        //Battery
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryFilter);

        //file
        archivo = new Archivo(context, activity);
        btnCreateFile.setOnClickListener(this::onSaveFile);
        //Bluetooth
        btnOnBluetooth.setOnClickListener(this::onBluetooth);
    }




    //Version android

    @Override
    protected void onResume() {
        super.onResume();
        String version = android.os.Build.VERSION.RELEASE;
        versionSDK = android.os.Build.VERSION.SDK_INT;
        versionAndroid.setText("Versión SO: " + version + "  /SDK: " + versionSDK);
        checkConnection();
    }

    //flashlight
    private void onLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onOffLight(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Battery //BroadcastReceiver.
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); //-1 si no hay bateria
            pbLevelBattery.setProgress(levelBattery);
            tvLevelBattery.setText("Nivel de la bateria: " + levelBattery + "%");
        }
    };

    //Connection
    private void checkConnection() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        boolean stateNet = network != null && network.isConnectedOrConnecting();
        if (stateNet) {
            tvConnection.setText("State ON: " + network.getTypeName());
        } else {
            tvConnection.setText("State OFF");
        }

    }

    //Archivo
    private void onSaveFile(View view){
        String nameFile = etNameFile.getText().toString();
        if(nameFile.isEmpty()){
            Toast.makeText(context, "Por favor ingrese un nombre para guardar el archivo", Toast.LENGTH_LONG).show();
        } else {
            archivo.saveFile(nameFile, " ");
        }
    }


    //Bluetooth
    public void onBluetooth(View view) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT>=31){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT>= 31){
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
            ivBluetooth.setBackgroundResource(R.drawable.bluetoothoff);
            btnOnBluetooth.setText("Encender");
            tvBluetoothState.setText("Bluetooth esta apagado");

        } else {
            bluetoothAdapter.enable();
            ivBluetooth.setBackgroundResource(R.drawable.bluetooth);
            btnOnBluetooth.setText("Apagar");
            tvBluetoothState.setText("Bluetooth esta activado");
        }
    }

    public void init() {
        this.context = getApplicationContext();
        this.activity = MainActivity.this;
        this.versionAndroid = findViewById(R.id.tvVersionAndroid);
        //Battery
        this.pbLevelBattery = findViewById(R.id.pbLevelBattery);
        this.tvLevelBattery = findViewById(R.id.tvLevelBattery);
        //Connection
        this.tvConnection = findViewById(R.id.tvConnection);
        //Light
        this.btnOffLight = findViewById(R.id.btnOff);
        this.btnOnLight = findViewById(R.id.btnOn);
        //File
        this.etNameFile = findViewById(R.id.etNameFile);
        this.btnCreateFile = findViewById(R.id.btnSaveFile);
        //Bluetooth
        this.ivBluetooth = findViewById(R.id.ivBluetooth);
        this.tvBluetoothState = findViewById(R.id.tvBluetoothState);
        this.btnOnBluetooth = findViewById(R.id.btnOnBluetooth);
    }

}