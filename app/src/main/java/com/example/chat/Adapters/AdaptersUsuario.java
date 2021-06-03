package com.example.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.MensajesActivity;
import com.example.chat.Objetos.Usuario;
import com.example.chat.Objetos.solicitudes;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptersUsuario extends RecyclerView.Adapter<AdaptersUsuario.ViewHolderAdapter> {
    List<Usuario> usersList;
    Context context;
    FirebaseUser userDatos = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences mPref;

    public AdaptersUsuario(List<Usuario> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_usuarios,parent,false);
        ViewHolderAdapter holder = new ViewHolderAdapter(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAdapter holder, int position) {
        final Usuario user = usersList.get(position);
        final Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        Glide.with(context).load(user.getFoto()).into(holder.img_usuarios);
        holder.tv_usuario.setText(user.getNombre());

        if (user.getId().equals(userDatos.getUid())){
            holder.cardView.setVisibility(View.GONE);

            //

        }else{
            holder.cardView.setVisibility(View.VISIBLE);
        }

        final DatabaseReference refBotones = database.getReference("Solicitudes").child(userDatos.getUid());
    refBotones.child(user.getId()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String estado = dataSnapshot.child("estado").getValue(String.class);
            holder.progressBar.setVisibility(View.GONE);
            if (dataSnapshot.exists()){

                switch (estado) {
                    case "enviado": {
                        holder.send.setVisibility(View.VISIBLE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);

                        break;
                    }

                    case "amigos": {
                        holder.amigos.setVisibility(View.VISIBLE);
                        holder.send.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);

                        break;
                    }
                    case "solicitud": {
                        holder.tengosolicitud.setVisibility(View.VISIBLE);
                        holder.send.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);



                        break;
                    }

                }
            }else {

                holder.send.setVisibility(View.GONE );
                holder.amigos.setVisibility(View.GONE);
                holder.tengosolicitud.setVisibility(View.GONE);
                holder.add.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
        //evento click del boton agregar usuario
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = database.getReference("Solicitudes").child(userDatos.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        solicitudes Solicitudes = new solicitudes("enviado" , "");
                           // reference.child(user.getId()).child("estado").setValue("enviado");
                        reference.child(user.getId()).setValue(Solicitudes);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference reference1 = database.getReference("Solicitudes").child(user.getId());
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        solicitudes Solicitudes = new solicitudes("solicitud" , "");
                            reference1.child(userDatos.getUid()).setValue(Solicitudes);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //conteo de solicitudes
                final DatabaseReference count = database.getReference("Contador").child(user.getId());
                count.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Integer val = dataSnapshot.getValue(Integer.class);
                            if (val == 0){
                                count.setValue(1);
                            }else {
                                count.setValue(val + 1);
                            }
                        }else {
                            count.setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                vibrator.vibrate(300);

            }//final de onclick


        });

        holder.tengosolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obteniendo id unico para chatear con persona
                final String idchat = refBotones.push().getKey();
                //Toast.makeText(context, idchat + "hola", Toast.LENGTH_SHORT).show();
                final DatabaseReference reference = database.getReference("Solicitudes").child(user.getId()).child(userDatos.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        solicitudes Solicitudes = new solicitudes("amigos" , idchat);
                        reference.setValue(Solicitudes);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference reference1 = database.getReference("Solicitudes").child(userDatos.getUid()).child(user.getId());
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        solicitudes Solicitudes = new solicitudes("amigos" , idchat);
                        reference1.setValue(Solicitudes);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                vibrator.vibrate(300);
            }
        });

        holder.amigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPref = v.getContext().getSharedPreferences("usuario_sp",Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = mPref.edit();

                final DatabaseReference reference = database.getReference("Solicitudes").child(userDatos.getUid()).child(user.getId()).child("idchat");
reference.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String id_unico = dataSnapshot.getValue(String.class);
        if (dataSnapshot.exists()){
            Intent intent = new Intent(v.getContext(), MensajesActivity.class);
            intent.putExtra("nombre",user.getNombre());
            intent.putExtra("img_user",user.getFoto());
            intent.putExtra("id_user",user.getId());
            intent.putExtra("id_unico",id_unico);

            editor.putString("usuario_sp",user.getId());
            editor.apply();

            v.getContext().startActivity(intent);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolderAdapter extends RecyclerView.ViewHolder {
        ImageView img_usuarios;
        TextView tv_usuario;
        CardView cardView;
        Button add,send,amigos,tengosolicitud;
        ProgressBar progressBar;


        public ViewHolderAdapter(@NonNull View itemView) {
            super(itemView);
           tv_usuario = itemView.findViewById(R.id.tv_usuarios);
            img_usuarios = itemView.findViewById(R.id.img_usuarios);
          cardView = itemView.findViewById(R.id.cardview_usuarios);
            add = itemView.findViewById(R.id.btn_add);
            send = itemView.findViewById(R.id.btn_send);
            amigos = itemView.findViewById(R.id.btn_amigos);
            tengosolicitud = itemView.findViewById(R.id.btn_tengosolicitud);
            progressBar = itemView.findViewById(R.id.progressbar);


        }
    }
}
