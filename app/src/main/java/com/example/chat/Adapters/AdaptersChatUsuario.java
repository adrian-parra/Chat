package com.example.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chat.Mensaje;
import com.example.chat.Objetos.Chats;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptersChatUsuario extends RecyclerView.Adapter<AdaptersChatUsuario.ViewHolderAdapterChatUsuario> {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private List<Chats> mensajeList = new ArrayList<>();
    private Context c;



    public AdaptersChatUsuario(Context c){
        this.c = c;
    }

    public void addMensaje (Chats m){
        mensajeList.add(m);
        notifyItemInserted(mensajeList.size());
    }

    @NonNull
    @Override
    public ViewHolderAdapterChatUsuario onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.row_chat_usuario,parent,false);
        return new ViewHolderAdapterChatUsuario(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapterChatUsuario holder, final int position) {

        Chats chatsPosition = mensajeList.get(position);

        if (mensajeList.get(position).getRecibe().equals(user.getUid())){
           // holder.cardView_mensaje.setBackground(Drawable.createFromPath("#"));
            //holder.cardView_mensaje.setBackground(d);
           // holder.cardView_mensaje.setBackgroundColor(Color.parseColor("#0099FF"));
            holder.cardView_mensaje_right.setVisibility(View.GONE);
            holder.cardView_mensaje.setVisibility(View.VISIBLE);



            holder.cardView_mensaje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0099FF")));
            holder.tvMsj.setText(mensajeList.get(position).getMensaje());


        }else {
            //holder.cardView_mensaje.setBackground(e);
            //holder.cardView_mensaje.setBackgroundColor(Color.blue(1));
            //holder.cardView_mensaje.setBackgroundColor(Color.parseColor("#FF9933"));
           // holder.cardView_mensaje.setCardBackgroundColor(Color.parseColor("#FF9933"));
            holder.cardView_mensaje_right.setVisibility(View.VISIBLE);
            holder.cardView_mensaje.setVisibility(View.GONE);


            holder.cardView_mensaje_right.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9933")));
            holder.tvMsj_right.setText(mensajeList.get(position).getMensaje());

        }

        holder.cardView_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("INFORMACIÓN DE MENSAJE");
                builder.setMessage("mensaje recibido: " + "\nfecha: " + mensajeList.get(position).getFecha()  + "\nhora: "+ mensajeList.get(position).getHora());
                builder.setIcon(R.drawable.ic_chats);
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        holder.cardView_mensaje_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("INFORMACIÓN DE MENSAJE");
                builder.setMessage("mensaje enviado " + "\nfecha: " + mensajeList.get(position).getFecha()  + "\nhora: "+ mensajeList.get(position).getHora());
                builder.setIcon(R.drawable.ic_chats);
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    public class ViewHolderAdapterChatUsuario extends RecyclerView.ViewHolder {
        private CardView cardView_mensaje ,cardView_mensaje_right;
        private TextView tvMsj ,tvFecha_right,tvMsj_right,usuario_right;

        public ViewHolderAdapterChatUsuario(@NonNull View itemView) {
            super(itemView);
            cardView_mensaje = (CardView) itemView.findViewById(R.id.cardview_chat_usuario);

            tvMsj = (TextView) itemView.findViewById(R.id.tv_msj);


            cardView_mensaje_right= (CardView)itemView.findViewById(R.id.cardview_chat_usuario_right);

            tvMsj_right = (TextView)itemView.findViewById(R.id.tv_msj_right);


        }
    }
}
