package com.example.agilemarket.models;

import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Category {

    private String id;
    private String desc;
    private String icone;

    public Category() {
    }

    public void save(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference categoriaRef = firebaseRef
                .child("categorias")
                .child( getId() );
        categoriaRef.setValue( this );
    }

    public void remove(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference categoriaRef = firebaseRef
                .child("categorias")
                .child( getId() );
        categoriaRef.removeValue();
    }

    public void update(){

    }

    public Category(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc.toUpperCase();
    }

    @Override
    public String toString(){
        return getDesc();
    }

    public String getIcon() {
        return icone;
    }

    public void setIcon(String icon) {
        this.icone = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
