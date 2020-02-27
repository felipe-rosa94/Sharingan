package com.devtech.sharingan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.devtech.sharingan.R;

import java.io.File;
import java.io.FileOutputStream;

public class EditorActivity extends AppCompatActivity {

    private EditText etTexto;
    private String diretorioApp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Sharingan/";
    private String arquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        iniciar();
    }

    private void iniciar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        String texto = getIntent().getStringExtra("texto");
        arquivo = getIntent().getStringExtra("arquivo");
        etTexto = findViewById(R.id.et_editor);
        etTexto.setText(texto);
        etTexto.setEnabled(false);
    }

    void salvar(String texto, String arquivo) {
        try {
            try {
                File file = new File(diretorioApp + arquivo);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File diretorio = new File(diretorioApp);

            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            File fileExt = new File(diretorioApp, arquivo);
            fileExt.getParentFile().mkdirs();
            FileOutputStream fosExt = new FileOutputStream(fileExt, true);

            fosExt.write(texto.getBytes());
            fosExt.close();

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    void deletar(String arquivo) {
        try {
            File file = new File(diretorioApp + arquivo);
            file.delete();
            Toast.makeText(this, "Arquivo " + arquivo + " Foi deletado.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.salvar:
                salvar(etTexto.getText().toString().trim(), arquivo);
                break;
            case R.id.apagar:
                deletar(arquivo);
                break;
            case R.id.editar:
                etTexto.setEnabled(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
