package com.apps.juncode.pruebawham.Activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.juncode.pruebawham.BaseDatos.ConstructorDB;
import com.apps.juncode.pruebawham.Model.User;
import com.apps.juncode.pruebawham.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegActivity extends AppCompatActivity {

    private static final String TAG = "RegActivity";
    private EditText et_Reg_nombre, et_Reg_email, et_Reg_pass, et_Reg_pass2;
    private Button btn_crear;
    private Activity activity;

    private static final String USERS_NODE = "User";
    private DatabaseReference databaseReference;
    static boolean called = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        et_Reg_nombre       = (EditText) findViewById(R.id.et_Reg_nombre);
        et_Reg_email        = (EditText) findViewById(R.id.et_Reg_email);
        et_Reg_pass         = (EditText) findViewById(R.id.et_Reg_pass);
        et_Reg_pass2        = (EditText) findViewById(R.id.et_Reg_pass2);
        btn_crear           = (Button) findViewById(R.id.btn_reg_Reg);
        activity = this;

        if(!called){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called = true;
        }

        initialize();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        initialize();

        btn_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_Reg_nombre.getText().toString().isEmpty()){

                    Toast.makeText(RegActivity.this, "Nombre requerido", Toast.LENGTH_SHORT).show();


                }else if(et_Reg_email.getText().toString().isEmpty()){

                    Toast.makeText(RegActivity.this, "Email requerido", Toast.LENGTH_SHORT).show();

                }else if(et_Reg_pass.getText().toString().isEmpty()){

                    Toast.makeText(RegActivity.this, "Contraseña requerido", Toast.LENGTH_SHORT).show();

                }else if(et_Reg_pass2.getText().toString().isEmpty()){

                    Toast.makeText(RegActivity.this, "Repita su contraseña", Toast.LENGTH_SHORT).show();

                }else if(et_Reg_pass.getText().toString().contentEquals(et_Reg_pass2.getText().toString()) ){

                    if(et_Reg_pass.getText().toString().length() < 6){

                        Toast.makeText(RegActivity.this, "La contraseña debe ser mayor o igual que 6", Toast.LENGTH_SHORT).show();

                    }else{
                        if(isNetDisponible()){

                                User user = new User( "token", "uid", et_Reg_nombre.getText().toString(), et_Reg_email.getText().toString(), "Foto", "false");
                                String clave = et_Reg_pass.getText().toString();
                                crearCuenta(user, clave);

                        }else{
                            Toast.makeText(RegActivity.this, "Revisa tu conexion a internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(RegActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void crearCuenta(final User user, String clave){

        Log.d(TAG, "Nombre: " + user.getNombre());
        Log.d(TAG, "Correo: " + user.getCorreo());
        Log.d(TAG, "Token: " + user.getToken());
        Log.d(TAG, "Genero: " + user.getActivo());


        firebaseAuth.createUserWithEmailAndPassword(user.getCorreo(), clave).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String token = FirebaseInstanceId.getInstance().getToken();
                    user.setUID(firebaseUser.getUid());
                    user.setToken(token);
                    Log.d(TAG, "Token: " + user.getToken());


                    guardarRealTime(user);
                    guardarDB(user);

                    Toast.makeText(RegActivity.this, "Cuenta creada, inicia sesion", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(RegActivity.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void guardarDB(User u){

        ConstructorDB constructorDB = new ConstructorDB(activity);
        constructorDB.insertarUsuario(u);

    }

    public void guardarRealTime(User user){

        databaseReference.child(USERS_NODE).child(user.getUID()).setValue(user);
    }

    public void initialize() {

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                    Log.d(TAG, "Usuario Activo: " + firebaseUser.getEmail());

                } else {

                    Log.d(TAG, "Usuario Inactivo");

                }
            }
        };
    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
