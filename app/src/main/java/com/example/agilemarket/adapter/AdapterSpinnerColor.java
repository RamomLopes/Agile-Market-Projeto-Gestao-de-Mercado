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
import com.example.agilemarket.helper.CustomColors;

import java.util.List;

public class AdapterSpinnerColor extends ArrayAdapter<CustomColors> {

    public AdapterSpinnerColor(@NonNull Context context, int resource, List<CustomColors> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from( parent.getContext() ).inflate(R.layout.formato_spinner_cor, parent, false);
        }
        return view( convertView, position );
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from( parent.getContext() ).inflate(R.layout.formato_spinner_cor, parent, false);
        }
        return view( convertView, position );
    }

    public View view(@NonNull View view, int position){

        CustomColors item = getItem(position);

        ImageView imageColor = view.findViewById( R.id.imageColor );
        TextView textColorSpinner = view.findViewById(R.id.textColor);
        //TextInputLayout spinnerLayoutColor = view.findViewById( R.id.inputLayoutSpinnerColor );

        imageColor.getDrawable().setTint( item.getColor() );
        textColorSpinner.setText( item.getDesc() );

        return view;
    }


}
