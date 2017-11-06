package com.apps.juncode.pruebawham.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.juncode.pruebawham.BaseDatos.BaseDatos;
import com.apps.juncode.pruebawham.Model.User;
import com.apps.juncode.pruebawham.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView tv_perfil_name, tv_userName;
    private ImageView img_perfil_main;
    private FloatingActionButton fab_contact;
    private boolean imgExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        initialize();


        img_perfil_main = (ImageView) findViewById(R.id.img_perfil_main);
        tv_perfil_name = (TextView) findViewById(R.id.tv_perf_nombre);
        tv_userName = (TextView) findViewById(R.id.tv_user_name_ini);
        fab_contact = (FloatingActionButton) findViewById(R.id.fab_Contactos);

        File foto = new File(String.valueOf("/data/data/com.apps.juncode.pruebawham/app_Imagenes/Perfil.jpeg"));
        if(foto.exists()){

            Bitmap bit = BitmapFactory.decodeFile("/data/data/com.apps.juncode.pruebawham/app_Imagenes/Perfil.jpeg");
            img_perfil_main.setImageBitmap(bit);
            imgExist = true;

        }else{


        }

        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

    public void initialize() {

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                    datosAlToolBar();
                    Log.d(TAG, "Usuario Activo: " + firebaseUser.getEmail());



                } else {

                    Intent i = new Intent(MainActivity.this, InicioActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        };
    }

//    public void img_perfil_circulo(){
//        //extraemos el drawable en un bitmap
//
//        if(imgExist){
//
//            Drawable originalDrawable = img_perfil_main.getDrawable();
//            Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();
//
//            //creamos el drawable redondeado
//            RoundedBitmapDrawable roundedDrawable =
//                    RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
//
//            //asignamos el CornerRadius
//            roundedDrawable.setCornerRadius(originalBitmap.getHeight());
//
//            ImageView imageView = (ImageView) findViewById(R.id.img_perfil_main);
//
//            imageView.setImageDrawable(roundedDrawable);
//
//
//        }else{
//
//            Drawable originalDrawable = getResources().getDrawable(R.drawable.user_icon_2098873_960_720);
//            Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();
//
//            //creamos el drawable redondeado
//            RoundedBitmapDrawable roundedDrawable =
//                    RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
//
//            //asignamos el CornerRadius
//            roundedDrawable.setCornerRadius(originalBitmap.getHeight());
//
//            ImageView imageView = (ImageView) findViewById(R.id.img_perfil_main);
//
//            imageView.setImageDrawable(roundedDrawable);
//
//        }
//
//
//
//    }


    public void datosAlToolBar(){

        BaseDatos db = new BaseDatos(this);

        User u;

        u = db.obtenerUsuario();

        tv_perfil_name.setText(u.getNombre());
     // tv_userName.setText();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.mSingOut:

                firebaseAuth.signOut();

                File foto = new File("/data/data/com.apps.juncode.pruebawham/app_Imagenes/Perfil.jpeg");

                if(foto.exists()){
                    String deleteCmd = "rm -r " + "/data/data/com.apps.juncode.pruebawham/app_Imagenes/Perfil.jpeg";
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec(deleteCmd);
                    }catch (IOException e){
                        Log.d(TAG, "no se puede eliminar");
                    }
                }else{
                    Log.d(TAG, "no tiene imagen");
                }


                finish();

                break;
        }

        return  super.onOptionsItemSelected(item);
    }




    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}
