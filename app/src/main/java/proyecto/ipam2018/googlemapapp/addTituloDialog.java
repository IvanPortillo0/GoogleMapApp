package proyecto.ipam2018.googlemapapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ivan on 28/06/2018.
 */

public class addTituloDialog extends AppCompatDialogFragment {

    private EditText txtAddTitulo;
    private EditText txtAddDescripcion;
    private  AddTituloDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_marca_dialog,null);


        txtAddTitulo=(EditText) view.findViewById(R.id.txtAddTitulo);
        txtAddDescripcion=(EditText) view.findViewById(R.id.txtAddDescripcion);

        builder.setView(view)
                .setTitle("Agregar Marcador")
                .setIcon(R.drawable.addmarca48)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tit = txtAddTitulo.getText().toString();
                        String desp = txtAddDescripcion.getText().toString();
                        listener.aplicarTexto(tit,desp);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        try {
            listener = (AddTituloDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " Debe implementar DialogListenerAddTitulo");
        }
        super.onAttach(context);
    }

    public interface AddTituloDialogListener{
        void aplicarTexto(String titulo, String desp);
    }
}
