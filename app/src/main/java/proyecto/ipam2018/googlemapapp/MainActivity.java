package proyecto.ipam2018.googlemapapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap Mapa;

    //controladores
    EditText txtLatitud;
    EditText txtLongitud;
    private ImageButton btnOpciones;
    private Button btnMarca;
    private Button btnIrUes;
    private Button btnPosicion;
    private ImageButton btnBorrar;
    private Button btnIr;

    //Variables
    private static final int MI_PERMISO = 1;
    private double lat = 0.0;
    private double lng = 0.0;
    private ArrayList<String> latCirculos;
    private ArrayList<String>lngCirculos;
    private ArrayList<String>ColorCirculos;
    private ArrayList<String>TamanioCirculos;

    private Set<String> latCirculo;
    private Set<String> lngCirculo;
    private Set<String> ColorCirculo;
    private Set<String> TamanioCirculo;

    private boolean doit;
    private String tamanio;
    private String color;
    private String mapa;
    private String tamaniolst;
    private String colorlst;
    private String txtlat, txtlong;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Asignar id de controles
        btnOpciones = (ImageButton) findViewById(R.id.btnOpciones);
        btnMarca = (Button) findViewById(R.id.btnMarca);
        btnIrUes = (Button) findViewById(R.id.btnIrUes);
        btnPosicion = (Button) findViewById(R.id.btnPosicion);
        btnBorrar = (ImageButton) findViewById(R.id.btnBorrar);
        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        btnIr = (Button) findViewById(R.id.btnIr);

        preferences = getSharedPreferences("map_preferences",MODE_PRIVATE);

        latCirculos = new ArrayList<>();
        lngCirculos = new ArrayList<>();
        ColorCirculos = new ArrayList<>();
        TamanioCirculos = new ArrayList<>();

        latCirculo = new HashSet<String>();
        lngCirculo = new HashSet<String>();
        ColorCirculo = new HashSet<String>();
        TamanioCirculo = new HashSet<String>();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        latCirculo = preferences.getStringSet("latCirculo", null);
        lngCirculo = preferences.getStringSet("lngCirculo", null);
        ColorCirculo = preferences.getStringSet("ColorCirculo", null);
        TamanioCirculo = preferences.getStringSet("TamanioCirculo", null);

        mapa = preferences.getString("tipo","Normal");
        tamanio = preferences.getString("tamanio", "3000");
        color = preferences.getString("color", "Azul");

        lat = Double.parseDouble(preferences.getString("latitud", "13.970263"));
        lng = Double.parseDouble(preferences.getString("longitud", "-89.574808"));

        if(latCirculos!= null){txtLatitud.setText("latCirculo");}
        if(lngCirculos!= null){txtLongitud.setText("lngCirculo");}
       // if(ColorCirculos!= null){txtLatitud.setText("ColorCirculo");}
       // if(TamanioCirculos!= null){txtLongitud.setText("TamanioCirculo");}
        if (latCirculo != null && lngCirculo != null && ColorCirculo != null && TamanioCirculo != null){
            latCirculos = new ArrayList<String>(latCirculo);
            lngCirculos = new ArrayList<String>(lngCirculo);
            ColorCirculos = new ArrayList<String>(ColorCirculo);
            TamanioCirculos= new ArrayList<String>(TamanioCirculo);
        }
        Log.d("TIPO DE MAPA",mapa);

        //Botones
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpcionesMapa.class);
                startActivityForResult(intent, 1);
            }
        });

        btnIrUes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irUES();
            }
        });

        btnMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mapa.addMarker(new MarkerOptions().position(new LatLng(13.970263, -89.574808)).title("Santa Ana: UES"));
            }
        });

        btnPosicion = (Button) findViewById(R.id.btnPosicion);
        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicion();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                //txtLatitud.setText("");
                //txtLongitud.setText("");
                obtenerPosicion();
                guardar();

               // SharedPreferences.Editor editor = preferences.edit();
               // editor.clear();
               // preferences.edit().clear();
               // editor.commit();
            }
        });

        btnIr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ((txtLatitud.getText().toString().isEmpty() || txtLatitud.getText().toString() == null)
                        && (txtLongitud.getText().toString().isEmpty() || txtLongitud.getText().toString() == null)) {
                    lat = 0.0;
                    lng = 0.0;
                    irPosicion();
                } else {
                    lat = Double.parseDouble(txtLatitud.getText().toString());
                    lng = Double.parseDouble(txtLongitud.getText().toString());
                    irPosicion();
                }

            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MI_PERMISO);
    }

    //Metodo para el mapa --------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap){

        Mapa = googleMap;
        mapType();

        Mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                Toast.makeText(MainActivity.this, "Click\n" + "Lat: " + point.latitude + "\n" + "Lng: " + point.longitude + "\n" , Toast.LENGTH_SHORT).show();
               txtLatitud.setText(""+point.latitude);
               txtLongitud.setText(""+point.longitude);
            }
        });


        Mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                Toast.makeText(MainActivity.this, "Punto del Clic: " + point, Toast.LENGTH_LONG).show();
                addCirculosGuardados(point,true);
            }
        });

        if (latCirculos != null && lngCirculos != null && ColorCirculos != null && TamanioCirculos != null) {
            Log.d("COLOCARA PUNTOS","Deberia servir");
            int i = 0;
            int tam = latCirculos.size();
            while (i < tam){
                Log.d("LATITUD",latCirculos.get(i));
                Log.d("LONGITUD",lngCirculos.get(i));
                Log.d("LONGITUD",ColorCirculos.get(i));
                Log.d("LONGITUD",TamanioCirculos.get(i));
                colorlst=ColorCirculos.get(i);
                tamaniolst=TamanioCirculos.get(i);
                addCirculosGuardados(new LatLng(Double.parseDouble( latCirculos.get(i)), Double.parseDouble(lngCirculos.get(i))),false);
                i++;
            }
        }
        moveCamara();
    }

    //Metodos utilizados en OnCreate y en onMapReady------------------------------------------------------------------------------------------------------------------------------------
    private void mapType(){
        if (mapa.equals("Normal")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mapa.equals("Satelite")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mapa.equals("Terreno")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (mapa.equals("Hibrido")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        }
        Mapa.getUiSettings().setZoomControlsEnabled(true);
    }

    private void addCirculo(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        latCirculos.add(point.latitude + "");
        lngCirculos.add(point.longitude + "");

        if(color.equals("Blanco")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 0, 100}));
            Log.d("COLOR",color);
        }else if(color.equals("Cafe")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{18, 77, 50}));
            Log.d("COLOR",color);
        } else if(color.equals("Negro")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 0, 0}));
            Log.d("COLOR",color);
        } else if (color.equals("Amarillo")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{60, 100, 100}));
            Log.d("COLOR",color);
        } else if (color.equals("Naranja")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{47, 95, 100}));
            Log.d("COLOR",color);
        }else if (color.equals("Rojo")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 100, 100}));
            Log.d("COLOR",color);
        }else if (color.equals("Verde")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{120, 100, 100}));
            Log.d("COLOR",color);
        }else if (color.equals("Azul")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{240, 100, 100}));
            Log.d("COLOR",color);
        } else if (color.equals("Violeta")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{299, 55, 64}));
            Log.d("COLOR",color);
        }else if (color.equals("Celeste")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{180, 50, 100}));
            Log.d("COLOR",color);
        }
        circleOptions.radius(Integer.parseInt(tamanio));
        circleOptions.strokeWidth(1);
        Circle circulo = Mapa.addCircle(circleOptions);
    }

    public void addCirculosGuardados(LatLng point, Boolean AgregarCirculo){ // AgregarCirculo: cuando el metedo se recorra, saber si se usa para cargar datos guardados(false) o agregar nuevos datos (true)
        String LocalColor,LocalTamanio;
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);

        if(AgregarCirculo){
            LocalColor= color;
            LocalTamanio = tamanio;
        }else{
            LocalColor = colorlst;
            LocalTamanio = tamaniolst;
        }

        if(LocalColor.equals("Blanco")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 0, 100}));
            Log.d("COLOR",LocalColor);
        }else if(color.equals("Cafe")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{18, 77, 50}));
            Log.d("COLOR",LocalColor);
        } else if(LocalColor.equals("Negro")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 0, 0}));
            Log.d("COLOR",LocalColor);
        } else if (LocalColor.equals("Amarillo")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{60, 100, 100}));
            Log.d("COLOR",LocalColor);
        } else if (LocalColor.equals("Naranja")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{47, 95, 100}));
            Log.d("COLOR",LocalColor);
        }else if (LocalColor.equals("Rojo")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 100, 100}));
            Log.d("COLOR",LocalColor);
        }else if (LocalColor.equals("Verde")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{120, 100, 100}));
            Log.d("COLOR",LocalColor);
        }else if (LocalColor.equals("Azul")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{240, 100, 100}));
            Log.d("COLOR",LocalColor);
        } else if (LocalColor.equals("Violeta")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{299, 55, 64}));
            Log.d("COLOR",LocalColor);
        }else if (LocalColor.equals("Celeste")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{180, 50, 100}));
            Log.d("COLOR",LocalColor);
        }

        circleOptions.radius(Integer.parseInt(LocalTamanio));

        if(AgregarCirculo){
            latCirculos.add(point.latitude + "");
            lngCirculos.add(point.longitude + "");
            ColorCirculos.add(LocalColor);
            TamanioCirculos.add(LocalTamanio);
        }

        circleOptions.strokeWidth(1);
        Circle circulo = Mapa.addCircle(circleOptions);
    }

    public void guardar(){
        if (latCirculos.size() > 0){
            latCirculo = new HashSet<String>(latCirculos);
            lngCirculo = new HashSet<String>(lngCirculos);
            ColorCirculo = new HashSet<String>(ColorCirculos);
            TamanioCirculo = new HashSet<String>(TamanioCirculos);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("latCirculo", latCirculo);
        editor.putStringSet("lngCirculo", lngCirculo);
        editor.putStringSet("ColorCirculo", ColorCirculo);
        editor.putStringSet("TamanioCirculo", TamanioCirculo);
        editor.putString("latitud", lat+"");
        editor.putString("longitud", lng+"");
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mapa = data.getStringExtra("tipo");
                tamanio = data.getStringExtra("tamanio");
                color = data.getStringExtra("color");
                mapType();
            }
        }
    }

    //La App pide permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MI_PERMISO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Mapa.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy(){
        obtenerPosicion();
        guardar();
        super.onDestroy();
    }



    //Metodos de Botones---------------------------------------------------------------------------------------------------------------------------------

    private void irUES() {
        lat=13.970263;
        lng=-89.574808;
        CameraUpdate camUpd1 = CameraUpdateFactory .newLatLngZoom(new LatLng(lat, lng), 16);
        Mapa.moveCamera(camUpd1);
    }

    private void obtenerPosicion() {
        CameraPosition camPosicion = Mapa.getCameraPosition();
        LatLng coordenadas = camPosicion.target;
        lat = coordenadas.latitude;
        lng = coordenadas.longitude;
        Toast.makeText(this, "Lat: " + lat + " | Long: " + lng, Toast.LENGTH_SHORT).show();
    }

    private void irPosicion() {
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16);
        Mapa.moveCamera(camUpd1);
    }

    public void moveCamara(){
        Mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),16));
    }
}
