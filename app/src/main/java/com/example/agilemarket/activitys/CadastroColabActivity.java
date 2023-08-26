package com.example.agilemarket.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agilemarket.R;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.helper.Base64Custom;
import com.example.agilemarket.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class CadastroColabActivity extends AppCompatActivity {

    private Button btnRegistrar;
    private TextView btnLogin;
    private TextView btnCadEmpresa;
    private ImageView imgBtnCadEmpresa;
    private ProgressBar progressBar;
    private EditText editNome;
    private EditText editUser;
    private EditText editSenha;

    private FirebaseAuth autenticacao;
    private User user;
    private boolean confereDadosEmpresa = false;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_colab);

        iniciarComponentes();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        progressBar.setVisibility(View.GONE);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = editNome.getText().toString().trim();
                String user = editUser.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();

                if( nome.isEmpty() || user.isEmpty() || senha.isEmpty() ){
                    Toast.makeText( getApplicationContext(), "Campo(s) em branco!", Toast.LENGTH_LONG ).show();
                }else{
                    CadastroColabActivity.this.user = new User();

                    CadastroColabActivity.this.user.setNome( nome );
                    CadastroColabActivity.this.user.setEmail( user );
                    CadastroColabActivity.this.user.setSenha( Base64Custom.codificarBase64( senha ) );
                    CadastroColabActivity.this.user.setCaminhoFoto( "" );

                    validarCadastro(view);
                }
            }
        });

        imgBtnCadEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), CadastroEmpresaActivity.class );
                startActivity( intent );
                finish();
            }
        });

        btnCadEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), CadastroEmpresaActivity.class );
                startActivity( intent );
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                startActivity( intent );
                finish();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(usuariosRef != null) {
            usuariosRef.removeEventListener(valueEventListenerUsuarios);
        }
    }

    public void validarCadastro(View view){

        Dialog dialog = new Dialog( CadastroColabActivity.this );

        dialog.setContentView( R.layout.dialog_confirm_cadastro );

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        EditText editEmpresa    = dialog.findViewById( R.id.editTextEmpresaConfirmaCadastro );
        EditText editSenha      = dialog.findViewById( R.id.editTextSenhaConfirmaCadastro );
        RadioButton radioBasico = dialog.findViewById( R.id.radioBtnAcessoBasico );
        RadioButton radioInter  = dialog.findViewById( R.id.radioBtnAcessoIntermediario );
        RadioButton radioAdmin  = dialog.findViewById( R.id.radioBtnAcessoAdmin );
        Button btnConfirmar     = dialog.findViewById( R.id.btnConfirmacaoCadastro );

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailEmpresa     = editEmpresa.getText().toString().trim();
                String senha            = editSenha.getText().toString().trim();

                if(radioAdmin.isChecked()){
                    user.setNvlAcesso( "Admin" );
                }else if (radioBasico.isChecked()){
                    user.setNvlAcesso( "Basico" );
                }else if(radioInter.isChecked()){
                    user.setNvlAcesso( "Intermediario" );
                }

                if( ( emailEmpresa.isEmpty() || senha.isEmpty() ) ||
                    ( !radioBasico.isChecked() && !radioInter.isChecked() && !radioAdmin.isChecked() ) ){
                        Toast.makeText(getApplicationContext(), "Campos em branco!", Toast.LENGTH_SHORT).show();
                }else{

                    usuariosRef = firebaseRef.child("usuarios");
                    valueEventListenerUsuarios = usuariosRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()){
                                User user = ds.getValue(User.class);

                                Log.i("USER", "email: " + user.getEmail() );
                                Log.i("EMAIL", "email: " + emailEmpresa );

                                if( user.getEmail().equals( emailEmpresa ) && user.getSenha().equals( Base64Custom.codificarBase64( senha ) ) ){
                                    CadastroColabActivity.this.user.setEmpresaVinculada(user.getEmail());
                                    confereDadosEmpresa = true;
                                }
                            }

                            if( confereDadosEmpresa ){
                                Log.i("FIREBASE", " ---------- Passou no confere dados empresa -------------");
                                //Toast.makeText(getApplicationContext(), "São iguais", Toast.LENGTH_SHORT).show();
                                cadastrarUsuario( view );
                                limparCampos();
                                dialog.dismiss();

                            }else{
                                Toast.makeText(getApplicationContext(), "Empresa não cadastrada!", Toast.LENGTH_SHORT).show();

                                Log.i("FIREBASE", " ---------- Não passou no confere dados empresa -------------");
                                limparCampos();
                                dialog.dismiss();

                                Handler handler = new Handler();

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar snackbar = Snackbar.make( view, "Erro: Empresa não cadastrada!", Snackbar.LENGTH_LONG );
                                        snackbar.setBackgroundTint( Color.WHITE );
                                        snackbar.setTextColor( Color.BLACK );
                                        snackbar.show();
                                    }
                                }, 500);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

    }

    private void cadastrarUsuario( View view ){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    progressBar.setVisibility(View.VISIBLE);

                    String idUsuario = task.getResult().getUser().getUid();
                    user.setId( idUsuario );
                    user.save();

                    UsuarioFirebase.atualizarNomeUsuario( user.getNome() );

                    Snackbar snackbar = Snackbar.make( view, "Sucesso ao cadastrar usuário!", Snackbar.LENGTH_LONG );
                    snackbar.setBackgroundTint( Color.WHITE );
                    snackbar.setTextColor( Color.BLACK );
                    snackbar.show();

                    autenticacao.signOut();

                    progressBar.setVisibility(View.GONE);

                    Log.i( "", "========================= SUCESSO ============================" );
                }else{
                    String erro;

                    try {
                       throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caracteres";
                    }catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta já foi cadastrada";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }

                    Log.i( "", "========================= ERROR ============================" + erro);

                    Snackbar snackbar = Snackbar.make( view, erro, Snackbar.LENGTH_LONG );
                    snackbar.setBackgroundTint( Color.WHITE );
                    snackbar.setTextColor( Color.BLACK );
                    snackbar.show();
                }
            }
        });

    }

    public void iniciarComponentes(){
        btnRegistrar    = findViewById( R.id.btnRegister );
        btnLogin        = findViewById( R.id.textBtnGoScreenLogin );

        editNome        = findViewById( R.id.editTextNomeRegister );
        editUser        = findViewById( R.id.editTextUserRegister );
        editSenha       = findViewById( R.id.editTextPassRegister );

        btnCadEmpresa       = findViewById( R.id.textScreenCadastroEmpresa );
        imgBtnCadEmpresa    = findViewById( R.id.imageButtonEmpresa );

        progressBar         = findViewById( R.id.progressBarTelaCadastroUsuario );
    }

    public void limparCampos(){
        editNome.getText().clear();
        editUser.getText().clear();
        editSenha.getText().clear();

        editNome.clearFocus();
        editUser.clearFocus();
        editSenha.clearFocus();
    }

}