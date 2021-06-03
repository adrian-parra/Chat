package com.example.chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.SettingInjectorService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chat.Adapters.PaginasAdapters;
import com.example.chat.Objetos.Estado;
import com.example.chat.Objetos.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.widget.Toast.LENGTH_SHORT;

public class HomeActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  FirebaseDatabase database = FirebaseDatabase.getInstance();
  DatabaseReference reference = database.getReference("usuarios").child(user.getUid());
  DatabaseReference ref_solicitudes_user = database.getReference("Contador").child(user.getUid());
    DatabaseReference ref_estado_user = database.getReference("Estado").child(user.getUid());
    WifiManager wifi;


    //static int Mensajes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificarConexion();

        //cargar transition a la activity



       // Slide slide = new Slide(Gravity.START);
        Fade fadein = new Fade(Fade.OUT);
        fadein.setDuration(200);
        fadein.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(fadein);
        setContentView(R.layout.activity_home);

        //inisio del oncreate

        //buscando datos de la cuenta de ususario
        /*
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       //insertar foto del usuario
        Glide.with(this).load(user.getPhotoUrl()).into(imagen_suaurio);

        tv_usuario.setText(user.getDisplayName());
        tv_correo.setText(user.getEmail());
        //color para el textviw correo
        tv_correo.setTextColor(Color.parseColor("#00CCFF"));
        tv_correo.setTextSize(22);
        tv_usuario.setTextColor(Color.parseColor("#00CCFF"));
        tv_usuario.setTextSize(22);
        */
        //inicio de la app
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();




        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new PaginasAdapters(this));
        final TabLayout tablayout = findViewById(R.id.tabLayout);


        CheckPermision();


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tablayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {


            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {



              //  Toast.makeText(HomeActivity.this,"number one", LENGTH_SHORT).show();


                switch(position){
                    case 0: {

                        tab.setText("Usuarios");
                        tab.setIcon(R.drawable.ic_sala_chat);

                        break;
                    }
                    case 1: {


                        tab.setText("Chats");
                        tab.setIcon(R.drawable.ic_chats);


                        break;
                    }
                    case 2: {
                        tab.setText("Solicitudes");
                        tab.setIcon(R.drawable.ic_solicitudes);

                        final BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(
                                ContextCompat.getColor(getApplicationContext(),R.color.colorAccent)
                        );



                        ref_solicitudes_user.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                           if (dataSnapshot.exists()){
                               Integer val = dataSnapshot.getValue(Integer.class);
                               badgeDrawable.setVisible(true);
                               if (val == 0){
                                   badgeDrawable.setVisible(false);

                               }else {
                                   badgeDrawable.setNumber(val);
                               }
                           }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                       // badgeDrawable.setNumber(999);

                        break;
                    }
                    case 3: {
                        tab.setText("Sala De Chat");
                        tab.setIcon(R.drawable.ic_sala_chat);





                        break;
                    }
                    case 4: {



                        tab.setText("Perfil");
                        tab.setIcon(R.drawable.ic_usuarios);



                        break;
                    }

                }


            }
        });
        tabLayoutMediator.attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badgeDrawable = tablayout.getTabAt(position).getOrCreateBadge();
                badgeDrawable.setVisible(false);

                if (position == 2){
                    countacero();
                }

            }
        });

        //agrega registro unico de usuario
        UserUnico();



    }//final de oncreate

    private void verificarConexion() {
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                metodoEjecutar();//llamamos nuestro metodo
                handler.postDelayed(this,1000);//se ejecutara cada 1 segundos
            }
        },1000);//empezara a ejecutarse después de 1 minuto
    }

    private void metodoEjecutar() {


        //inicio de la app
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();




        if (!isConnected) {
            //Toast.makeText(this,"no cuenta con internet fabor de conectarse a internet",Toast.LENGTH_SHORT).show();
            final CharSequence[] opciones = {"me conectare", "no cuento con internet"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("No cuneta con una conexion a internet ,fabor de conectarce");
            builder.setIcon(R.drawable.ic_wifi);


            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (opciones[which].equals("me conectare")) {
                        Intent intent = new Intent();
                        //intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                        // Uri uri = Uri.fromParts("package" ,getPackageName(),null);
                        //intent.setData(uri);
                        startActivity(intent);

                        getFragmentManager().beginTransaction().
                                remove(getFragmentManager().findFragmentById(R.id.f_chats)).commit();


                    } else {

                        Toast.makeText(HomeActivity.this, "la aplicacion no funcionara sin internet", LENGTH_SHORT).show();


                    }

                }
            });

            builder.show();
            //builder.setCancelable(true);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
estadousuario("conectado");
    }//Final onresume

    @Override
    protected void onPause() {
        super.onPause();
        estadousuario("desconectado");
        ultimafecha();
    }//final onpause

    private void ultimafecha() {
        final Calendar c = Calendar.getInstance();
        final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

        ref_estado_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref_estado_user.child("fecha").setValue(dateformat.format(c.getTime()));
                ref_estado_user.child("hora").setValue(timeformat.format(c.getTime()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void estadousuario(final String estado) {
        ref_estado_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Estado estado1 = new Estado(estado,"","","");
                ref_estado_user.setValue(estado1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countacero() {
        ref_solicitudes_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ref_solicitudes_user.setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Permisos de la aplicacion
    private void CheckPermision() {
        //verifica si el usuario tiene activado los permisos
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED) &&
        (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){ }else {
            //verifica si algun permiso no esta activo
            if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                    (shouldShowRequestPermissionRationale(RECORD_AUDIO))){
                DialogoRecomendacion();
            }else {
                requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE,RECORD_AUDIO},1);
            }
        }
    }

    private void DialogoRecomendacion() {
        //dialogo para usuario

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("permisos desactivados");
        builder.setMessage("Acepte los permisos para el correcto funcionamiento de la app");


        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE,RECORD_AUDIO},1);
            }
        });
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED){}else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {

        final CharSequence[] opciones= {"si","no"};
       final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("¿Desea configurar los permisos de forma manual?");
       builder.setItems(opciones, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("si")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package" ,getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else {
                    Toast.makeText(HomeActivity.this, "Los permisos no fueron aseptados", LENGTH_SHORT).show();
                    dialog.dismiss();
                }
           }
       });

        builder.show();



        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("permisos desactivados");
        builder.setMessage("Asepte los permisos para el correcto funcionamiento de la app");


        builder.setPositiveButton("Aceptar","Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE,RECORD_AUDIO},1);
            }
        });
        builder.show();


         */


    }

    private void UserUnico() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){

                    Usuario usuario = new Usuario(
                            user.getUid(),
                            user.getDisplayName(),
                            user.getEmail(),
                            user.getPhotoUrl().toString(),
                            "desconectado",
                            "12/02/02",
                            "12:20 p.m",
                            0,
                            0

                    );
                    reference.setValue(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_cerrar:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                          finish();
                           Toast.makeText(HomeActivity.this, "cerrando sesion", LENGTH_SHORT).show();
                            vamosainisio();
                            }
                        });


        }

        return super.onOptionsItemSelected(item);
    }

    private void vamosainisio() {
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


}
