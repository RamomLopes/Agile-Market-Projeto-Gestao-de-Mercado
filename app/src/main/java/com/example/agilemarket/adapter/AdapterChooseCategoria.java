package com.example.agilemarket.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.helper.CustomIcons;

import java.util.List;

public class AdapterChooseCategoria extends RecyclerView.Adapter<AdapterChooseCategoria.MyViewHolder> {

    private List<Category> listaCategory;

    public AdapterChooseCategoria(List<Category> listaCategory) {
        this.listaCategory = listaCategory;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from( parent.getContext()).inflate( R.layout.formato_lista_choose_categoria, parent, false);

        return new MyViewHolder( itemView );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Category cat = listaCategory.get( position );
        holder.textDesc.setText( cat.getDesc() );
        holder.iconeCategoria.setImageResource( CustomIcons.chooseIcon( cat.getIcon() ) );
    }

    @Override
    public int getItemCount() {
        return listaCategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView iconeCategoria;
        private TextView textDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iconeCategoria  = itemView.findViewById( R.id.imgIconeCategoria );
            textDesc        = itemView.findViewById( R.id.textDescChooseCategoria );

        }
    }

}
