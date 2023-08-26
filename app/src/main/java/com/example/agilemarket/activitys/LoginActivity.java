package com.example.agilemarket.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agilemarket.R;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.helper.Base64Custom;
import com.example.agilemarket.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView btnRegister;
    private EditText editLogin;
    private EditText editSenha;
    private ProgressBar progressBar;

    private FirebaseAuth autenticacao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        verificarUsuarioLogado();

        inicializarComponentes();

        progressBar.setVisibility( View.GONE );

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = new User();

                user.setEmail( editLogin.getText().toString().trim() );
                user.setSenha( Base64Custom.codificarBase64( editSenha.getText().toString().trim() ));

                validarLogin( view );

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getApplicationContext(), CadastroColabActivity.class ));
                finish();
            }
        });

    }

    public void inicializarComponentes(){
        btnLogin    = findViewById( R.id.btnLogin );
        btnRegister = findViewById( R.id.textBtnGoScreenRegister );
        editLogin   = findViewById( R.id.editTextUsuario );
        editSenha   = findViewById( R.id.editTextSenha );
        progressBar = findViewById( R.id.progressBarLogin );
    }

    // verifica se possui usuario logado, se sim, carrega a tela principal
    public void verificarUsuarioLogado(){
        if( autenticacao.getCurrentUser() != null ){
            startActivity( new Intent(getApplicationContext(), BottomMenuActivity.class) );
            finish();
        }
    }

    public void validarLogin(View view){

        if( editLogin.getText().toString().isEmpty() || editSenha.getText().toString().isEmpty() ) {
            Toast.makeText(getApplicationContext(), "Campo(s) em branco", Toast.LENGTH_SHORT).show();
        }else{
            progressBar.setVisibility( View.VISIBLE );

            autenticacao.signInWithEmailAndPassword(
                    user.getEmail(),
                    user.getSenha()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if( task.isSuccessful() ){

                        Snackbar snackbar = Snackbar.make(view, "Usuário autenticado!", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();

                        Handler handler = new Handler();

                        // tarefa postergada por 5000 milissegundos
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                startActivity( new Intent( getApplicationContext(), BottomMenuActivity.class ));
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 1000);

                    }else{
                        Snackbar snackbar = Snackbar.make(view, "Erro ao autenticar usuário!", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint( Color.WHITE );
                        snackbar.setTextColor( Color.BLACK );
                        snackbar.show();

                        //Toast.makeText( getApplicationContext(), "Erro ao autenticar usuario!", Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility(View.GONE);

                    }
                }
            });
        }

    }

}