package proyecto.ipam2018.googlemapapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.Preference;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, addTituloDialog.AddTituloDialogListener{

    private GoogleMap Mapa;

    //controladores
    EditText txtLatitud;
    EditText txtLongitud;
    private ImageButton btnOpciones;
    private ImageButton btnMarca;
    private ImageButton btnIrUes;
    private ImageButton btnPosicion;
    private ImageButton btnBorrarMarca;
    private ImageButton btnBorrarCirculo;
    private ImageButton btnLimpiar;
    private Button btnIr;

    //Variables
    private static final int MI_PERMISO = 1;
    private double lat = 0.0;
    private double lng = 0.0;

    private ArrayList<String> latCirculos;
    private ArrayList<String>lngCirculos;
    private ArrayList<String>ColorCirculos;
    private ArrayList<String>TamanioCirculos;

    private ArrayList<String> latMarcas;
    private ArrayList<String>lngMarcas;
    private ArrayList<String>TituloMarcas;
    private ArrayList<String>DespMarcas;

    private String tamanio;
    private String color;
    private String mapatipo;
    private float zoom;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Asignar id de controles
        btnOpciones = (ImageButton) findViewById(R.id.btnOpciones);
        btnMarca = (ImageButton) findViewById(R.id.btnMarca);
        btnIrUes = (ImageButton) findViewById(R.id.btnIrUes);
        btnPosicion = (ImageButton) findViewById(R.id.btnPosicion);
        btnBorrarMarca = (ImageButton) findViewById(R.id.btnBorrarMarca);
        btnBorrarCirculo = (ImageButton) findViewById(R.id.btnBorrarCirculo);
        btnLimpiar = (ImageButton) findViewById(R.id.btnLimpiar);
        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        btnIr = (Button) findViewById(R.id.btnIr);

        preferences = getSharedPreferences("map_preferences", MODE_PRIVATE);

        latCirculos = new ArrayList<>();
        lngCirculos = new ArrayList<>();
        ColorCirculos = new ArrayList<>();
        TamanioCirculos = new ArrayList<>();

        latMarcas = new ArrayList<>();
        lngMarcas = new ArrayList<>();
        TituloMarcas = new ArrayList<>();
        DespMarcas = new ArrayList<>();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Cargar Datos
        mapatipo = preferences.getString("tipo","Normal");
        tamanio = preferences.getString("tamanio", "2000");
        color = preferences.getString("color", "Blanco");

        zoom = Float.parseFloat(preferences.getString("zoom","16.0"));
        lat = Double.parseDouble(preferences.getString("latitud", "13.970263"));
        lng = Double.parseDouble(preferences.getString("longitud", "-89.574808"));

        txtLatitud.setText(preferences.getString("textLatitud",""));
        txtLongitud.setText(preferences.getString("textLongitud",""));

        //Botones////////////////////////////////////////////////////
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpcionesMapa.class);
                startActivityForResult(intent, 1);
            }
        });

        btnMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mapa.addMarker(new MarkerOptions().position(new LatLng(13.970263, -89.574808)).title("Santa Ana: UES"));
                if ((txtLatitud.getText().toString().isEmpty() || txtLatitud.getText().toString() == null)
                        && (txtLongitud.getText().toString().isEmpty() || txtLongitud.getText().toString() == null)) {

                    Toast.makeText(MainActivity.this, "Campos de texto vacios", Toast.LENGTH_LONG).show();

                }else{
                    addTituloDialog tituloDialog = new addTituloDialog();
                    tituloDialog.show(getSupportFragmentManager(),"Añadir Titulo");
                }
            }
        });

        btnIrUes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irUES();
            }
        });

        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicion();
            }
        });

        btnBorrarMarca.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                borrarMarca();
            }
        });

        btnBorrarCirculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                borrarCircle();
            }
        });


        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                txtLatitud.setText("");
                txtLongitud.setText("");
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

    //Sirve para traer el dato(titulo) de la ventana emergente de agregar marca, (este metodo pertenece a addTituloDialog.java)
    //Y añade las nuevas marcas a la BD
    @Override
    public void aplicarTexto(String titulo, String Desp) {
        addMarca(new LatLng(Double.parseDouble(txtLatitud.getText().toString()), Double.parseDouble(txtLongitud.getText().toString())),titulo, Desp,true);
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
                addCirculo(point, color, tamanio,true);

            }
        });

        //Cargar circulos y marcas de la base de datos en el mapa
        CargarCirculos();
        CargarMarcas();

        //mover camara a posicion guardada en el preference
        moveCamara();
    }

    //Metodos utilizados propios para la App (onCreate, OnPause, OnMapReady)/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mapType(){
        if (mapatipo.equals("Normal")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mapatipo.equals("Satelite")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mapatipo.equals("Terreno")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (mapatipo.equals("Hibrido")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        }
        Mapa.getUiSettings().setZoomControlsEnabled(true);

    }

    public void addCirculo(LatLng point, String LocalColor, String LocalTamanio, Boolean AgregarCirculo){ // AgregarCirculo: cuando el metedo se recorra, saber si se usa para cargar datos guardados(false) o agregar nuevos datos (true)

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);


        if(LocalColor.equals("Blanco")){
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{0, 0, 100}));
            Log.d("COLOR",LocalColor);
        }else if(LocalColor.equals("Negro")){
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

        if(AgregarCirculo){//Se añade a la BD si es agrega un marcador y no cuando cargue la app
            ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
            SQLiteDatabase db = conx.getWritableDatabase();

            ContentValues values=new ContentValues();
            values.put(UtilidadesSQL.CAMPO_LAT,point.latitude);
            values.put(UtilidadesSQL.CAMPO_LONG,point.longitude);
            values.put(UtilidadesSQL.CAMPO_COLOR,LocalColor);
            values.put(UtilidadesSQL.CAMPO_TAMANIO,LocalTamanio);

            Long resultante = db.insert(UtilidadesSQL.TABLA_CIRCULOS,null,values);
            Toast.makeText(getApplicationContext(),"N° Registro: "+resultante,Toast.LENGTH_SHORT).show();
            db.close();
        }

        circleOptions.strokeWidth(1);
        Mapa.addCircle(circleOptions);
    }

    public void addMarca(LatLng point, String LocalTitulo,String LocalDesp, Boolean AgregarMarca){

        if(AgregarMarca){//Se añade a la BD si es agrega un marcador y no cuando cargue la app
            ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
            SQLiteDatabase db = conx.getWritableDatabase();

            ContentValues values=new ContentValues();
            values.put(UtilidadesSQL.CAMPO_MAR_LAT,point.latitude);
            values.put(UtilidadesSQL.CAMPO_MAR_LONG,point.longitude);
            values.put(UtilidadesSQL.CAMPO_MAR_TITULO,LocalTitulo);
            values.put(UtilidadesSQL.CAMPO_MAR_DESPCRIPCION,LocalDesp);

            Long resultante = db.insert(UtilidadesSQL.TABLA_MARCAS,null,values);
            Toast.makeText(getApplicationContext(),"N° Registro: "+resultante,Toast.LENGTH_SHORT).show();
            db.close();
        }


        Mapa.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(LocalTitulo).snippet(LocalDesp));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void guardar(){

        CameraPosition camPosicion = Mapa.getCameraPosition();
        LatLng coordenadas = camPosicion.target;
        lat = coordenadas.latitude;
        lng = coordenadas.longitude;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("latitud", lat+"");
        editor.putString("longitud", lng+"");
        editor.putString("textLatitud", txtLatitud.getText().toString());
        editor.putString("textLongitud", txtLongitud.getText().toString());
        editor.putString("zoom", camPosicion.zoom+"");
        editor.commit();
    }

    public void CargarCirculos(){
        //Este Fragmento es para añadir los circulos a las listas
        ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
        SQLiteDatabase db = conx.getWritableDatabase();
        Cursor myCursor;
        myCursor = db.rawQuery("SELECT * FROM circulos", null);
        while(myCursor.moveToNext()) {
            latCirculos.add(""+myCursor.getDouble(0));
            lngCirculos.add(""+myCursor.getDouble(1));
            ColorCirculos.add(myCursor.getString(2));
            TamanioCirculos.add(myCursor.getString(3));
        }
        myCursor.close();

        //añadir Circulos al mapa
        if (latCirculos != null && lngCirculos != null && ColorCirculos != null && TamanioCirculos != null) {
            Log.d("COLOCARA PUNTOS","Deberia servir");

            for (int i = 0; i < latCirculos.size(); i++) {
                Log.d("LATITUD", latCirculos.get(i));
                Log.d("LONGITUD", lngCirculos.get(i));
                Log.d("COLOR", ColorCirculos.get(i));
                Log.d("TAMANIO", TamanioCirculos.get(i));
                addCirculo(new LatLng(Double.parseDouble(latCirculos.get(i)), Double.parseDouble(lngCirculos.get(i))), ColorCirculos.get(i), TamanioCirculos.get(i), false);
            }
        }
        db.close();
    }

    public void CargarMarcas(){
        //Este Fragmento es para añadir las marcas a las listas
        ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
        SQLiteDatabase db = conx.getWritableDatabase();
        Cursor myCursor;
        myCursor = db.rawQuery("SELECT * FROM marcas", null);
        while(myCursor.moveToNext()) {
            latMarcas.add(""+myCursor.getDouble(0));
            lngMarcas.add(""+myCursor.getDouble(1));
            TituloMarcas.add(myCursor.getString(2));
            DespMarcas.add(myCursor.getString(3));
        }
        myCursor.close();

        //añadir Marcas al mapa
        if (latMarcas != null && lngMarcas != null && TituloMarcas != null && DespMarcas != null) {
            Log.d("COLOCARA Marcas","Si entro a if");

            for (int i = 0; i < latMarcas.size(); i++) {
                Log.d("LATITUD", latMarcas.get(i));
                Log.d("LONGITUD", lngMarcas.get(i));
                Log.d("TITULO", TituloMarcas.get(i));
                Log.d("DESCRIPCION", DespMarcas.get(i));
                addMarca(new LatLng(Double.parseDouble(latMarcas.get(i)), Double.parseDouble(lngMarcas.get(i))), TituloMarcas.get(i),DespMarcas.get(i), false);
            }
        }
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mapatipo = data.getStringExtra("tipo");
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
    protected void onPause() {
        guardar();

        super.onPause();
    }

    //Metodos de Botones---------------------------------------------------------------------------------------------------------------------------------

    private void irUES() {
        lat=13.970263;
        lng=-89.574808;
        txtLatitud.setText("13.970263");
        txtLongitud.setText("-89.574808");
        CameraUpdate camUpd1 = CameraUpdateFactory .newLatLngZoom(new LatLng(lat, lng), 16);
        Mapa.moveCamera(camUpd1);
    }

    private void obtenerPosicion() {
        CameraPosition camPosicion = Mapa.getCameraPosition();
        LatLng coordenadas = camPosicion.target;
        lat = coordenadas.latitude;
        lng = coordenadas.longitude;

        txtLatitud.setText("" + lat);
        txtLongitud.setText("" + lng);
        Toast.makeText(this, "Lat: " + lat + " | Long: " + lng, Toast.LENGTH_SHORT).show();
    }

    private void irPosicion() {
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16);
        Mapa.moveCamera(camUpd1);
    }

    public void moveCamara(){
        Mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), zoom));
    }

    public void borrarCircle(){
        ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
        SQLiteDatabase db = conx.getWritableDatabase();

        String Delete="DELETE FROM circulos;";

        db.execSQL(Delete);
        db.close();
        Toast.makeText(MainActivity.this, "Circulos Eliminados", Toast.LENGTH_SHORT).show();
        Mapa.clear();
        latCirculos.clear();
        lngCirculos.clear();
        ColorCirculos.clear();
        TamanioCirculos.clear();
        CargarMarcas();
    }

    public void borrarMarca(){
        ConexionSQLitehelper conx = new ConexionSQLitehelper(this,"bd_Mapa",null,1);
        SQLiteDatabase db = conx.getWritableDatabase();

        String Delete="DELETE FROM marcas;";

        db.execSQL(Delete);
        db.close();
        Toast.makeText(MainActivity.this, "Marcas Eliminadas", Toast.LENGTH_SHORT).show();
        Mapa.clear();
        latMarcas.clear();
        lngMarcas.clear();
        TituloMarcas.clear();
        DespMarcas.clear();

        CargarCirculos();
    }

}
