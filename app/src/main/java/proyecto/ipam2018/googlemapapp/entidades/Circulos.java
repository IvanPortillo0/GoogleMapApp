package proyecto.ipam2018.googlemapapp.entidades;

/**
 * Created by Ivan on 28/06/2018.
 */

public class Circulos {

    private double latitud;
    private double longitud;
    private String color;
    private String tamanio;

    public Circulos(double latitud, double longitud, String color, String tamanio){
        this.latitud = latitud;
        this.longitud = longitud;
        this.color = color;
        this.tamanio = tamanio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }
}
