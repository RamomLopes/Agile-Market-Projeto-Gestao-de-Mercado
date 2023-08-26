package com.example.agilemarket.ui.data.listas;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.agilemarket.R;
import com.example.agilemarket.fragments.ListaCotacaoFragment;
import com.example.agilemarket.fragments.ListaGeralFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ConstraintLayout btnListaGeral, btnListaCotacao;

    private ListaGeralFragment listaGeralFragment;
    private ListaCotacaoFragment listaCotacaoFragment;

    public ListasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListasFragment newInstance(String param1, String param2) {
        ListasFragment fragment = new ListasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listas, container, false);

        btnListaGeral   = view.findViewById( R.id.panelBtnListaProduto );
        btnListaCotacao = view.findViewById( R.id.panelBtnListaCotacao );

        btnListaGeral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaGeralFragment = new ListaGeralFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.nav_host_fragment_activity_bottom_menu, listaGeralFragment );
                transaction.commit();
            }
        });

        btnListaCotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaCotacaoFragment = new ListaCotacaoFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.nav_host_fragment_activity_bottom_menu, listaCotacaoFragment );
                transaction.commit();
            }
        });

        return view;
    }
}