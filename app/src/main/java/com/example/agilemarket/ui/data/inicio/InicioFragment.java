package com.example.agilemarket.ui.data.inicio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agilemarket.R;
import com.example.agilemarket.adapter.AdapterCategoriaHorizontal;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.fragments.AddCategoriaFragment;
import com.example.agilemarket.fragments.ListaCotacaoFragment;
import com.example.agilemarket.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private TextView btnCriarCategoria;
    private List<Category> listaCategory;
    private TextView btnListaCotacao;
    private AdapterCategoriaHorizontal adapter;
    private DatabaseReference firebaseRef;
    private DatabaseReference categoriasRef;
    private ValueEventListener valueEventListenerCategorias;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        preencherListaCategoria();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoriasRef.removeEventListener( valueEventListenerCategorias );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView        = view.findViewById( R.id.recyclerCategoriaInicio );
        btnCriarCategoria   = view.findViewById( R.id.textBtnCriarCategoriaInicio );
        btnListaCotacao     = view.findViewById( R.id.textBtnListaCotacao );

        listaCategory = new ArrayList<>();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        btnCriarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategoriaFragment addCategoriaFragment = new AddCategoriaFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.nav_host_fragment_activity_bottom_menu, addCategoriaFragment );
                transaction.commit();
            }
        });

        btnListaCotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListaCotacaoFragment listaCotacaoFragment = new ListaCotacaoFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.nav_host_fragment_activity_bottom_menu, listaCotacaoFragment );
                transaction.commit();
            }
        });

        adapter = new AdapterCategoriaHorizontal(listaCategory);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.HORIZONTAL, false );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );

        return view;
    }

    private void preencherListaCategoria(){

        categoriasRef = firebaseRef.child("categorias");
        valueEventListenerCategorias = categoriasRef.orderByChild("desc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCategory.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    listaCategory.add( ds.getValue(Category.class) );
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}