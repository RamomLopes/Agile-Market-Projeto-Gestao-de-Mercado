package com.example.agilemarket.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

        long data = System.currentTimeMillis();

        SimpleDateFormat dia = new SimpleDateFormat("dd");
        SimpleDateFormat mes = new SimpleDateFormat("MM");
        SimpleDateFormat ano = new SimpleDateFormat("yyyy");

        String diaString = dia.format( data );
        String mesString = mes.format( data );
        String anoString = ano.format( data );

        return diaString + mesString + anoString;
    }

    public static String horarioAtual(){

        long time = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String timeString = simpleDateFormat.format( time );

        return timeString;
    }
}
