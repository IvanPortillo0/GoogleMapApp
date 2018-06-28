package proyecto.ipam2018.googlemapapp.entidades;

/**
 * Created by Ivan on 28/06/2018.
 */

public class Marcas {

    private double Latitud;
    private double Longitud;
    private String Titulo;

    public Marcas(double Latitud, double Longitud, String Titulo){
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.Titulo = Titulo;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }
}
