package com.example.agilemarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.helper.CustomIcons;

import java.util.List;

public class AdapterCategoriaHorizontal extends RecyclerView.Adapter<AdapterCategoriaHorizontal.MyViewHolder> {

    private List<Category> listaCategories;

    public AdapterCategoriaHorizontal(List<Category> lista) {
        this.listaCategories = lista;
    }

    @NonNull
    @Override
    public AdapterCategoriaHorizontal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate( R.layout.formato_lista_categoria_inicio_fragment, parent,false );

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategoriaHorizontal.MyViewHolder holder, int position) {

        Category category = listaCategories.get( position );

        holder.desc.setText( category.getDesc() );
        holder.imageIcon.setImageResource( CustomIcons.chooseIcon( category.getIcon() ) );
    }

    @Override
    public int getItemCount() {
        return listaCategories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView desc;
        private ImageView imageIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc      = itemView.findViewById( R.id.textDescCategoriaRecyclerInicio );
            imageIcon = itemView.findViewById( R.id.imageIconRecyclerInicio );
        }
    }
}
