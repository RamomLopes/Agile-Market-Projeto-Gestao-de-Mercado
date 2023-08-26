package com.example.agilemarket.activitys;

import android.os.Bundle;

import com.example.agilemarket.R;
import com.example.agilemarket.ui.data.inicio.InicioFragment;
import com.example.agilemarket.ui.data.listas.ListasFragment;
import com.example.agilemarket.ui.data.perfil.PerfilFragment;
import com.example.agilemarket.ui.data.produto.ProdutoFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.agilemarket.databinding.ActivityBottomMenuBinding;

public class BottomMenuActivity extends AppCompatActivity {

    private ActivityBottomMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment( new InicioFragment() );

        binding.navView.setOnItemSelectedListener( item -> {

           switch ( item.getItemId() ){

               case R.id.navigation_inicio:
                    replaceFragment( new InicioFragment() );
                   break;

               case R.id.navigation_produto:
                    replaceFragment( new ProdutoFragment() );
                   break;

               case R.id.navigation_listas:
                    replaceFragment( new ListasFragment() );
                   break;

               case R.id.navigation_perfil:
                    replaceFragment( new PerfilFragment() );
                   break;
           }

           return true;
        });

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace( R.id.nav_host_fragment_activity_bottom_menu, fragment );
        transaction.commit();

    }

}