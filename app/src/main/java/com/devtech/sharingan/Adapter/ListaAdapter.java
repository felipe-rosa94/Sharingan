package com.devtech.sharingan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.devtech.sharingan.R;

import java.util.ArrayList;

public class ListaAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<String> arquivos;
    public LayoutInflater inflater;

    public ListaAdapter(Context context, ArrayList<String> arquivos) {
        this.context = context;
        this.arquivos = arquivos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arquivos.size();
    }

    @Override
    public String getItem(int i) {
        return arquivos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.layout_historico, null);
        }
        TextView tvHistorico = view.findViewById(R.id.tv_historico);
        tvHistorico.setText(arquivos.get(i));
        return view;
    }
}
