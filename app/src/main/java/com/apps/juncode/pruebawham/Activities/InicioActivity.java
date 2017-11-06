package com.apps.juncode.pruebawham.Activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.juncode.pruebawham.BaseDatos.ConstructorDB;
import com.apps.juncode.pruebawham.Model.User;
import com.apps.juncode.pruebawham.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InicioActivity extends AppCompatActivity {

    private static final String TAG = "IniciarActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static final String USERS_NODE = "User";
    private static final String CONTACT_NODE = "Contact";
    private DatabaseReference databaseReference;

    private StorageReference storageReference;

    TextView tv_create;
    EditText et_userName, et_pass;
    Button btn_iniciar;
    ProgressBar progressBar;
    Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        getSupportActionBar().hide();
        activity = this;

        initialize();

        tv_create       = (TextView)    findViewById(R.id.tv_ini_reg);
        et_userName     = (EditText)    findViewById(R.id.et_ini_userName);
        et_pass         = (EditText)    findViewById(R.id.et_ini_pass);
        btn_iniciar     = (Button)      findViewById(R.id.btn_ini_Iniciar);
        progressBar     = (ProgressBar) findViewById(R.id.ProgressBarIniciar);

        storageReference = FirebaseStorage.getInstance().getReference();

        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_userName.getText().toString().isEmpty()){
                    Toast.makeText(InicioActivity.this, "Introduce tu correo electronico", Toast.LENGTH_SHORT).show();
                }else if(et_pass.getText().toString().isEmpty()){
                    Toast.makeText(InicioActivity.this, "Introduce tu contraseña", Toast.LENGTH_SHORT).show();
                }else{

                    String user = et_userName.getText().toString();
                    String pass = et_pass.getText().toString();

                    if(isNetDisponible()){

                        SingIn(user, pass);
                        progressBar.setVisibility(View.VISIBLE);
                        btn_iniciar.setVisibility(View.GONE);

                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputMethodManager.hideSoftInputFromWindow(et_userName.getWindowToken(), 0);
                        inputMethodManager.hideSoftInputFromWindow(et_pass.getWindowToken(), 0);


                    }else{
                        Toast.makeText(InicioActivity.this, "Revisa tu conexion a internet", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InicioActivity.this, RegActivity.class);
                startActivity(i);
            }
        });

    }

    private void SingIn(String user, String pass){

        firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    databaseReference = FirebaseDatabase.getInstance().getReference();

                    String token = FirebaseInstanceId.getInstance().getToken();

                    databaseReference.child(USERS_NODE).child(user.getUid()).child("token").setValue(token);


                    databaseReference.child(USERS_NODE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User u = dataSnapshot.getValue(User.class);
                            u.setActivo("false");
                            descargarFotoPerfil(u);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(InicioActivity.this, "Algo salio mal!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btn_iniciar.setVisibility(View.VISIBLE);
                        }
                    });

                }else{
                    Toast.makeText(InicioActivity.this, "Correo o Contraseña Incorrectos :(", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_iniciar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void descargarFotoPerfil(final User u){

        final File file;
        try {

            file = File.createTempFile("Perfil", "jpg");
            storageReference.child(u.getUID() + ".jpeg").getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            guardarImg(InicioActivity.this, bitmap);

                            checkFoto(u);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "La imagen no existe");
                    e.printStackTrace();

                    actContactos(u);
                    guardarDB(u);
                    checkBD();
                }
            });


        }catch (Exception e){
            Log.e(TAG, "La descarga se fue ALV");
            e.printStackTrace();
        }


    }

    public void checkFoto(final User u){

        File foto = new File(String.valueOf("/data/data/com.apps.juncode.pruebawham/app_Imagenes/Perfil.jpeg"));
        if(foto.exists()){

            actContactos(u);
            guardarDB(u);
            checkBD();

        }else{

            checkFoto(u);

        }
    }

    private String guardarImg(Context context, Bitmap imagen){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Imagenes", Context.MODE_PRIVATE);

        File myPath = new File(dirImages, "Perfil.jpeg");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            imagen.compress(Bitmap.CompressFormat.JPEG, 40, fos);
            fos.flush();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return myPath.getAbsolutePath();


    }

    public void actContactos(User u){

        final ConstructorDB constructorDB = new ConstructorDB(activity);
        constructorDB.BorrarContactos();


        databaseReference.child(CONTACT_NODE).child(u.getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Log.d(TAG, String.valueOf(dataSnapshot.getChildrenCount()));

                    if(dataSnapshot.getChildrenCount() == 0){

                        Log.d(TAG, "no hay contactos");

                    }else {

                        for (int i = 1; i <= dataSnapshot.getChildrenCount(); i++) {

                            User cCurrent = dataSnapshot.child(String.valueOf(i)).getValue(User.class);
                            constructorDB.insertarContacto(cCurrent);

                        }

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void guardarDB(User u) {

        ConstructorDB constructorDB = new ConstructorDB(activity);
        constructorDB.insertarUsuario(u);



    }

    private void checkBD(){

        ConstructorDB constructorDB = new ConstructorDB(getApplicationContext());
        if(constructorDB.checkIfUserExist()){

            Intent i = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            Log.d(TAG, String.valueOf(constructorDB.checkIfUserExist()));

        }else {
            checkBD();
        }

    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
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
