package com.example.chat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {


    //private DatabaseReference firebasereferencia = FirebaseDatabase.getInstance().getReference();
//autenticacion

//transition de actividades
    private Transition transition;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mutListener;
    public static final int SIGN_IN = 1;
    //verifica si cuenta con internet



    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashThem);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicio de la app
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        if (isConnected){


            //Toast.makeText(this,activeNetwork.getExtraInfo().toString(),Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(this,"no cuenta con internet fabor de conectarse a internet",Toast.LENGTH_SHORT).show();
            final CharSequence[] opciones= {"me conectare","no cuento con internet"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No cuneta con una conexion a internet ,fabor de conectarce");
            builder.setIcon(R.drawable.ic_wifi);

            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(opciones[which].equals("me conectare")){
                        Intent intent = new Intent();
                        //intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                       // Uri uri = Uri.fromParts("package" ,getPackageName(),null);
                        //intent.setData(uri);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(MainActivity.this, "la aplicacion no funcionara sin internet", LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                }
            });

            builder.show();



        }
        /*
        Toast.makeText(this, "Inisiando chat ll", Toast.LENGTH_LONG).show();
    firebasereferencia.child("loal").setValue("001");
*/

if (isConnected) {
    mFirebaseAuth = FirebaseAuth.getInstance();
    mutListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //Toast.makeText(MainActivity.this, "bienvenido " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                vamosahome();
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(), SIGN_IN

                );
            }
        }
    };
}


    }//fin oncreate


    //fin del oncreate
    public void vamosahome(){

      // transition = new Fade(Fade.OUT);

        //transition = new Slide(Gravity.START);
/* animacion
        transition = new Explode();
        transition.setDuration(1000);
        transition.setInterpolator(new DecelerateInterpolator());
       getWindow().setExitTransition(transition);
*/

    Intent i = new Intent(this,HomeActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
   // startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();



        //inicio de la app
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
if(isConnected) mFirebaseAuth.addAuthStateListener(mutListener);




    }

    @Override
    protected void onPause() {
        super.onPause();
        //inicio de la app
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

if(isConnected) mFirebaseAuth.removeAuthStateListener(mutListener);


    }
}
