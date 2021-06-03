package com.example.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Adapters.AdaptersChats;
import com.example.chat.Adapters.AdaptersUsuario;
import com.example.chat.Objetos.Usuario;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ProgressBar progressBar;

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        progressBar = view.findViewById(R.id.progresbar);




        final RecyclerView recyclerView;
        final ArrayList<Usuario> usuarioArrayList;
        final AdaptersChats adaptersChats;
        LinearLayoutManager linearLayoutManager;

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(linearLayoutManager);

        usuarioArrayList = new ArrayList<>();
        adaptersChats = new AdaptersChats(usuarioArrayList,getContext());
        recyclerView.setAdapter(adaptersChats);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("usuarios");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    usuarioArrayList.removeAll(usuarioArrayList);
                    for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        usuarioArrayList.add(usuario);
                    }
                    adaptersChats.notifyDataSetChanged();

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"no existen usuarios",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
