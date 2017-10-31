package com.apps.juncode.pruebawham.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView tv_perfil_name, tv_userName;
    private FloatingActionButton fab_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        initialize();

        img_perfil_circulo();

        tv_perfil_name = (TextView) findViewById(R.id.tv_perf_nombre);
        tv_userName = (TextView) findViewById(R.id.tv_user_name_ini);
        fab_contact = (FloatingActionButton) findViewById(R.id.fab_Contactos);

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

    public void datosAlToolBar(){

        BaseDatos db = new BaseDatos(this);

        User u;

        u = db.obtenerUsuario();

        tv_perfil_name.setText(u.getNombre());
     // tv_userName.setText();


    }

    public void img_perfil_circulo(){
        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.user_icon_2098873_960_720);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        ImageView imageView = (ImageView) findViewById(R.id.img_perfil_main);

        imageView.setImageDrawable(roundedDrawable);

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
