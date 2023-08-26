package com.example.agilemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agilemarket.R;
import com.example.agilemarket.helper.CustomIcons;

import java.util.List;

public class AdapterSpinnerIcone extends ArrayAdapter {

    public AdapterSpinnerIcone(@NonNull Context context, int resource, List<CustomIcons> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.formato_spinner_icone, parent, false);
        }

        return view(convertView, position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.formato_spinner_icone, parent, false);
        }

        return view(convertView, position);
    }

    public View view(View view, int position){

        CustomIcons item = (CustomIcons) getItem( position );

        ImageView imageIcone = view.findViewById( R.id.imageIcone );
        TextView textIcone = view.findViewById( R.id.textIcone );

        imageIcone.setImageResource( item.getIcone() );
        textIcone.setText( item.getDesc() );

        return view;
    }
}
