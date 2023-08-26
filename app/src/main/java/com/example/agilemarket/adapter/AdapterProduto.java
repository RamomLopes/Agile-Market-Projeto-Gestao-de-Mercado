package com.example.agilemarket.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.Product;
import com.example.agilemarket.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private List<Product> listaProducts;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference cotacaoRef;
    private DatabaseReference produtoRef;
    private String nvlAcesso;

    public AdapterProduto(List<Product> lista) {
        this.listaProducts = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.formato_lista_geral, parent, false);

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product product = listaProducts.get(position);

        holder.produto.setText( product.getDesc() );
        holder.categoria.setText( product.getCategoria().getDesc() );
        holder.btnAddParaListaCotacao.setOnClickListener( view -> inserirNaListaCotacao(product, holder ) );
        holder.btnDelete.setOnClickListener( view -> delete( holder.itemView, product, position ) );
    }

    @Override
    public int getItemCount() {
        return listaProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView produto;
        private TextView categoria;
        private ImageButton btnAddParaListaCotacao;
        private ImageButton btnDelete;
        private DatabaseReference firebaseRef;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            produto     = itemView.findViewById( R.id.textDescProduto );
            categoria   = itemView.findViewById( R.id.textDescCategoria );
            btnDelete   = itemView.findViewById( R.id.imageButtonDeleteProdutoListaGeral );
            btnAddParaListaCotacao = itemView.findViewById( R.id.imageButtonAddListaCotacao );

            String idUsuario = UsuarioFirebase.getIdUsuario();

            firebaseRef = ConfiguracaoFirebase.getFirebase();

            usuariosRef =  firebaseRef.child("usuarios");
            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()){
                        User user = ds.getValue( User.class );

                        if (user.getId().equals( idUsuario )){
                            nvlAcesso = user.getNvlAcesso();
                        }
                    }

                    Log.i("tag: ", "ACESSO: " + nvlAcesso);

                    if( nvlAcesso != null ){
                        if( nvlAcesso.equals("Básico") ){
                            btnDelete.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void inserirNaListaCotacao(Product product, MyViewHolder view){

        String idUsuario = UsuarioFirebase.getIdUsuario();
        Log.i("Prod", "Produto: " + product.getDesc());

        DatabaseReference listaCotacaoRef = firebaseRef.child("listaCotacao");
        listaCotacaoRef.child( idUsuario )
                .child("produtos")
                .child( product.getId() )
                .setValue(product);

        Snackbar snackbar = Snackbar.make(view.itemView, "Produto adicionado na sua lista de cotação!", Snackbar.LENGTH_SHORT);
        snackbar.setDuration(500);
        snackbar.setTextColor(Color.WHITE);
        snackbar.setBackgroundTint(Color.DKGRAY);
        snackbar.show();
    }

    private void delete(View view, Product prod, int position){

        Dialog dialog = new Dialog( view.getContext() );

        dialog.setContentView( R.layout.dialog_confirm_delete_object );

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        Button btnConfirmar = dialog.findViewById( R.id.btnConfirmacaoDialogDeletarProduto );
        Button btnCancelar  = dialog.findViewById( R.id.btnCancelarDialogDeletarProduto );
        TextView titulo = dialog.findViewById( R.id.textTituloDeletar );
        TextView desc = dialog.findViewById( R.id.textDescDeleteObject );

        titulo.setText("Deletar Produto");
        desc.setText( R.string.msg_deletar_produto );

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removerProdutoListaCotacao( prod );

                listaProducts.remove( position );
                notifyItemRemoved( position );
                notifyItemRangeChanged( position, getItemCount() );

                dialog.dismiss();

                Snackbar snackbar = Snackbar.make( view, "Produto excluído!", Snackbar.LENGTH_SHORT);
                snackbar.setDuration(500);
                snackbar.setTextColor(Color.WHITE);
                snackbar.setBackgroundTint(Color.DKGRAY);
                snackbar.show();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void removerProdutoListaCotacao(Product product){

        produtoRef = firebaseRef.child("produtos")
                .child( product.getId() );
        produtoRef.removeValue();

        String idUsuario = UsuarioFirebase.getIdUsuario();

        cotacaoRef = firebaseRef.child("listaCotacao");
        cotacaoRef.child( idUsuario )
                .child("produtos")
                .child( product.getId() );
        cotacaoRef.removeValue();
    }

}
