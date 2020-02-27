package com.devtech.sharingan.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devtech.sharingan.Banco.DBConfig;
import com.devtech.sharingan.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText etTexto;
    private TextView tvAviso;
    private CardView cvTexto, cvImagem;
    private ImageView btnLimpa;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALERIA_REQUEST_CODE = 400;
    private static final int IMAGEM_GALERIA_CODE = 1000;
    private static final int IMAGEM_CAMERA_CODE = 1001;

    private String[] cameraPermissao;
    private String[] galeriaPermissao;
    private String diretorioApp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Sharingan/";

    private Uri imagem_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciar();
        intro();
        acoes();
        permissoes();
        visualizacao(true, "");
    }

    void iniciar() {
        etTexto = findViewById(R.id.et_texto);
        cvImagem = findViewById(R.id.cv_image);
        cvTexto = findViewById(R.id.cv_texto);
        tvAviso = findViewById(R.id.tv_aviso);
        btnLimpa = findViewById(R.id.btn_limpa);
    }

    void acoes() {
        cvImagem.setOnClickListener(v -> dialogSeleciona());
        btnLimpa.setOnClickListener(v -> visualizacao(true, ""));
    }

    void permissoes() {
        cameraPermissao = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        galeriaPermissao = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    void dialogSeleciona() {
        String[] items = {"Câmera", "Galeria"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.selecione_imagem);
        dialog.setItems(items, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    if (!permissaoCamera()) {
                        pedirPermissaoCamera();
                    } else {
                        capturarCamera();
                    }
                    break;
                case 1:
                    if (!permissaoGaleria()) {
                        pedirPermissaoGaleria();
                    } else {
                        capturarGaleria();
                    }
                    break;
            }
        });
        dialog.create().show();
    }

    void visualizacao(boolean b, String s) {
        if (b) {
            btnLimpa.setVisibility(View.GONE);
            cvImagem.setVisibility(View.VISIBLE);
            cvTexto.setVisibility(View.GONE);
            tvAviso.setText(R.string.aviso_selecao);
            etTexto.setText(s);
        } else {
            btnLimpa.setVisibility(View.VISIBLE);
            cvImagem.setVisibility(View.GONE);
            cvTexto.setVisibility(View.VISIBLE);
            tvAviso.setText(R.string.aviso_limpa);
            etTexto.setText(s);
        }
    }

    void intro() {
        DBConfig dbConfig = new DBConfig(this);
        try {
            dbConfig.abrirBanco();
            dbConfig.Fields.Intro = 1;
            dbConfig.alterar(1);
        } catch (Exception e) {
            e.getMessage();
        } finally {
            dbConfig.fechaBanco();
        }
    }

    void capturarGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGEM_GALERIA_CODE);
    }

    void capturarCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        imagem_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagem_uri);
        startActivityForResult(cameraIntent, IMAGEM_CAMERA_CODE);
    }

    void pedirPermissaoGaleria() {
        ActivityCompat.requestPermissions(this, galeriaPermissao, GALERIA_REQUEST_CODE);
    }

    boolean permissaoGaleria() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    void pedirPermissaoCamera() {
        ActivityCompat.requestPermissions(this, cameraPermissao, CAMERA_REQUEST_CODE);
    }

    boolean permissaoCamera() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    void dialogSalvar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_salvar, null, false);
        EditText etNomeArquivo = view.findViewById(R.id.et_nome_arquivo);
        builder.setTitle(R.string.digite_nome_arquivo);
        builder.setView(view)
                .setPositiveButton(R.string.confirmar, (dialogInterface, i) -> {
                    String texto = etTexto.getText().toString();
                    String arquivo = etNomeArquivo.getText().toString().isEmpty() ? dataHora() : etNomeArquivo.getText().toString().trim();
                    salvar(texto, arquivo);
                });
        builder.show();
    }

    void salvar(String texto, String arquivo) {
        try {
            File diretorio = new File(diretorioApp);

            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            File fileExt = new File(diretorioApp, arquivo + ".txt");
            fileExt.getParentFile().mkdirs();
            FileOutputStream fosExt = new FileOutputStream(fileExt, true);

            fosExt.write(texto.getBytes());
            fosExt.close();

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    void compartilhar() {
        copiarAreaTransferencia();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Aqui vai sua mensagem");
        share.putExtra(Intent.EXTRA_TEXT, "*Sharingan*:\n" + etTexto.getText().toString());
        startActivity(Intent.createChooser(share, "Compartilhar"));
    }

    void copiarAreaTransferencia() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(etTexto.getText().toString());
        Toast.makeText(this, R.string.area_tranferencia, Toast.LENGTH_LONG).show();
    }

    String dataHora() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.salvar:
                dialogSalvar();
                break;
            case R.id.compartilhar:
                compartilhar();
                break;
            case R.id.historico:
                Intent it = new Intent(this, HistoricoActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStrorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStrorageAccepted) {
                        capturarCamera();
                    } else {
                        Toast.makeText(this, "Permissão não foi dada.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case GALERIA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStrorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStrorageAccepted) {
                        capturarGaleria();
                    } else {
                        Toast.makeText(this, "Permissão não foi dada.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGEM_GALERIA_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGEM_CAMERA_CODE) {
                CropImage.activity(imagem_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    try {
                        Uri resultUri = result.getUri();

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                        TextRecognizer recognizer = new TextRecognizer.Builder(getBaseContext()).build();

                        if (!recognizer.isOperational()) {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = recognizer.detect(frame);
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < items.size(); i++) {
                                TextBlock myItem = items.valueAt(i);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }

                            visualizacao(false, sb.toString().trim());
                            copiarAreaTransferencia();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception exception = result.getError();
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
