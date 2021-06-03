package com.example.chat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.HomeActivity;
import com.example.chat.MainActivity;
import com.example.chat.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Formatter;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    private FirebaseUser user;
    private ImageView ImagenUsuario;
    private TextView Usuario, Correo,infoWifi;
    private MaterialButton btn_cerrar_sesion;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btn_cerrar_sesion = (MaterialButton) view.findViewById(R.id.btn_cerrar_sesion);


        btn_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AuthUI.getInstance()
                        .signOut(v.getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //user.delete();

                                   // finalize();

                                getActivity().finish();




                                Toast.makeText(v.getContext(), "cerrando sesion", LENGTH_SHORT).show();
                                vamosainisio();
                            }
                        });
            }
        });

        ImagenUsuario = (ImageView)view.findViewById(R.id.img_usuario);
        Usuario = (TextView)view.findViewById(R.id.tvNombre);
        Correo = (TextView)view.findViewById(R.id.tvCorreo);
        infoWifi = (TextView)view.findViewById(R.id.tv_info_wifi);

        //informacion de red

         WifiManager wifi = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();

            String BSSID = wifiInfo.getBSSID();
            int ip = wifiInfo.getIpAddress();
            String mac = wifiInfo.getMacAddress();
            int frec = wifiInfo.getFrequency();
           int rssi =wifiInfo.getRssi();
            String ssid =wifiInfo.getSSID();
            int linkspeed = wifiInfo.getLinkSpeed();
           int netid = wifiInfo.getNetworkId();

        String ipadres =  android.text.format.Formatter.formatIpAddress(ip);
        String info = "IpAddress: "+ ipadres +
                "\n" + "MacAddress: " + mac +
                "\n" + "BSSID: "+ BSSID +
                "\n" + "SSID: "+ssid +
                "\n" + "NetworkId: " + netid +
                "\n" + "Frecuencia: " + frec +
                "\n" + "LinkSpeed:" + linkspeed;




        //informacion de conexion
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected && activeNetwork.getTypeName().equals("WIFI")){
            infoWifi.setText("RED " + activeNetwork.getTypeName()  + "\n" + info);
        }
        else if (isConnected){
            infoWifi.setText("RED " + activeNetwork.getTypeName());
        }else {
            infoWifi.setTextColor(Color.blue(1));
            infoWifi.setText("SIN CONEXION");
        }


        //infoWifi.setText(wifi.getConnectionInfo().toString());

        user = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(this).load(user.getPhotoUrl()).into(ImagenUsuario);
        Usuario.setText("NOMBRE: \n" +user.getDisplayName());
        Correo.setText("CORREO: \n" + user.getEmail());




        return view;
    }

    private void vamosainisio() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
