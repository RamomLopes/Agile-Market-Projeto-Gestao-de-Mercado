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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.agilemarket.R;
import com.example.agilemarket.adapter.AdapterCategoria;
import com.example.agilemarket.adapter.AdapterSpinnerColor;
import com.example.agilemarket.adapter.AdapterSpinnerIcone;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.helper.CustomColors;
import com.example.agilemarket.helper.CustomIcons;
import com.example.agilemarket.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCategoriaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton fabDialogAddCategoria;
    private RecyclerView recyclerCategoria;
    private Category category;
    private List<Category> listaCategories;
    private List<CustomColors> cores;
    private List<CustomIcons> icones;
    private AutoCompleteTextView spinnerColor;
    private AutoCompleteTextView spinnerIcon;
    private String nvlAcesso;
    private DatabaseReference firebaseRef;
    private DatabaseReference categoriasRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerAddCategoria;
    private ValueEventListener valueEventListenerUsuarios;

    public AddCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCategoriaFragment newInstance(String param1, String param2) {
        AddCategoriaFragment fragment = new AddCategoriaFragment();
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
        loadCategoryList();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoriasRef.removeEventListener( valueEventListenerAddCategoria );
        //usuariosRef.removeEventListener( valueEventListenerUsuarios );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_categoria, container, false);

        fabDialogAddCategoria   = view.findViewById( R.id.fabAddCategoria );
        recyclerCategoria       = view.findViewById( R.id.recyclerShowCategoria );

        category = new Category();
        listaCategories = new ArrayList<>();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        fabDialogAddCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                            if ( nvlAcesso.equals( "Básico" ) ){
                                Snackbar snackbar = Snackbar.make(view, "Você não tem permissão para criar categorias", Snackbar.LENGTH_SHORT);
                                snackbar.setTextColor(Color.WHITE);
                                snackbar.setBackgroundTint(Color.DKGRAY);
                                snackbar.show();
                            }else{
                                showDialog(view);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }

    public void showDialog(View view){

        Dialog dialog = new Dialog( getContext() );

        dialog.setContentView( R.layout.dialog_cadastro_categoria);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        EditText editDescCategoria       = dialog.findViewById( R.id.editTextDescAddCategoria );
        Button btnAddCategoria           = dialog.findViewById( R.id.btnAddCategoria );
        spinnerColor  = dialog.findViewById( R.id.spinnerColorAddCategoria );
        spinnerIcon = dialog.findViewById( R.id.spinnerIconeAddCategoria );
        //spinnerLayoutColor = dialog.findViewById( R.id.inputLayoutSpinnerColor );

        configSpinner();

        btnAddCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = editDescCategoria.getText().toString().trim();

                if( !desc.isEmpty()  ){
                    if( !spinnerColor.getText().toString().isEmpty() ){
                        if( !spinnerIcon.getText().toString().isEmpty() ){
                            category.setDesc( desc );
                            createCategory( view );
                            loadCategoryList();
                            editDescCategoria.getText().clear();
                            dialog.dismiss();
                        }else{
                            spinnerIcon.setError("Escolha um icone");
                        }
                    }else{
                        spinnerColor.setError("Escolha uma cor");
                    }
                }else{
                    editDescCategoria.setError("Preencha a descrição");
                }
            }
        });

    }

    public void loadCategoryList(){

        AdapterCategoria adapterCategoria = new AdapterCategoria(listaCategories);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerCategoria.setLayoutManager( layoutManager );
        recyclerCategoria.setHasFixedSize( true );
        recyclerCategoria.setAdapter( adapterCategoria );

        categoriasRef = firebaseRef.child("categorias");
        valueEventListenerAddCategoria = categoriasRef.orderByChild("desc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCategories.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Category category = ds.getValue( Category.class );
                    listaCategories.add(category);
                }
                adapterCategoria.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void createCategory(View view){

        String idCategoria = firebaseRef.push().getKey();
        category.setId( idCategoria );

        String icone = spinnerIcon.getText().toString();

        category.setIcon( icone );

        category.save();

        Snackbar snackbar = Snackbar.make(view, "Categoria criada com sucesso!", Snackbar.LENGTH_SHORT);
        snackbar.setTextColor(Color.WHITE);
        snackbar.setBackgroundTint(Color.DKGRAY);
        snackbar.show();

        //Toast.makeText(getContext(), "Categoria criada com sucesso!",Toast.LENGTH_SHORT).show();
    }

    private void configSpinner(){

        AdapterSpinnerColor adapterColor = new AdapterSpinnerColor( getContext(), R.layout.formato_spinner_cor, getListColors() );

        if( spinnerColor != null){
            spinnerColor.setAdapter( adapterColor );
            spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getSelectedItem().toString();
                    spinnerColor.setText( item );
                    spinnerColor.setBackgroundColor( CustomColors.chooseColor( item ) );
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        AdapterSpinnerIcone adapterIcone = new AdapterSpinnerIcone( getContext(), R.layout.formato_spinner_icone, getListIcons() );

        if( spinnerIcon != null) {
            spinnerIcon.setAdapter(adapterIcone);
        }
    }

    private List<CustomColors> getListColors(){

        cores = new ArrayList<>();

        cores.add( new CustomColors("Abóbora", CustomColors.chooseColor("Abóbora") ) );
        cores.add( new CustomColors( "Coral", CustomColors.chooseColor("Coral") ));
        cores.add( new CustomColors( "Açafrão", CustomColors.chooseColor("Açafrão") ));
        cores.add( new CustomColors( "Amarelo", CustomColors.chooseColor("Amarelo") ));
        cores.add( new CustomColors( "Ameixa", CustomColors.chooseColor("Ameixa") ));
        cores.add( new CustomColors( "Vermelho", CustomColors.chooseColor("Vermelho") ));
        cores.add( new CustomColors( "Azul marinho", CustomColors.chooseColor("Azul marinho") ));
        cores.add( new CustomColors( "Azul claro", CustomColors.chooseColor("Azul claro") ));
        cores.add( new CustomColors( "Verde", CustomColors.chooseColor("Verde") ));
        cores.add( new CustomColors( "Verde claro", CustomColors.chooseColor("Verde claro") ));
        cores.add( new CustomColors( "Rosa", CustomColors.chooseColor("Rosa") ));
        cores.add( new CustomColors( "Rosa claro", CustomColors.chooseColor("Rosa claro") ));
        cores.add( new CustomColors( "Cinza", CustomColors.chooseColor("Cinza") ));
        cores.add( new CustomColors( "Marrom", CustomColors.chooseColor("Marrom") ));
        cores.add( new CustomColors( "Roxo", CustomColors.chooseColor("Roxo") ));

        return cores;
    }

    private List<CustomIcons> getListIcons(){

        icones = new ArrayList<>();

        icones.add( new CustomIcons("Padaria", R.drawable.padaria ) );
        icones.add( new CustomIcons("Carnes", R.drawable.carnes ) );
        icones.add( new CustomIcons("Farinhas", R.drawable.farinhas ) );
        icones.add( new CustomIcons("Frios e Laticínios", R.drawable.frios ) );
        icones.add( new CustomIcons("Bebidas", R.drawable.bebidas ) );
        icones.add( new CustomIcons("Utensílios", R.drawable.utensilios ) );
        icones.add( new CustomIcons("Limpeza", R.drawable.limpeza ) );
        icones.add( new CustomIcons("Higiene", R.drawable.higiene ) );
        icones.add( new CustomIcons("Frutas e Verduras", R.drawable.hortifruti ) );
        icones.add( new CustomIcons("Papelaria", R.drawable.papelaria ) );
        icones.add( new CustomIcons("Brinquedos", R.drawable.brinquedos ) );
        icones.add( new CustomIcons("Pet Shop", R.drawable.pet_shop ) );

        return icones;
    }

}