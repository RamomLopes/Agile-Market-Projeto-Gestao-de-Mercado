package com.example.agilemarket.models;

import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String nvlAcesso;
    private String empresaVinculada;
    private String caminhoFoto;

    public User() {
    }

    public void save(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        firebaseRef.child("usuarios")
                .child( getId() )
                .setValue( this );
    }

    public void update(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( getId() );

        Map<String, Object> valoresUsuario = convertToMap();

        usuariosRef.updateChildren( valoresUsuario );
    }

    public Map<String, Object> convertToMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("id", getId());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());
        usuarioMap.put("nvlAcesso", getNvlAcesso());

        return usuarioMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNvlAcesso() {
        return nvlAcesso;
    }

    public void setNvlAcesso(String nvlAcesso) {
        this.nvlAcesso = nvlAcesso;
    }

    public String getEmpresaVinculada() {
        return empresaVinculada;
    }

    public void setEmpresaVinculada(String empresaVinculada) {
        this.empresaVinculada = empresaVinculada;
    }
    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    @Override
    public String toString(){
        return getNvlAcesso();
    }


}
