package com.example.agilemarket.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class CadastroEmpresaActivity extends AppCompatActivity {

    private ConstraintLayout btnVoltar;
    private EditText editNomeEmpresa;
    private EditText editSenhaEmpresa;
    private EditText editEmailEmpresa;
    private Button btnCadastrar;
    private ProgressBar progressBar;

    private FirebaseAuth autenticacao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_empresa);

        iniciarComponentes();

        progressBar.setVisibility(View.GONE);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CadastroColabActivity.class));
                finish();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome     = editNomeEmpresa.getText().toString().trim();
                String senha    = editSenhaEmpresa.getText().toString().trim();
                String email    = editEmailEmpresa.getText().toString().trim();

                // se os campos não estão em branco então crio um novo objeto usuario
                if( validaCampos(nome, email, senha) ){
                    user = new User();

                    user.setNome( nome );
                    user.setEmail( email );
                    user.setSenha( Base64Custom.codificarBase64( senha ) );
                    user.setNvlAcesso( "Admin" );
                    user.setEmpresaVinculada( "" );
                    user.setCaminhoFoto( "" );

                    cadastrarEmpresa( v );
                }

            }
        });

    }

    // verifica se os campos estão em branco
    private boolean validaCampos(String nome, String email, String senha){
        if( nome.isEmpty() || email.isEmpty() || senha.isEmpty() ){
            Toast.makeText(getApplicationContext(), "Campo(s) em branco!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void cadastrarEmpresa( View view ){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    progressBar.setVisibility(View.VISIBLE);

                    // salvar dados no firebase
                    String idEmpresa = task.getResult().getUser().getUid();
                    user.setId( idEmpresa );
                    user.save();

                    // salvar dados no profile do firebase
                    UsuarioFirebase.atualizarNomeUsuario( user.getNome() );

                    // deslogo a conta, para que não entre direto assim que mudar para a tela de login
                    autenticacao.signOut();

                    limparCampos();

                    Snackbar snackbar = Snackbar.make( view, "Empresa cadastrada com sucesso!", Snackbar.LENGTH_LONG );
                    snackbar.setBackgroundTint( Color.WHITE );
                    snackbar.setTextColor( Color.BLACK );
                    snackbar.show();

                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), CadastroColabActivity.class));
                            finish();
                        }
                    }, 1000);

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

                    //Toast.makeText( view.getContext(), erro, Toast.LENGTH_LONG ).show();
                    Log.i( "", "========================= ERROR ============================" + erro);

                    Snackbar snackbar = Snackbar.make( view, erro, Snackbar.LENGTH_LONG );
                    snackbar.setBackgroundTint( Color.WHITE );
                    snackbar.setTextColor( Color.BLACK );
                    snackbar.show();
                }
            }
        });

    }

    private void iniciarComponentes(){
        btnVoltar           = findViewById( R.id.layoutBtnVoltarTelaCadastroUser );
        btnCadastrar        = findViewById( R.id.btnCadastroEmpresa );
        editNomeEmpresa     = findViewById( R.id.editTextNomeEmpresa );
        editSenhaEmpresa    = findViewById( R.id.editTextSenhaEmpresa );
        editEmailEmpresa    = findViewById( R.id.editTextEmailEmpresa );
        progressBar         = findViewById( R.id.progressBarTelaCadastroEmpresa );
    }

    private void limparCampos(){
        editNomeEmpresa.getText().clear();
        editEmailEmpresa.getText().clear();
        editSenhaEmpresa.getText().clear();
    }
}