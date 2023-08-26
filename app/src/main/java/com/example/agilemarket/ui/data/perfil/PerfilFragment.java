package com.example.agilemarket.ui.data.perfil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agilemarket.AreaAdminFragment;
import com.example.agilemarket.R;
import com.example.agilemarket.activitys.LoginActivity;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.helper.Base64Custom;
import com.example.agilemarket.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnLogout;
    private CircleImageView fotoPerfil;
    private TextView btnAlterarFoto;
    private ImageView btnAreaAdmin;
    private ConstraintLayout layoutConteudoAdmin, layoutConteudoPerfil;

    private TextView textNome;
    private TextView textEmail;
    private TextView textAcesso;

    private FirebaseAuth autenticacaoRef;
    private StorageReference storageRef;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerPerfil;
    private String idUsuario;
    private User userLogado;

    private static final int SELECAO_GALERIA = 200;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener( valueEventListenerPerfil );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        iniciarComponentes(view);
        layoutConteudoAdmin.setVisibility(View.GONE);
        layoutConteudoPerfil.setVisibility(View.VISIBLE);

        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuario = UsuarioFirebase.getIdUsuario();
        userLogado = UsuarioFirebase.getDadosUsuarioLogado();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        // recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        String emailUserAtual = Base64Custom.codificarBase64( usuarioPerfil.getEmail() );

        textNome.setText( usuarioPerfil.getDisplayName() );
        textEmail.setText( usuarioPerfil.getEmail() );

        Uri url = usuarioPerfil.getPhotoUrl();
        if( url != null ){
            Glide.with( getContext() )
                    .load( url )
                    .into( fotoPerfil );
        }else{
            fotoPerfil.setImageResource( R.drawable.bg_market );
        }

        List<User> users = new ArrayList<>();

        usuariosRef =  firebaseRef.child("usuarios");
        valueEventListenerPerfil = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue( User.class );
                    users.add( user );
                }

                for (User user: users){
                    String emailUserPesquisa = Base64Custom.codificarBase64( user.getEmail() );

                    if(emailUserAtual.equals(emailUserPesquisa)){
                        textAcesso.setText( user.getNvlAcesso() );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLogout();
            }
        });

        btnAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getContext().getPackageManager()) != null){
                    startActivityForResult( intent, SELECAO_GALERIA );
                }
            }
        });

        btnAreaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutConteudoAdmin.setVisibility(View.VISIBLE);
                layoutConteudoPerfil.setVisibility(View.GONE);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace( R.id.layoutConteudoAdmin, new AreaAdminFragment() );
                transaction.commit();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){
            Bitmap imagem = null;

            try {
                // selecao apenas da galeria
                switch ( requestCode ){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap( getContext().getContentResolver(), localImagemSelecionada );
                        break;
                }

                if ( imagem != null){
                    fotoPerfil.setImageBitmap( imagem );

                    // recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child( idUsuario + ".jpeg" );

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // recuperar local da foto
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario( url );
                                }
                            });

                            Toast.makeText(getContext(), "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url){
        // atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario( url );

        // atualizar foto no firebase
        userLogado.setCaminhoFoto( url.toString() );
        userLogado.update();

        Toast.makeText(getContext(), "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();
    }

    public void iniciarComponentes(View view){
        btnLogout   = view.findViewById( R.id.btnLogout );
        textNome    = view.findViewById( R.id.textPerfilNome );
        textEmail   = view.findViewById( R.id.textPerfilEmail );
        textAcesso  = view.findViewById( R.id.textPerfilAcesso );
        fotoPerfil  = view.findViewById( R.id.imageFotoColab);
        btnAlterarFoto = view.findViewById( R.id.btnAlterarFotoPerfil );
        btnAreaAdmin = view.findViewById( R.id.btnAreaAdmin );
        layoutConteudoPerfil = view.findViewById( R.id.layoutConteudoPerfil);
        layoutConteudoAdmin = view.findViewById( R.id.layoutConteudoAdmin );
    }

    public void showDialogLogout(){

        // impedir que abra mais de uma dialog
        btnLogout.setEnabled(false);

        Dialog dialog = new Dialog( getContext() );

        dialog.setContentView( R.layout.dialog_logout );

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCancelable(false);

        Button btnCancelar     = dialog.findViewById( R.id.btnCancelarDialogLogout );
        Button btnConfirmar    = dialog.findViewById( R.id.btnConfirmarDialogLogout );

        autenticacaoRef = ConfiguracaoFirebase.getFirebaseAutenticacao();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                btnLogout.setEnabled(true);
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    autenticacaoRef.signOut();
                }catch (Exception e){
                    e.printStackTrace();
                }

                startActivity(new Intent(getContext(), LoginActivity.class));
                dialog.dismiss();
                PerfilFragment.this.getActivity().finish();
            }
        });

    }
}