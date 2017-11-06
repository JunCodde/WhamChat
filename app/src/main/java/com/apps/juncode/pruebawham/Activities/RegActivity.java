package com.apps.juncode.pruebawham.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class RegActivity extends AppCompatActivity {

    private static final String TAG = "RegActivity";
    private EditText et_Reg_nombre, et_Reg_email, et_Reg_pass, et_Reg_pass2;
    private ImageView img_contacto;
    private Button btn_crear;
    private Activity activity;
    private ProgressBar progressBarRegistro;

    private final int CODIGO_FOTO = 100;
    private final int SELECT_FOTO = 200;
    private String APP_DIRECTORY = "fotoUser/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private Bitmap fotoFinal = null;
    private boolean fotolista = false;

    private static final String USERS_NODE = "User";
    private DatabaseReference databaseReference;
    static boolean called = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        et_Reg_nombre       = (EditText) findViewById(R.id.et_Reg_nombre);
        et_Reg_email        = (EditText) findViewById(R.id.et_Reg_email);
        et_Reg_pass         = (EditText) findViewById(R.id.et_Reg_pass);
        et_Reg_pass2        = (EditText) findViewById(R.id.et_Reg_pass2);
        img_contacto        = (ImageView) findViewById(R.id.img_contacto);
        btn_crear           = (Button) findViewById(R.id.btn_reg_Reg);
        progressBarRegistro = (ProgressBar) findViewById(R.id.progressBarRegistro);
        activity = this;

        storageReference = FirebaseStorage.getInstance().getReference();

        if(!called){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called = true;
        }


        databaseReference = FirebaseDatabase.getInstance().getReference();

        img_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {"Tomar foto", "Abrir galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(RegActivity.this);
                builder.setTitle("Foto de Perfil :)");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i] == "Tomar foto"){

                            abrirCamara();

                        }else if(options[i] == "Abrir galeria"){

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Seleccion la app de imagen"), SELECT_FOTO);

                        }else if (options[i] == "Cancelar"){

                            dialogInterface.dismiss();

                        }
                    }
                });
                builder.show();
            }
        });


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

                                btn_crear.setVisibility(View.GONE);
                                progressBarRegistro.setVisibility(View.VISIBLE);
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

    private String getHora(){

        Calendar cal = Calendar.getInstance();

        int hora = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int seg = cal.get(Calendar.SECOND);
        String horaS = String.valueOf(hora);
        String minS = String.valueOf(min);
        String segS = String.valueOf(seg);

        return horaS + "_" + minS + "_" + segS;

    }

    private void abrirCamara(){



        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY );
        file.mkdirs();

        String hora = getHora();

        String path = Environment.getExternalStorageDirectory() + File.separator
                + MEDIA_DIRECTORY + File.separator + "User_" + hora;

        File newfile = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(intent, CODIGO_FOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    String hora = getHora();

        switch (requestCode){
            case CODIGO_FOTO:
                if(resultCode == RESULT_OK){

                    String dir = Environment.getExternalStorageDirectory() + File.separator
                            + MEDIA_DIRECTORY + File.separator + "User_" + hora;
                    decodeBitmap(dir);

                }
                break;

            case SELECT_FOTO:
                if(resultCode == RESULT_OK){

                    Uri path = data.getData();
                    img_contacto.setImageURI(path);
                    fotoFinal = ((BitmapDrawable)img_contacto.getDrawable()).getBitmap();
                    fotolista = true;
                }
                break;
        }

    }

    private void decodeBitmap(String dir) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(dir);
        img_contacto.setImageBitmap(bitmap);

        img_perfil_circulo(bitmap);

    }

    public void img_perfil_circulo(Bitmap b){
        //extraemos el drawable en un bitmap
        Bitmap originalBitmap = b;

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        img_contacto.setImageDrawable(roundedDrawable);

    }

    private String guardarImg(Context context, Bitmap imagen){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Imagenes", Context.MODE_PRIVATE);

        String hora = getHora();

        File myPath = new File(dirImages, "Perfil.jpeg");

        Log.d(TAG,"A las: " + hora);
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

                    //Ademas guarda los datos en firebase y en la DB local :)
                    guardarFotoStorage(user);


                    Toast.makeText(RegActivity.this, "Cuenta creada, inicia sesion", Toast.LENGTH_SHORT).show();
                    finish();
                }else{


                    btn_crear.setVisibility(View.VISIBLE);
                    progressBarRegistro.setVisibility(View.GONE);
                    Toast.makeText(RegActivity.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void guardarFotoStorage(final User user) {
        if (fotolista) {

            btn_crear.setVisibility(View.GONE);
            progressBarRegistro.setVisibility(View.VISIBLE);


            StorageReference perfilFotoRef = storageReference.child(user.getUID() + ".jpeg");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fotoFinal.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            UploadTask uploadTask = perfilFotoRef.putBytes(imageInByte);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(activity, "no se subio manual", Toast.LENGTH_SHORT).show();

                    btn_crear.setVisibility(View.VISIBLE);
                    progressBarRegistro.setVisibility(View.GONE);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getDownloadUrl().getPath();

                    Log.d(TAG, downloadUrl);

                    user.setFoto(String.valueOf(downloadUrl));

                    guardarRealTime(user);
                    guardarDB(user);

                    String rutaLocal = guardarImg(RegActivity.this, fotoFinal);

                    Log.d(TAG, rutaLocal);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Long van = taskSnapshot.getBytesTransferred();
                    Long son = taskSnapshot.getTotalByteCount();

                    Log.d(TAG, "Van: " + String.valueOf(van) + " de " + String.valueOf(son));
                }
            });
        } else {

            guardarRealTime(user);
            guardarDB(user);

        }
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
