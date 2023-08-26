package com.example.agilemarket.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.adapter.AdapterListaCotacao;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.helper.DateCustom;
import com.example.agilemarket.helper.Permissao;
import com.example.agilemarket.models.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaCotacaoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaCotacaoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<Product> listaCotacao;
    private ProgressBar progressBar;
    private ImageButton btnOpcoes;
    private Dialog dialogOptions;
    private AdapterListaCotacao adapter;
    private DatabaseReference firebaseRef;
    private DatabaseReference listaCotacaoRef;
    private ValueEventListener valueEventListenerCotacao;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public ListaCotacaoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaCotacaoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaCotacaoFragment newInstance(String param1, String param2) {
        ListaCotacaoFragment fragment = new ListaCotacaoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_cotacao, container, false);

        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        recyclerView = view.findViewById( R.id.recyclerListaCotacao );
        progressBar  = view.findViewById( R.id.progressBarListaCotacao );
        btnOpcoes    = view.findViewById( R.id.imageButtonOptions );

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        listaCotacao = new ArrayList<>();
        configRecycler();

        btnOpcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        populatePriceList();
    }

    @Override
    public void onStop() {
        super.onStop();
        listaCotacaoRef.removeEventListener( valueEventListenerCotacao );
    }

    private void configRecycler(){
        adapter = new AdapterListaCotacao( listaCotacao );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ));
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );
    }

    private void populatePriceList(){

        String idUsuario = UsuarioFirebase.getIdUsuario();
        Log.i("User", "id: " + idUsuario);

        listaCotacaoRef = firebaseRef.child("listaCotacao")
                .child( idUsuario )
                .child("produtos");

        progressBar.setVisibility(View.VISIBLE);
        valueEventListenerCotacao = listaCotacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaCotacao.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Product prod = ds.getValue(Product.class);
                    listaCotacao.add( prod );

                    Log.i("lista", "cotacao: " + listaCotacao.get(0));
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(View v){

        dialogOptions = new Dialog( getContext() );

        dialogOptions.setContentView( R.layout.dialog_options_lista_cotacao );

        dialogOptions.show();
        dialogOptions.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptions.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogOptions.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout btnLimparLista  = dialogOptions.findViewById( R.id.linearLayoutLimparLista );
        LinearLayout btnCompartilhar = dialogOptions.findViewById( R.id.linearLayoutCompartilharLista );

        btnLimparLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!listaCotacao.isEmpty() || !listaCotacao.equals( null )){
                    FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
                    String idUsuario = user.getUid();

                    DatabaseReference cotacaoRef = firebaseRef.child("listaCotacao").child( idUsuario );
                    cotacaoRef.removeValue();

                    listaCotacao.clear();
                    adapter.notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(view, "Sua lista agora está limpa!", Snackbar.LENGTH_SHORT);

                    snackbar.setDuration(1000);
                    snackbar.setBackgroundTint(Color.DKGRAY);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    dialogOptions.dismiss();
                }else{
                    //Toast.makeText(getContext(), "A lista já está vazia!", Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar.make(view, "A lista já está vazia!", Snackbar.LENGTH_SHORT);

                    snackbar.setDuration(1000);
                    snackbar.setBackgroundTint(Color.DKGRAY);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    dialogOptions.dismiss();
                }

            }
        });

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlanExcel();
                dialogOptions.dismiss();
            }
        });

    }

    private void createPlanExcel(){

        String nomeUsuario = UsuarioFirebase.getUsuarioAtual().getDisplayName();

        nomeUsuario = nomeUsuario.toLowerCase(Locale.ROOT);
        nomeUsuario = nomeUsuario.replaceAll(" ", "");

        String dataEnvio = DateCustom.dataAtual();
        String nomeArquivo = nomeUsuario + dataEnvio + ".xlsx";

        Log.i("Arquivo", "Nome: " + nomeArquivo);

        try {

            HSSFWorkbook workbook = new HSSFWorkbook(); // criando ambiente de trabalho

            HSSFSheet sheet = workbook.createSheet(); // criando planilha

            // inserindo dados na planilha

            int rownum = 0;
            for (Product prod : listaCotacao){
                Row row =  sheet.createRow(rownum++);
                int cellnum = 0;
                Cell cellDescricao = row.createCell( cellnum++ );
                cellDescricao.setCellValue( prod.getDesc() );
            }


            File diretory = getActivity().getExternalFilesDir(null);
            File file = new File( diretory, nomeArquivo );

            if( !file.exists() ){
                file.createNewFile();
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream( file );
                workbook.write( fileOutputStream );

                if (fileOutputStream != null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }

                Log.i("Workbook", "Value: " + workbook);

                Log.i("TAG", "Location: " + diretory + nomeArquivo);

                Snackbar snackbar = Snackbar.make(getView(), "Planilha gerada com sucesso!", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(Color.WHITE);
                snackbar.setBackgroundTint(Color.DKGRAY);
                snackbar.show();

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (FileNotFoundException e) {
            Log.i("Arquivo não encontrado: ", nomeArquivo);
        } catch (IOException e) {
            Log.i("Erro ao processar o arquivo: ", nomeArquivo);
        }
    }

    private void readPlanExcel(){

    }

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (!result){
                showAlertPermission();
            }
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showAlertPermission(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissões negadas!");
        builder.setMessage("Para gerar a planilha é necessário aceitar a permissão.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}