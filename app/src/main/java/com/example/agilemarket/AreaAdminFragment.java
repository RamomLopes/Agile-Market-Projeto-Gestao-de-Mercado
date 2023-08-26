package com.example.agilemarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agilemarket.adapter.AdapterColab;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AreaAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AreaAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<User> listaColab;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerUsuarios;

    public AreaAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AreaAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AreaAdminFragment newInstance(String param1, String param2) {
        AreaAdminFragment fragment = new AreaAdminFragment();
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
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener( valueEventListenerUsuarios );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_area_admin, container, false);

        recyclerView = view.findViewById( R.id.recyclerColab );
        listaColab = new ArrayList<>();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        configRecycler();

        return view;
    }

    private void configRecycler(){
        AdapterColab adapterColab = new AdapterColab( getColabs() );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize( true );
        recyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ));
        recyclerView.setAdapter( adapterColab );
    }

    private List<User> getColabs(){

        listaColab.clear();
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();

        usuariosRef = firebaseRef.child("usuarios");
        valueEventListenerUsuarios = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    if ( user.getEmpresaVinculada().equals( user.getEmail() )){
                        listaColab.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return listaColab;
    }
}