package com.example.agilemarket.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.agilemarket.R;
import com.example.agilemarket.adapter.AdapterChooseCategoria;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.models.Product;
import com.example.agilemarket.helper.RecyclerItemClickListener;
import com.example.agilemarket.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProdutoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProdutoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerChooseCategoria;
    private List<Category> listaCategory;

    private Product product;
    private Category category;
    private String descProduto;
    private ProgressBar progressBar;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference categoriasRef;
    private ValueEventListener valueEventListenerUsuarios;
    private ValueEventListener valueEventListenerCategorias;
    private String nvlAcesso;

    public AddProdutoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProdutoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProdutoFragment newInstance(String param1, String param2) {
        AddProdutoFragment fragment = new AddProdutoFragment();

        //recupera dados da activity anterior
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
        carregaRecyclerChooseCategoria();
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
        View view = inflater.inflate(R.layout.fragment_add_produto, container, false);

        recyclerChooseCategoria = view.findViewById( R.id.recyclerChooseCategoria );
        progressBar             = view.findViewById( R.id.progressBarAddProduto );

        listaCategory = new ArrayList<>();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        recyclerChooseCategoria.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        recyclerChooseCategoria,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                category = listaCategory.get( position );

                                String idUsuario = UsuarioFirebase.getIdUsuario();

                                usuariosRef =  firebaseRef.child("usuarios");
                                valueEventListenerUsuarios = usuariosRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            User user = ds.getValue( User.class );

                                            if (user.getId().equals( idUsuario )){
                                                nvlAcesso = user.getNvlAcesso();
                                            }
                                        }

                                        Log.i("tag: ", "ACESSO: " + nvlAcesso);

                                        if( nvlAcesso != null ){
                                            if( nvlAcesso.equals("Básico") ){
                                                Snackbar snackbar = Snackbar.make( AddProdutoFragment.this.getView(), "Você não tem permissão para criar produtos!", Snackbar.LENGTH_SHORT );

                                                snackbar.setDuration(1000);
                                                snackbar.setTextColor( Color.WHITE );
                                                snackbar.setBackgroundTint( Color.DKGRAY );
                                                snackbar.show();
                                            }else{
                                                showDialogRegisterProduct();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                ));

        return view;
    }

    public void showDialogRegisterProduct(){

        Dialog dialog = new Dialog( getContext() );

        dialog.setContentView( R.layout.dialog_cadastro_produto );

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Button btnAddProduto        = dialog.findViewById( R.id.btnAddProduto );
        EditText editDescCategoria  = dialog.findViewById( R.id.editTextDescCriarProduto );

        btnAddProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                descProduto = editDescCategoria.getText().toString().trim();

                if(!descProduto.isEmpty()){

                    createProduct( descProduto );

                    Snackbar snackbar = Snackbar.make( AddProdutoFragment.this.getView(), "Produto criado com sucesso!", Snackbar.LENGTH_SHORT );

                    snackbar.setDuration(1000);
                    snackbar.setTextColor( Color.WHITE );
                    snackbar.setBackgroundTint( Color.DKGRAY );
                    snackbar.show();

                    //Toast.makeText( getContext(), "Produto criado com sucesso!", Toast.LENGTH_SHORT ).show();
                    dialog.dismiss();

                }else{
                    editDescCategoria.setError("Preencha o campo descrição do produto!");
                    //Toast.makeText( getContext(), "Preencha o campo descrição!", Toast.LENGTH_SHORT ).show();
                }

            }
        });
    }

    public void carregaRecyclerChooseCategoria(){

        progressBar.setVisibility(View.VISIBLE);
        AdapterChooseCategoria adapter = new AdapterChooseCategoria(listaCategory);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
        recyclerChooseCategoria.setLayoutManager( layoutManager );
        recyclerChooseCategoria.setHasFixedSize( true );
        recyclerChooseCategoria.setAdapter( adapter );

        categoriasRef = firebaseRef.child("categorias");
        valueEventListenerCategorias = categoriasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Category category = ds.getValue( Category.class );
                    listaCategory.add(category);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createProduct(String descProduto){

        product = new Product();

        product.setCategoria(category);
        product.setDesc( descProduto );
        product.setDescPesquisa( descProduto );

        Log.i("PRODUTO", "Descrição: " + product.getDesc());

        String idProduto = firebaseRef.push().getKey();
        product.setId( idProduto );
        Log.i("Produto", "produto: " + idProduto);

        product.save();

        //Toast.makeText(getContext(), "Produto criado com sucesso!",Toast.LENGTH_SHORT).show();

    }
}