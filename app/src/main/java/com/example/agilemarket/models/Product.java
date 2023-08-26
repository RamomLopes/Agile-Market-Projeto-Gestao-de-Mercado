package com.example.agilemarket.models;

import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private String id;
    private String desc;
    private String descPesquisa;
    //private int qtd;
    private Category category;
    private static List<Product> products = new ArrayList<>();

    public Product() {
    }

    public void save(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child( getId() );
        produtoRef.setValue( this );

    }

    public Product(String desc, Category category) {
        this.desc = desc;
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Category getCategoria() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategoria(Category category) {
        this.category = category;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa.toUpperCase();
    }

}
