package com.example.chat;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.LogRecord;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMensajes extends RecyclerView.ViewHolder {
    private TextView nombre, mensaje, hora, nombre2, hora2, textCurrentTime, textTotalDuration;
    private CircleImageView FotoMensajePerfil, Foto2;
    private ImageView FotoMensajeEnviado, imagePlayPause;
    private SeekBar playerSeekBar;
    public MediaPlayer mediaPlayer;
    public Handler handler = new Handler();




    public TextView getTextCurrentTime() {
        return textCurrentTime;
    }

    public void setTextCurrentTime(TextView textCurrentTime) {
        this.textCurrentTime = textCurrentTime;
    }

    public TextView getTextTotalDuration() {
        return textTotalDuration;
    }

    public void setTextTotalDuration(TextView textTotalDuration) {
        this.textTotalDuration = textTotalDuration;
    }

    public ImageView getImagePlayPause() {
        return imagePlayPause;
    }

    public void setImagePlayPause(ImageView imagePlayPause) {
        this.imagePlayPause = imagePlayPause;
    }

    public SeekBar getPlayerSeekBar() {
        return playerSeekBar;
    }

    public void setPlayerSeekBar(SeekBar playerSeekBar) {
        this.playerSeekBar = playerSeekBar;
    }

    private CardView cardViewSC,cardView2;

    public TextView getNombre2() {
        return nombre2;
    }

    public void setNombre2(TextView nombre2) {
        this.nombre2 = nombre2;
    }

    public TextView getHora2() {
        return hora2;
    }

    public void setHora2(TextView hora2) {
        this.hora2 = hora2;
    }

    public CircleImageView getFoto2() {
        return Foto2;
    }

    public void setFoto2(CircleImageView foto2) {
        Foto2 = foto2;
    }

    public CardView getCardView2() {
        return cardView2;
    }

    public void setCardView2(CardView cardView2) {
        this.cardView2 = cardView2;
    }

    public CardView getCardViewSC()
    {
        return cardViewSC;
    }

    public void setCardViewSC(CardView cardViewSC) {
        this.cardViewSC = cardViewSC;
    }


    public HolderMensajes(@NonNull View itemView) {

        super(itemView);



        nombre = (TextView)itemView.findViewById(R.id.nombre_mensaje);
        nombre2 = (TextView)itemView.findViewById(R.id.nombre_mensaje_2);


        mensaje = (TextView)itemView.findViewById(R.id.Mensaje_mensaje);


        hora = (TextView)itemView.findViewById(R.id.hora_mensaje);
        hora2 = (TextView)itemView.findViewById(R.id.hora_mensaje_2);
        FotoMensajePerfil = (CircleImageView) itemView.findViewById(R.id.foto_mensaje);
        Foto2 = (CircleImageView) itemView.findViewById(R.id.foto_mensaje_2);

        FotoMensajeEnviado = (ImageView) itemView.findViewById(R.id.MensajeFoto);

        cardViewSC = (CardView)itemView.findViewById(R.id.cardViewSC);
        cardView2 = (CardView)itemView.findViewById(R.id.cardview2);


        imagePlayPause = (ImageView)itemView.findViewById(R.id.imagePlayPause);
        textCurrentTime = (TextView)itemView.findViewById(R.id.textCurrentTime);
        textTotalDuration = (TextView)itemView.findViewById(R.id.textTotalDuration);
        playerSeekBar = (SeekBar)itemView.findViewById(R.id.playerSeekBar);

        mediaPlayer = new MediaPlayer();
        playerSeekBar.setMax(100);


    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public CircleImageView getFotoMensajePerfil() {
        return FotoMensajePerfil;
    }

    public void setFotoMensajePerfil(CircleImageView fotoMensajePerfil) {
        this.FotoMensajePerfil = fotoMensajePerfil;
    }

    public ImageView getFotoMensajeEnviado() {
        return FotoMensajeEnviado;
    }

    public void setFotoMensajeEnviado(ImageView fotoMensajeEnviado) {
        FotoMensajeEnviado = fotoMensajeEnviado;
    }




    //metodo
    public String milisegundos(long milisegundo) {
        String TimerString = "", secondString;
        int hour = (int) (milisegundo / (1000 * 60 * 60));
        int minutes = (int) (milisegundo % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milisegundo % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hour > 0) {
            TimerString = hour + ":";
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        }else{
            secondString = "" + seconds;
        }

        TimerString = TimerString + minutes +":" + secondString;
        return TimerString;


    }

    public void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            //playerSeekBar.setProgress((int)(((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            getPlayerSeekBar().setProgress((int)(((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    public Runnable updater = new Runnable() {
        @Override
        public void run() {
      updateSeekbar();
      long currentduration = mediaPlayer.getCurrentPosition();
      //textCurrentTime.setText(milisegundos(currentduration));
      getTextCurrentTime().setText(milisegundos(currentduration));
        }
    };




}
