package com.example.agilemarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterColab extends RecyclerView.Adapter<AdapterColab.MyViewHolder> {

    private List<User> lista;

    public AdapterColab(List<User> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate( R.layout.formato_lista_colaboradores, parent, false);

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = lista.get( position );

        //holder.foto.setImageResource(  );
        holder.nome.setText( user.getNome() );
        holder.nvlAcesso.setText( user.getNvlAcesso() );

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, nvlAcesso;
        ImageView edit, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById( R.id.imageFotoColab );
            nome = itemView.findViewById( R.id.textNomeColaborador );
            nvlAcesso = itemView.findViewById( R.id.textNvlAcessoColab );

        }
    }
}
