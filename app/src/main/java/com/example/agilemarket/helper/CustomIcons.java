package com.example.agilemarket.helper;

import com.example.agilemarket.R;

public class CustomIcons {

    private String desc;
    private int icone;

    public CustomIcons(String desc, int icone) {
        this.desc = desc;
        this.icone = icone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    @Override
    public String toString() {
        return getDesc();
    }

    public static int chooseIcon(String descIcon){
        int icon = 0;

        switch ( descIcon ){

            case "Padaria":
                icon = R.drawable.padaria;
                break;

            case "Carnes":
                icon = R.drawable.carnes;
                break;

            case "Farinhas":
                icon = R.drawable.farinhas;
                break;

            case "Frios e Laticínios":
                icon = R.drawable.frios;
                break;

            case "Bebidas":
                icon = R.drawable.bebidas;
                break;

            case "Utensílios":
                icon = R.drawable.utensilios;
                break;

            case "Limpeza":
                icon = R.drawable.limpeza;
                break;

            case "Higiene":
                icon = R.drawable.higiene;
                break;

            case "Frutas e Verduras":
                icon = R.drawable.hortifruti;
                break;

            case "Papelaria":
                icon = R.drawable.papelaria;
                break;

            case "Brinquedos":
                icon = R.drawable.brinquedos;
                break;

            case "Pet Shop":
                icon = R.drawable.pet_shop;
                break;

        }

        return icon;
    }
}
