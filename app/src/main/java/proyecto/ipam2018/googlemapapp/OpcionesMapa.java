package proyecto.ipam2018.googlemapapp;

/**
 * Created by Ivan on 27/06/2018.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class OpcionesMapa extends AppCompatActivity {

    private Spinner spMapa;
    private Spinner spColor;
    private EditText txtTamanio;
    private Button btnGuardar;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_mapa);
        preferences = getSharedPreferences("map_preferences",MODE_PRIVATE);

        spMapa = (Spinner)findViewById(R.id.spTipoMapa);
        spColor = (Spinner)findViewById(R.id.spColorForma);
        txtTamanio = (EditText) findViewById(R.id.txtTamaño);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        if (preferences.getString("tipo","").equals("Normal")){
            spMapa.setSelection(0);
        } else if (preferences.getString("tipo","").equals("Satelital")){
            spMapa.setSelection(1);
        } else if (preferences.getString("tipo","").equals("Terreno")){
            spMapa.setSelection(2);
        } else if (preferences.getString("tipo","").equals("Hibrido")){
            spMapa.setSelection(3);
        }

        if (preferences.getString("color","").equals("Blanco")){
            spColor.setSelection(0);
        }else if (preferences.getString("color","").equals("Cafe")){
            spColor.setSelection(1);
        }else if (preferences.getString("color","").equals("Negro")){
            spColor.setSelection(2);
        }else if (preferences.getString("color","").equals("Amarillo")){
            spColor.setSelection(3);
        }else if (preferences.getString("color","").equals("Naranja")){
            spColor.setSelection(4);
        } else if (preferences.getString("color","").equals("Rojo")){
            spColor.setSelection(5);
        } else if (preferences.getString("color","").equals("Verde")){
            spColor.setSelection(6);
        } else if (preferences.getString("color","").equals("Azul")){
            spColor.setSelection(7);
        }else if (preferences.getString("color","").equals("Violeta")){
            spColor.setSelection(8);
        }else if (preferences.getString("color","").equals("Celeste")){
            spColor.setSelection(9);
        }

        txtTamanio.setText(preferences.getString("tamanio","3000"));

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mapa = spMapa.getSelectedItem().toString();
                String color = spColor.getSelectedItem().toString();
                String tamanio = txtTamanio.getText().toString();

                if ((tamanio != "") && (mapa != "") && (color != "") && (tamanio != null) && (mapa != null) && (color != null)){
                    Intent intent = new Intent();
                    intent.putExtra("tipo", mapa);
                    intent.putExtra("color",color);
                    intent.putExtra("tamanio",tamanio);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(OpcionesMapa.this,"Debe de llenar los campos requeridos", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("tipo", mapa);
                editor.putString("color", color);
                editor.putString("tamanio", tamanio);
                editor.commit();
                Intent intent = new Intent(OpcionesMapa.this, MainActivity.class);
            }
        });
    }
}
