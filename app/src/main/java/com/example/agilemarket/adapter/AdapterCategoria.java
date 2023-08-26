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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agilemarket.R;
import com.example.agilemarket.database.ConfiguracaoFirebase;
import com.example.agilemarket.database.UsuarioFirebase;
import com.example.agilemarket.models.Category;
import com.example.agilemarket.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> {

    private List<Category> listaCategories;

    public AdapterCategoria(List<Category> lista) {
        this.listaCategories = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate( R.layout.formato_lista_categoria, parent,false );

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Category category = listaCategories.get( position );

        holder.desc.setText( category.getDesc() );
        holder.edit.setOnClickListener( view -> update() );
        holder.delete.setOnClickListener( view -> delete( holder.itemView, category, position ) );
    }

    @Override
    public int getItemCount() {
        return listaCategories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView desc;
        private ImageView edit, delete;
        private String nvlAcesso;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc    = itemView.findViewById( R.id.textDescListaCategoria );
            edit    = itemView.findViewById( R.id.imageEditCategoria );
            delete  = itemView.findViewById( R.id.imageDeleteCategoria );

            String idUsuario = UsuarioFirebase.getIdUsuario();

            DatabaseReference usuarioRef =  ConfiguracaoFirebase.getFirebase().child("usuarios");
            usuarioRef.addValueEventListener(new ValueEventListener() {
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
                        if ( nvlAcesso.equals( "Básico" ) ){
                            delete.setVisibility(View.GONE);
                            edit.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void update(){

    }

    private void delete(View view, Category category, int position){

        Dialog dialog = new Dialog( view.getContext() );

        dialog.setContentView( R.layout.dialog_confirm_delete_object);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        Button btnConfirmar        = dialog.findViewById( R.id.btnConfirmacaoDialogDeletarProduto );
        Button btnCancelar  = dialog.findViewById( R.id.btnCancelarDialogDeletarProduto );
        TextView titulo = dialog.findViewById( R.id.textTituloDeletar );
        TextView desc = dialog.findViewById( R.id.textDescDeleteObject );

        titulo.setText("Deletar Categoria");
        desc.setText( R.string.msg_deletar_categoria );

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.remove();

                listaCategories.remove( position );
                notifyItemRemoved( position );
                notifyItemRangeChanged( position, getItemCount() );

                dialog.dismiss();

                Snackbar snackbar = Snackbar.make( view, "Categoria excluída!", Snackbar.LENGTH_SHORT);
                snackbar.setDuration(1000);
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
}
