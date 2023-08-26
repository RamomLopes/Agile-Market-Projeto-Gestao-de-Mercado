package com.example.agilemarket.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.agilemarket.R;
import com.example.agilemarket.adapter.AdapterProduto;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaGeralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaGeralFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerListaGeral;
    private AutoCompleteTextView autoCompleteTextView;
    private List<Product> listaProducts;
    private List<Category> listaCategories;
    private ProgressBar progressBar;
    private ImageButton imgBtnCleanFilter;
    private DatabaseReference firebaseRef;
    private DatabaseReference categoriasRef;
    private DatabaseReference produtosRef;
    private ValueEventListener valueEventListenerCategorias;
    private ValueEventListener valueEventListenerProdutos;
    public AdapterProduto adapterProduto;

    public ListaGeralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaGeralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaGeralFragment newInstance(String param1, String param2) {
        ListaGeralFragment fragment = new ListaGeralFragment();
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
        popularListaProdutos();
        configurarSpinnerFiltro();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosRef.removeEventListener( valueEventListenerProdutos );
        categoriasRef.removeEventListener( valueEventListenerCategorias );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_geral, container, false);

        inicializarComponentes( view );

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        listaProducts = new ArrayList<>();
        listaCategories = new ArrayList<>();

        adapterProduto = new AdapterProduto(listaProducts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerListaGeral.setLayoutManager( layoutManager );
        recyclerListaGeral.setHasFixedSize( true );
        recyclerListaGeral.addItemDecoration( new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerListaGeral.setAdapter( adapterProduto );

        progressBar.setVisibility(View.VISIBLE);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemSelecionado = parent.getItemAtPosition(position).toString();

                Log.i("Spinner", "item: " + itemSelecionado);
                //autoCompleteTextView.setText(itemSelecionado[0]);

                listaProducts.clear();
                produtosRef = firebaseRef.child("produtos");
                valueEventListenerProdutos = produtosRef.orderByChild("desc").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if ( itemSelecionado.equals( ds.getValue( Product.class ).getCategoria().getDesc() ) ) {
                                listaProducts.add( ds.getValue( Product.class ) );
                            }
                        }
                        Log.i("produto", "lista: " + listaProducts.toString());
                        adapterProduto.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

                imgBtnCleanFilter.setVisibility(View.VISIBLE);
            }
        });

        imgBtnCleanFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaProducts.clear();
                popularListaProdutos();
                autoCompleteTextView.setText(null);
                autoCompleteTextView.setHint("Filtre por categoria");
                imgBtnCleanFilter.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void configurarSpinnerFiltro() {

        categoriasRef = firebaseRef.child("categorias");
        valueEventListenerCategorias = categoriasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Category category = ds.getValue( Category.class );
                    listaCategories.add(category);
                }

                ArrayAdapter<Category> adapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_spinner_dropdown_item, listaCategories);
                autoCompleteTextView.setAdapter( adapter );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void popularListaProdutos(){

        produtosRef = firebaseRef.child("produtos");
        valueEventListenerProdutos = produtosRef.orderByChild("produtos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Product prod = ds.getValue( Product.class );
                    listaProducts.add( prod );
                }
                progressBar.setVisibility(View.GONE);
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(View view){
        recyclerListaGeral  = view.findViewById( R.id.recyclerListaGeral );
        autoCompleteTextView = view.findViewById( R.id.textFiltroCategoria );
        progressBar         = view.findViewById( R.id.progressBarListaProduto );
        imgBtnCleanFilter   = view.findViewById( R.id.imgButtonCleanFilter );
    }

}