package com.example.agilemarket.database;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class ConfiguracaoFirebase {

    private static DatabaseReference referencia;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;

    public static DatabaseReference getFirebase(){

        if( referencia == null ){
            referencia = FirebaseDatabase.getInstance().getReference();
        }
        return referencia;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){

        if( autenticacao == null ){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static StorageReference getFirebaseStorage(){
        if( storage == null ){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

}


