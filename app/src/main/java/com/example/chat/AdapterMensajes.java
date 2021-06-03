package com.example.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensajes> {
    private List<Mensaje> mensajeList = new ArrayList<>();
    private Context c;

    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje (Mensaje m){
        mensajeList.add(m);
        notifyItemInserted(mensajeList.size());
    }

    @NonNull
    @Override
    public HolderMensajes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);

        return new HolderMensajes(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderMensajes holder, final int position) {
        holder.getCardViewSC().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),mensajeList.get(position).getNombre().toString(),Toast.LENGTH_SHORT).show();

            }
        });

        holder.mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                holder.getPlayerSeekBar().setSecondaryProgress(percent);
            }
        });

        holder.getPlayerSeekBar().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar = (SeekBar)v;
                int playposition = (holder.mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                holder.mediaPlayer.seekTo(playposition);
                holder.getTextCurrentTime().setText(holder.milisegundos(holder.mediaPlayer.getCurrentPosition()));

                return false;
            }
        });

        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.getPlayerSeekBar().setProgress(0);
                holder.getImagePlayPause().setBackgroundResource(R.drawable.ic_play);
                holder.getTextCurrentTime().setText("0:00");
                holder.getTextTotalDuration().setText("0:00");
                //holder.mediaPlayer.reset();
                mp.reset();
                try {
                    holder.mediaPlayer.setDataSource(mensajeList.get(position).getUrlAudio());
                    //holder.mediaPlayer.setDataSource("https://trochlear-waste.000webhostapp.com/archivos/mp3/Riverdale-Cast-feat-KJ-Apa-Camila-Mendes-Lili-Reinhart_-_Mad-World.mp3");
                    holder.mediaPlayer.prepare();
                    holder.getTextTotalDuration().setText(holder.milisegundos(holder.mediaPlayer.getDuration()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        holder.getImagePlayPause().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mediaPlayer.isPlaying()){
                    holder.handler.removeCallbacks(holder.updater);
                    holder.mediaPlayer.pause();
                    //holder.getImagePlayPause().setImageResource(R.drawable.ic_play);
                    holder.getImagePlayPause().setBackgroundResource(R.drawable.ic_play);
                }else {

                    holder.mediaPlayer.start();
                    holder.updateSeekbar();
                    //holder.getImagePlayPause().setImageResource(R.drawable.ic_pause);
                    holder.getImagePlayPause().setBackgroundResource(R.drawable.ic_pause);


                }

            }
        });


        //foto de mensaje
        Glide.with(c).load(mensajeList.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());
        if (mensajeList.get(position).getTypeMensaje().equals("2")){
            holder.getCardView2().setVisibility(View.GONE);
            holder.getCardViewSC().setVisibility(View.VISIBLE);
            holder.getNombre().setText(mensajeList.get(position).getNombre());
            holder.getHora().setText(mensajeList.get(position).getHora());
            holder.getMensaje().setText(mensajeList.get(position).getMensaje());
            holder.getFotoMensajeEnviado().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(mensajeList.get(position).getUrlFoto()).into(holder.getFotoMensajeEnviado());
        }else if (mensajeList.get(position).getTypeMensaje().equals("1")) {
            holder.getCardView2().setVisibility(View.GONE);
            holder.getCardViewSC().setVisibility(View.VISIBLE);
            holder.getNombre().setText(mensajeList.get(position).getNombre());
            holder.getHora().setText(mensajeList.get(position).getHora());
            holder.getMensaje().setText(mensajeList.get(position).getMensaje());
            holder.getFotoMensajeEnviado().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);

        }else if (mensajeList.get(position).getTypeMensaje().equals("3")){
            holder.getCardView2().setVisibility(View.VISIBLE);
            holder.getCardViewSC().setVisibility(View.GONE);
            holder.getHora2().setText(mensajeList.get(position).getHora());
            Glide.with(c).load(mensajeList.get(position).getFotoPerfil()).into(holder.getFoto2());
            holder.getNombre2().setText(mensajeList.get(position).getNombre());

            try {
                holder.mediaPlayer.setDataSource(mensajeList.get(position).getUrlAudio());
               // holder.mediaPlayer.setDataSource("https://trochlear-waste.000webhostapp.com/archivos/mp3/Riverdale-Cast-feat-KJ-Apa-Camila-Mendes-Lili-Reinhart_-_Mad-World.mp3");
                holder.mediaPlayer.prepare();
                holder.getTextTotalDuration().setText(holder.milisegundos(holder.mediaPlayer.getDuration()));
            } catch (IOException e) {
                e.printStackTrace();
            }


        }



    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }


}
