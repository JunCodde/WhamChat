package com.apps.juncode.pruebawham.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apps.juncode.pruebawham.Adapter.RestAPIAdapter;
import com.apps.juncode.pruebawham.BaseDatos.ConstructorDB;
import com.apps.juncode.pruebawham.Endpoints.Endpoint;
import com.apps.juncode.pruebawham.Model.UID;
import com.apps.juncode.pruebawham.Model.User;
import com.apps.juncode.pruebawham.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    LinearLayout ly_invite;
    Button btn_invite;
    EditText et_invite;
    ProgressBar progressBarContacto;
    private static final String USERS_NODE = "User";
    private static final String CONTACT_NODE = "Contact";
    private DatabaseReference databaseReference;
    Context activity;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "ContactActivity";

    private RecyclerView rv_contactos;
    //private Contacto_adaptador adaptador;
    private LinearLayoutManager lManager;
    private ArrayList<User> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        activity = this;

        ly_invite = (LinearLayout) findViewById(R.id.ly_invite);
        btn_invite = (Button) findViewById(R.id.btn_invite);
        et_invite = (EditText) findViewById(R.id.et_invite_Email);
        progressBarContacto = (ProgressBar) findViewById(R.id.ProgressBarAgregarContacto);

        rv_contactos = (RecyclerView) findViewById(R.id.rv_contactos);
        rv_contactos.setHasFixedSize(true);

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_invite.getText().toString().isEmpty()){

                    Toast.makeText(activity, "Ingrese un correo", Toast.LENGTH_SHORT).show();

                }else{

                    RestAPIAdapter restAPIAdapter = new RestAPIAdapter();
                    Gson gsonEmail = restAPIAdapter.construyeGSONEmail();
                    Endpoint endpoint = restAPIAdapter.conexionRestApi(gsonEmail);
                    Call<UID> UIDResponse = endpoint.enviarMail(et_invite.getText().toString());

                    UIDResponse.enqueue(new Callback<UID>() {
                        @Override
                        public void onResponse(Call<UID> call, Response<UID> response) {

                            databaseReference = FirebaseDatabase.getInstance().getReference();

                            UID contactoUID = response.body();
                            Log.d(TAG, "UID:" + contactoUID.getUid());

                            databaseReference.child(USERS_NODE).child(contactoUID.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);

                                    User c = new User(u.getToken(), u.getUID(), u.getNombre(), u.getCorreo(), u.getFoto(), u.getActivo());

                                    guardarDB(c);

                                    guardarRealTime(c);

                                    //ponerRV();

                                    Log.d(TAG,  "Nombre:"   +  c.getNombre()    + "\n" +
                                            "Correo:"   +  c.getCorreo()    + "\n" +
                                            "UID:"      +  c.getUID()       + "\n");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            ly_invite.setVisibility(View.GONE);
                            et_invite.setText("");
                        }

                        @Override
                        public void onFailure(Call<UID> call, Throwable t) {

                            Toast.makeText(activity, "el correo no existe", Toast.LENGTH_SHORT).show();

                        }
                    });


                }

            }
        });
    }





    public void actContactos(){

        ConstructorDB constructorDB = new ConstructorDB(activity);
        constructorDB.BorrarContactos();

    }

    private void guardarDB(User c) {

        ConstructorDB constructorDB = new ConstructorDB(activity);
        constructorDB.insertarContacto(c);

    }

//    private void ponerRV(){
//
//        lManager = new LinearLayoutManager(this);
//        lManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rv_contactos.setLayoutManager(lManager);
//
//        pasarContactos();
//
//        adaptador = new Contacto_adaptador(contacts, this);
//        rv_contactos.setAdapter(adaptador);
//    }

    private void pasarContactos(){

        ConstructorDB constructorDB = new ConstructorDB(getApplicationContext());
        contacts = constructorDB.obtenerTodosLosContactos();

    }


    private void guardarRealTime(User c){

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        ConstructorDB db = new ConstructorDB(activity);

        if(db.obtenerTodosLosContactos().isEmpty()){

            Log.d(TAG, "No hay contactos");

            databaseReference.child(CONTACT_NODE).child(user.getUid()).child("0").setValue(c);

        }else{

            int contactosIndex =  db.obtenerTodosLosContactos().size();

            databaseReference.child(CONTACT_NODE).child(user.getUid()).child(String.valueOf(contactosIndex)).setValue(c);

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menucontact, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                Toast.makeText(ContactActivity.this, "Nuevo: " + newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.McontactNew:

                ly_invite.setVisibility(View.VISIBLE);

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(ContactActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
