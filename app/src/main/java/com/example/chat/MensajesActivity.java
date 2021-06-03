package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chat.Adapters.AdaptersChatUsuario;
import com.example.chat.Objetos.Chats;
import com.example.chat.Objetos.Estado;
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
import java.util.Date;

public class MensajesActivity extends AppCompatActivity {
CircleImageView img_user;
TextView username;
ImageView ic_conectado,ic_desconectado;
SharedPreferences mPref;
FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference ref_estado = database.getReference("Estado").child(user.getUid());
    DatabaseReference ref_chats = database.getReference("Chats");
MediaPlayer mp;

EditText et_mensaje_txt;
ImageButton btn_enviar_msj;
private AdaptersChatUsuario adapter;
private RecyclerView rvMsj;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPref = getApplicationContext().getSharedPreferences("usuario_sp",MODE_PRIVATE);
        rvMsj = findViewById(R.id.rvMensaje);
        img_user = findViewById(R.id.img_usuarios);
        username = findViewById(R.id.tv_usuarios);
        ic_conectado = findViewById(R.id.icon_conectado);
        ic_desconectado = findViewById(R.id.icon_desconectado);

        adapter = new AdaptersChatUsuario(this);
        final LinearLayoutManager o = new LinearLayoutManager(this);
        rvMsj.setLayoutManager(o);
        rvMsj.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                setScrollbar();
                super.onItemRangeInserted(positionStart, itemCount);

            }
        });


        String usuario = getIntent().getExtras().getString("nombre");
        String foto = getIntent().getExtras().getString("img_user");
        final String id_user = getIntent().getExtras().getString("id_user");
        final String id_unico = getIntent().getExtras().getString("id_unico");

        et_mensaje_txt = findViewById(R.id.et_txt_mensaje);
        btn_enviar_msj = findViewById(R.id.btn_enviar_msj);

        mp=MediaPlayer.create(this,R.raw.moneda);

        btn_enviar_msj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_mensaje_txt.getText().toString().equals("")){
                    et_mensaje_txt.setError("Ingrese el mensaje...");
                    et_mensaje_txt.requestFocus();

                }else{
                   // mp =MediaPlayer.create(this, androidx.media.R.raw.musica);
                    mp.start();
                    String msj = et_mensaje_txt.getText().toString();
                    Chats chatmsj = new Chats(user.getUid() ,id_user ,msj ,"no",FormatoHora(new Date().toString()),fecha());
                    ref_chats.child(id_unico).push().setValue(chatmsj);
                    Toast.makeText(MensajesActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    et_mensaje_txt.setText("");
                }

            }
        });
        final String id_user_sp = mPref.getString("usuario_sp","");
        username.setText(usuario);
        Glide.with(this).load(foto).into(img_user);

        final DatabaseReference ref = database.getReference("Estado").child(id_user_sp).child("chatcon");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           String chatcon = dataSnapshot.getValue(String.class);
           if (dataSnapshot.exists()){
               if (chatcon.equals(user.getUid())){
                   ic_conectado.setVisibility(View.VISIBLE);
                   ic_desconectado.setVisibility(View.GONE);

               }else {
                   ic_desconectado.setVisibility(View.VISIBLE);
                   ic_conectado.setVisibility(View.GONE);
               }
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //firebase escucha cada segundo que pasa
        ref_chats.child(id_unico).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //entra cuando se agrega datos a la referencia de firebase
                if (dataSnapshot.exists()){

                    Chats chats = dataSnapshot.getValue(Chats.class);
                    adapter.addMensaje(chats);

                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//fin oncreate

    private void setScrollbar() {
        rvMsj.smoothScrollToPosition(adapter.getItemCount() - 1) ;
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

        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref_estado.child("fecha").setValue(dateformat.format(c.getTime()));
                ref_estado.child("hora").setValue(timeformat.format(c.getTime()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void estadousuario(final String estado) {
        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String id_user_sp = mPref.getString("usuario_sp","");
                Estado estado1 = new Estado(estado,"","",id_user_sp);
                ref_estado.setValue(estado1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String fecha(){
        Calendar calendario = Calendar.getInstance();
        int meses = calendario.get(Calendar.MONTH) + 1;
        int dias = calendario.get(Calendar.DAY_OF_MONTH);
        int amos = calendario.get(Calendar.YEAR);
        String mes = Integer.toString(meses) ,mess = "";


        switch(mes){
            case "1": mess = "Enero";
                break;
            case "2": mess = "Febrero";
                break;
            case "3": mess = "Marzo";
                break;
            case "4": mess = "Abril";
                break;
            case "5": mess = "Mayo";
                break;
            case "6": mess = "Junio";
                break;
            case "7": mess = "Julio";
                break;
            case "8": mess = "Agosto";
                break;
            case "9": mess = "Septiembre";
                break;
            case "10": mess = "Octubre";
                break;
            case "11": mess = "Noviembre";
                break;
            case "12": mess = "Diciembre";
        }
        return dias + " de " +mess + " del " + amos;
    }

    //FORMATO HORA
    public String FormatoHora(String date) {
        //34 caracteres
        //12 a 19 --> hora minutos segundos
        String Hora = date.substring(11, 13);
        String Minutos = date.substring(14,16);


        String AmPm = (Integer.parseInt(Hora) >= 12 && Integer.parseInt(Minutos) > 0 ) ? "p. m." : "a. m.";

        Hora = FormatoHoraDoce(Hora);

        return Hora + ":" + Minutos + " " + AmPm;
    }

    //Hora formato 12 horas
    private String FormatoHoraDoce(String Hora){
        String HoraFormateada = "";

        switch (Hora) {
            case "13": HoraFormateada = "1";
                break;
            case "14": HoraFormateada = "2";
                break;
            case "15": HoraFormateada = "3";
                break;
            case "16": HoraFormateada = "4";
                break;
            case "17": HoraFormateada = "5";
                break;
            case "18": HoraFormateada = "6";
                break;
            case "19": HoraFormateada = "7";
                break;
            case "20": HoraFormateada = "8";
                break;
            case "21": HoraFormateada = "9";
                break;
            case "22": HoraFormateada = "10";
                break;
            case "23": HoraFormateada = "11";
                break;
            case "00": HoraFormateada = "12";
                break;
            default:
                int fmat = Integer.parseInt(Hora);
                HoraFormateada = Integer.toString(fmat);
        }
        return HoraFormateada;
    }
}
