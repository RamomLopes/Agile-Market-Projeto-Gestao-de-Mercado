package com.example.agilemarket.helper;


import androidx.annotation.ColorInt;

public class CustomColors {

    private String desc;
    private int color;

    public CustomColors(String desc, int color) {
        this.desc = desc;
        this.color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return getDesc();
    }

    public static @ColorInt int chooseColor(String color){

        int chooseColor = 0;

        @ColorInt final int abobora = 0xFFFF7200;
        @ColorInt final int acafrao = 0xFFD39902;
        @ColorInt final int amarelo = 0xFFFFFF00;
        @ColorInt final int ameixa = 0xFF603652;
        @ColorInt final int cinza = 0xFF484D50;
        @ColorInt final int coral = 0xFFFF7F50;
        @ColorInt final int vermelho = 0xFF7B0000;
        @ColorInt final int azulMarinho = 0xFF274360;
        @ColorInt final int azulClaro = 0xFFADD8E6;
        @ColorInt final int rosa = 0xFFFF0084;
        @ColorInt final int rosaClaro = 0xFFFFB6C0;
        @ColorInt final int roxo = 0xFF5E4D85;
        @ColorInt final int verde = 0xFF008000;
        @ColorInt final int verdeClaro = 0xFF90EE90;
        @ColorInt final int marrom = 0xFF4B2F28;

        switch (color){

            case "Abóbora":
                chooseColor = abobora;
                break;

            case "Açafrão":
                chooseColor = acafrao;
                break;

            case "Amarelo":
                chooseColor = amarelo;
                break;

            case "Ameixa":
                chooseColor = ameixa;
                break;

            case "Cinza":
                chooseColor = cinza;
                break;

            case "Coral":
                chooseColor = coral;
                break;

            case "Vermelho":
                chooseColor = vermelho;
                break;

            case "Azul marinho":
                chooseColor = azulMarinho;
                break;

            case "Azul claro":
                chooseColor = azulClaro;
                break;

            case "Rosa":
                chooseColor = rosa;
                break;

            case "Rosa claro":
                chooseColor = rosaClaro;
                break;

            case "Roxo":
                chooseColor = roxo;
                break;

            case "Verde":
                chooseColor = verde;
                break;

            case "Verde claro":
                chooseColor = verdeClaro;
                break;

            case "Marrom":
                chooseColor = marrom;
                break;

        }

        return chooseColor;
    }
}
