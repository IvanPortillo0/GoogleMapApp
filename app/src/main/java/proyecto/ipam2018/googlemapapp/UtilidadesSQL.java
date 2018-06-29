package proyecto.ipam2018.googlemapapp;

/**
 * Created by Ivan on 28/06/2018.
 */

public class UtilidadesSQL {

    //Constantes de campos tabla circulos
    public static final String TABLA_CIRCULOS = "circulos";
    public static final String CAMPO_LAT="latitud";
    public static final String CAMPO_LONG = "longitud";
    public static final String CAMPO_COLOR = "color";
    public static final String CAMPO_TAMANIO = "tamanio";

    //constantes de tabla marcas
    public static final String TABLA_MARCAS = "marcas";
    public static final String CAMPO_MAR_LAT="latitud";
    public static final String CAMPO_MAR_LONG = "longitud";
    public static final String CAMPO_MAR_TITULO="titulo";
    public static final String CAMPO_MAR_DESPCRIPCION="descripcion";

    public static final String CREAR_TABLA_CIRCULOS= "CREATE TABLE "+TABLA_CIRCULOS+
            " ("+CAMPO_LAT+" DOUBLE, "+CAMPO_LONG+" DOUBLE, "+CAMPO_COLOR+" TEXT, "+CAMPO_TAMANIO+" TEXT)";

    public static final String CREAR_TABLA_MARCAS= "CREATE TABLE "+TABLA_MARCAS+
            " ("+CAMPO_LAT+" DOUBLE, "+CAMPO_LONG+" DOUBLE, "+CAMPO_MAR_TITULO+" TEXT, "+CAMPO_MAR_DESPCRIPCION+" TEXT)";
}
