package com.devtech.sharingan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import com.devtech.sharingan.Adapter.ListaAdapter;
import com.devtech.sharingan.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class HistoricoActivity extends AppCompatActivity {

    private String diretorioApp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Sharingan/";
    private ArrayList<String> arquivos;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        historico();
        iniciar();
    }

    private void iniciar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        lv = findViewById(R.id.lv_historico);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            try {
                String arquivo = arquivos.get(i);
                abriArquivo(arquivo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void lista() {
        try {
            ListaAdapter adapter = new ListaAdapter(this, arquivos);
            lv.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void historico() {
        File dir = new File(diretorioApp);
        File[] filelist = dir.listFiles();
        String[] theNamesOfFiles = new String[filelist.length];
        arquivos = new ArrayList<>();
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            arquivos.add(filelist[i].getName());
        }
        if (arquivos.size() != 0) {
            lista();
        } else {
            Toast.makeText(this, R.string.nao_existe, Toast.LENGTH_SHORT).show();
        }
    }

    void abriArquivo(String arquivo) {
        try {
            File file = new File(diretorioApp, arquivo);
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("texto", text.toString());
            intent.putExtra("arquivo", arquivo);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onResume() {
        historico();
        super.onResume();
    }
}
