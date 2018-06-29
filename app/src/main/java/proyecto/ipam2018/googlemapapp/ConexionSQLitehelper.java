package proyecto.ipam2018.googlemapapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ivan on 28/06/2018.
 */

public class ConexionSQLitehelper extends SQLiteOpenHelper {

    public ConexionSQLitehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase dbMap) {
        dbMap.execSQL(UtilidadesSQL.CREAR_TABLA_CIRCULOS);
        dbMap.execSQL(UtilidadesSQL.CREAR_TABLA_MARCAS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase dbMap, int versionAntigua, int versionNueva) {
        dbMap.execSQL("DROP TABLE IF EXISTS circulos");
        dbMap.execSQL("DROP TABLE IF EXISTS marcas");
        onCreate(dbMap);
    }
}
