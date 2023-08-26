package com.example.agilemarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.Product;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdapterListaCotacao extends RecyclerView.Adapter<AdapterListaCotacao.MyViewHolder>{

    private List<Product> listaProducts;

    public AdapterListaCotacao(List<Product> products){
        listaProducts = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate( R.layout.formato_lista_cotacao, parent, false);

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product prod = listaProducts.get( position );

        holder.desc.setText( prod.getDesc() );
        holder.btnDelete.setOnClickListener( view -> delete( prod, position ) );

    }

    @Override
    public int getItemCount() {
        return listaProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView desc;
        private ImageButton btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc        = itemView.findViewById( R.id.textDescProdutoRecyclerCotacao );
            btnDelete   = itemView.findViewById( R.id.imageButtonDeleteProdutoListaCotacao );
        }
    }

    private void delete(Product product, int position){

        // remove o produto do firebase
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase()
                .child("listaCotacao")
                .child(idUsuario)
                .child("produtos")
                .child( product.getId() );
        firebaseRef.removeValue();

        // notifica adapter do recycler que o produto foi removido
        listaProducts.remove( position );
        notifyItemRemoved( position );
        notifyItemRangeChanged( position, getItemCount() );
    }

}
