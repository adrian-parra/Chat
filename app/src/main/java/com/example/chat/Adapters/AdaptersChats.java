package com.example.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.MensajesActivity;
import com.example.chat.Objetos.Usuario;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptersChats extends RecyclerView.Adapter<AdaptersChats.ViewHolderAdapterChats> {
    SharedPreferences mPref;
    List<Usuario> usersList;
    Context context;
    FirebaseUser userDatos = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public AdaptersChats(List<Usuario> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAdapterChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chats,parent,false);
        ViewHolderAdapterChats holder = new ViewHolderAdapterChats(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAdapterChats holder, int position) {
        final Usuario user = usersList.get(position);
        final Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        Glide.with(context).load(user.getFoto()).into(holder.img_usuarios);
        holder.tv_usuario.setText(user.getNombre());


        DatabaseReference ref_mis_amigos = database.getReference("Solicitudes").child(userDatos.getUid());
        ref_mis_amigos.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String estado = dataSnapshot.child("estado").getValue(String.class);

                if (dataSnapshot.exists()){
                    if (estado.equals("amigos")){
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

        final Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

        DatabaseReference ref_estado = database.getReference("Estado").child(user.getId());
        ref_estado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String estado = dataSnapshot.child("estado").getValue(String.class);
                String fecha = dataSnapshot.child("fecha").getValue(String.class);
                String hora = dataSnapshot.child("hora").getValue(String.class);

                if (dataSnapshot.exists()){
                    if (estado.equals("conectado")){
                        holder.img_conectado.setVisibility(View.VISIBLE);
                        holder.tv_conectado.setVisibility(View.VISIBLE);
                        holder.img_desconectado.setVisibility(View.GONE);
                        holder.tv_desconectado.setVisibility(View.GONE);

                    }else {

                        holder.img_desconectado.setVisibility(View.VISIBLE);
                        holder.tv_desconectado.setVisibility(View.VISIBLE);
                        holder.img_conectado.setVisibility(View.GONE);
                        holder.tv_conectado.setVisibility(View.GONE);

                        if (fecha.equals(dateformat.format(c.getTime()))){
                            holder.tv_desconectado.setText("ult.vez hoy a las " + hora);
                        }else {
                            holder.tv_desconectado.setText("ult.vez " +fecha + " a las " + hora);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //escucha cuando se le da click al cardview del fragment chats
                mPref = v.getContext().getSharedPreferences("usuario_sp", Context.MODE_PRIVATE);
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

    public class ViewHolderAdapterChats extends RecyclerView.ViewHolder {
        ImageView img_usuarios,img_desconectado,img_conectado;
        TextView tv_usuario,tv_desconectado,tv_conectado;
        CardView cardView;



        public ViewHolderAdapterChats(@NonNull View itemView) {
            super(itemView);
           tv_usuario = itemView.findViewById(R.id.tv_usuarios);
            img_usuarios = itemView.findViewById(R.id.img_usuarios);
          cardView = itemView.findViewById(R.id.cardview_usuarios);
          img_desconectado = itemView.findViewById(R.id.img_desconectado);
            img_conectado = itemView.findViewById(R.id.img_conectado);
            tv_desconectado = itemView.findViewById(R.id.tv_desconectado);
            tv_conectado = itemView.findViewById(R.id.tv_conectado);



        }
    }
}
