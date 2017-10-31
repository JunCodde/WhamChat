package com.apps.juncode.pruebawham.Adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.juncode.pruebawham.Model.User;
import com.apps.juncode.pruebawham.R;

import java.util.ArrayList;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class Contacto_adaptador extends RecyclerView.Adapter<Contacto_adaptador.ContactoViewHolder> {

    ArrayList<User> contactos;
    Activity activity;

    public Contacto_adaptador(ArrayList<User> contactos, Activity activity) {
        this.contactos = contactos;
        this.activity = activity;
    }

    @Override
    public ContactoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacto_rv, parent , false);
        return  new ContactoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactoViewHolder holder, int position) {

        final User user = contactos.get(position);

        holder.tv_nombre.setText(user.getNombre());
        holder.tv_activo.setText(user.getActivo());

    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }


    public static class ContactoViewHolder extends RecyclerView.ViewHolder{

        private CardView CV_contactos;
        private TextView tv_nombre;
        private TextView tv_activo;
        private ImageView img_Foto;

        public ContactoViewHolder(View itemView) {
            super(itemView);

            CV_contactos    = (CardView) itemView.findViewById(R.id.CV_contactos);
            tv_nombre       = (TextView) itemView.findViewById(R.id.tv_cv_NombreContacto);
            tv_activo       = (TextView) itemView.findViewById(R.id.tv_cv_ActivoContacto);
            img_Foto        = (ImageView) itemView.findViewById(R.id.img_fotoContacto);
        }
    }

}
