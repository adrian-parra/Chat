package com.example.chat.Adapters;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Objetos.Usuario;
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

public class AdaptersSolicitudes extends RecyclerView.Adapter<AdaptersSolicitudes.ViewHolderAdaptersSolicitudes> {
    List<Usuario> usersList;
    Context context;
    FirebaseUser userDatos = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public AdaptersSolicitudes(List<Usuario> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAdaptersSolicitudes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_solicitudes,parent,false);
        ViewHolderAdaptersSolicitudes holder = new ViewHolderAdaptersSolicitudes(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAdaptersSolicitudes holder, int position) {
        final Usuario user = usersList.get(position);
        final Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        Glide.with(context).load(user.getFoto()).into(holder.img_usuarios);
        holder.tv_usuario.setText(user.getNombre());


        DatabaseReference ref_mis_amigos = database.getReference("Solicitudes").child(user.getId());
        ref_mis_amigos.child(userDatos.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String estado = dataSnapshot.child("estado").getValue(String.class);

                if (dataSnapshot.exists()){
                    if(estado.equals("enviado")){

                        holder.cardView.setVisibility(View.VISIBLE);
                    }else {
                        holder.cardView.setVisibility(View.GONE);
                    }
                }else {
                    holder.cardView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference refBotones = database.getReference("Solicitudes").child(userDatos.getUid());
        refBotones.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String estado = dataSnapshot.child("estado").getValue(String.class);
                holder.progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {

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
                } else {

                    holder.send.setVisibility(View.GONE);
                    holder.amigos.setVisibility(View.GONE);
                    holder.tengosolicitud.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        holder.tengosolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = database.getReference("Solicitudes").child(user.getId()).child(userDatos.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        reference.child("estado").setValue("amigos");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference reference1 = database.getReference("Solicitudes").child(userDatos.getUid()).child(user.getId());
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        reference1.child("estado").setValue("amigos");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                vibrator.vibrate(300);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolderAdaptersSolicitudes extends RecyclerView.ViewHolder {
        ImageView img_usuarios;
        TextView tv_usuario;
        CardView cardView;
        Button add,send,amigos,tengosolicitud;
        ProgressBar progressBar;

        public ViewHolderAdaptersSolicitudes(@NonNull View itemView) {
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
