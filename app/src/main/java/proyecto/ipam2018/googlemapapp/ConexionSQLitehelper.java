package proyecto.ipam2018.googlemapapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ivan on 28/06/2018.
 */

public class ConexionSQLitehelper extends SQLiteOpenHelper {

    final String CREAR_TABLA_CIRCULOS="CREATE TABLE circulos (latitud DOUBLE, longitud DOUBLE, color TEXT, tamanio TEXt )";

    public ConexionSQLitehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase dbMap) {
        dbMap.execSQL(CREAR_TABLA_CIRCULOS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase dbMap, int versionAntigua, int versionNueva) {
        dbMap.execSQL("DROP TABLE IF EXISTS circulos");
        onCreate(dbMap);
    }
}
