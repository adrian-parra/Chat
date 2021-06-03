package com.example.chat.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.AdapterMensajes;
import com.example.chat.HomeActivity;
import com.example.chat.Mensaje;
import com.example.chat.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.toUpperCase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalaChatsFragment extends Fragment {
private TextView nombre;
private RecyclerView rvMensaje;
private CircleImageView fotoperfil;
private AdapterMensajes adapter;
private FirebaseDatabase database ;
private FirebaseStorage storage;
private StorageReference storageReference;
private DatabaseReference databaseReference;
private ImageButton btnEnviarFoto;
private static final int PHOTO_SEND = 1;
private ProgressDialog progressDialog;
private FirebaseUser user;
private EditText etMensaje;
private int eventET = 0;
private MediaRecorder mediaRecorder;
private String FileName = null;
private String FileNameEnviado = null;



    public SalaChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_sala_chats, container, false);


        final Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);

        //creando directorio
        crearDirectorioPublico("ChatsAudio");
        //declarando el progres dialog
        progressDialog = new ProgressDialog(getActivity());


        user = FirebaseAuth.getInstance().getCurrentUser();
        btnEnviarFoto = (ImageButton)view.findViewById(R.id.btnEnviarFoto);
        //PARTE DEL NOMBRE CON IMAGEN DE PERFIL
        //nombre = (TextView)view.findViewById(R.id.tvNombre);
        //nombre.setText(user.getDisplayName());
        //PARTE DEL NOMBRE CON IMAGEN DE PERFIL
       //fotoperfil = (CircleImageView)view.findViewById(R.id.img_usuario);
       //url de imagen de usuario
        //Glide.with(this).load(user.getPhotoUrl()).into(fotoperfil);


        rvMensaje = (RecyclerView) view.findViewById(R.id.rvMensajes);
        etMensaje = (EditText) view.findViewById(R.id.etMensaje);
        final Button btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        final Button btnEnviarAudio = (Button) view.findViewById(R.id.btnGrabarAudio);

        etMensaje.requestFocus();

        //FECHA ACTUAL
       // final Date date = new Date();
        //final CharSequence Fecha = DateFormat.format("MMMM/dd/yyyy ", date.getTime());
        //clase calendario para trabajar con fechas

       // tvFecha.setText(dia + " de "+ mes(Integer.toString(mes)) + " del " + amo);
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
       FileName += "/ChatsAudio/"+FormatoHora(new Date().toString()) +"-"+ user.getUid() + Fecha()+ ".3gp";
//FileName += "/ChatsAudio/123456.3gpp";

        btnEnviar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    if (event.getAction() == event.ACTION_DOWN){
                        StartRecording();

                        etMensaje.setHint("Grabando audio");

                    }else if (event.getAction() == event.ACTION_UP){
                        StopRecording();

                        etMensaje.setHint("Grabacion terminada");
                    }else if (event.getAction() ==event.ACTION_CANCEL){
                        etMensaje.setHint("Grabacion Cancelada");
                    }
                }catch (Exception e){
                    etMensaje.setHint("Escribe un mensaje");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setTitle("ERROR EN ENVIO DE AUDIO");
                    builder.setMessage("Mantén presionado para grabar,\nsuelta para enviar");
                    builder.setIcon(R.drawable.ic_error);
                    builder.setCancelable(true);
                    builder.setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }



                return false;
            }
        });

        etMensaje.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable s) {
                String elNuevoTexto = s.toString();
                // Hacer lo que sea con elNuevoTexto
                if(elNuevoTexto.equals("")){
                    btnEnviar.setVisibility(View.VISIBLE);
                    btnEnviarAudio.setVisibility(View.GONE);
                    btnEnviar.setText("Audio");



                }else{

                    etMensaje.setHint("Escribe un mensaje");
                    // btnEnviarFoto.setTooltipText("Enviar");
                    btnEnviar.setVisibility(View.GONE);
                    btnEnviarAudio.setVisibility(View.VISIBLE);

                    btnEnviarAudio.setText("Enviar");
                }

                //Toast.makeText(getContext(), "Cambió a " + elNuevoTexto, Toast.LENGTH_SHORT).show();
            }
        });


      //firebase database
    database = FirebaseDatabase.getInstance();
    databaseReference = database.getReference("Chat");//sala de chat
    //Firebase storage cloud
    storage = FirebaseStorage.getInstance();



    adapter = new AdapterMensajes(getActivity());
    final LinearLayoutManager o = new LinearLayoutManager(getActivity());

      rvMensaje.setLayoutManager(o);

       rvMensaje.setAdapter(adapter);

        btnEnviarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (etMensaje.length() == 0 ){
                    etMensaje.setError("Escribe un mensaje");


                }else {
                    databaseReference.push().setValue(new Mensaje(etMensaje.getText().toString(),toUpperCase(user.getDisplayName()),user.getPhotoUrl().toString(),"1",FormatoHora(new Date().toString())));
                    etMensaje.setText("");
                    setScrollbar();
                }




            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);




            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                setScrollbar();
                super.onItemRangeInserted(positionStart, itemCount);

            }
        });


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    Mensaje m = dataSnapshot.getValue(Mensaje.class);
                    adapter.addMensaje(m);
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

        return view;



    } //FIN DEL ONCREATE DEL FRAGMENT

    public File crearDirectorioPublico(String nombreDirectorio) {
        //Crear directorio público en la carpeta principal
        File directorio = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), nombreDirectorio);



        if (!directorio.exists()){
            if (!directorio.mkdirs()){}
        }


        return directorio;
    }
    //formato fecha
    public String Fecha(){
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

    private void StartRecording(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        FileNameEnviado = FileName;
        mediaRecorder.setOutputFile(FileNameEnviado);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
        mediaRecorder.prepare();

        }catch (IOException e){
        Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

        mediaRecorder.start();
    }

    private void StopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        UploadAudio();

    }
    private void setScrollbar() {
        rvMensaje.smoothScrollToPosition(adapter.getItemCount() - 1) ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            //Iniciando el progres dialogo
            progressDialog.setTitle("Subiendo...");
            progressDialog.setMessage("Enviando Imagen");
            progressDialog.setCancelable(false);
            progressDialog.show();



            final Uri u = data.getData();

            storageReference = storage.getReference("ImagenesDelChat");
           //final StorageReference fotoreferencia = StorageReference.child(u.getLastPathSegment());
          final StorageReference fotoreferencia = storageReference.child(u.getLastPathSegment());


          fotoreferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                  progressDialog.dismiss();

                  //obtener url de imagen
                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                  while (!uriTask.isSuccessful());
                  Uri ImagenDowload = uriTask.getResult();
                  Mensaje m =  new Mensaje("Envio una imagen", user.getDisplayName() , user.getPhotoUrl().toString(),"2",FormatoHora(new Date().toString()),ImagenDowload.toString());
                  databaseReference.push().setValue(m);


              }
          });


        }

    }
    //ENVIAR AUDIO A FIREBASE
    private void UploadAudio(){
        //Iniciando el progres dialogo
        //progressDialog.setTitle("Subiendo...");
        progressDialog.setMessage("Enviando Audio");
        progressDialog.setCancelable(false);
        progressDialog.show();


    storageReference = storage.getReference("AudiosDelChat");
    Uri uri = Uri.fromFile(new File(FileNameEnviado));
    final StorageReference audioreferencia = storageReference.child(uri.getLastPathSegment());

    audioreferencia.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            progressDialog.dismiss();

            //obtener url de imagen
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            Uri AudioDowload = uriTask.getResult();

            Mensaje m =  new Mensaje("Envio un audio", user.getDisplayName() , user.getPhotoUrl().toString(),"3",FormatoHora(new Date().toString()),"",AudioDowload.toString());
            databaseReference.push().setValue(m);
        }
    });





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
