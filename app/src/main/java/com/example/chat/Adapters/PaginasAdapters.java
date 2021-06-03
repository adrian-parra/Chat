package com.example.chat.Adapters;

import com.example.chat.Fragments.ChatsFragment;
import com.example.chat.Fragments.MisSolicitudesFragment;
import com.example.chat.Fragments.PerfilFragment;
import com.example.chat.Fragments.SalaChatsFragment;
import com.example.chat.Fragments.SolicitudesFragment;
import com.example.chat.Fragments.UsuariosFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PaginasAdapters extends FragmentStateAdapter {
    public PaginasAdapters(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position){
            case 0:

                return new UsuariosFragment();

            case 1:

            return new ChatsFragment();

            case 2:
                return new SolicitudesFragment();
            case 3:

            return new SalaChatsFragment();
            case 4:
                return new PerfilFragment();
            default:
                return new UsuariosFragment();

        }


    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
