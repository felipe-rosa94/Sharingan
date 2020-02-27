package com.devtech.sharingan.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.devtech.sharingan.Banco.DBConfig;
import com.devtech.sharingan.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class IntroducaoActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redireciona();
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation((view, percentage) -> view.setAlpha(percentage));

        String[] permissoes = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.ic_content_copy)
                .title("Sharingan")
                .description("O sharingan e um aplicativo que copia textos de uma simples foto, de forma rápida e fácil.")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.blue)
                .neededPermissions(permissoes)
                .image(R.drawable.ic_android)
                .title("Permissões")
                .description("Para avançar precisamos que libera \n as permissões do dispositivo")
                .build());
    }

    void redireciona() {
        DBConfig dbConfig = new DBConfig(this);
        try {
            dbConfig.abrirBanco();
            if (dbConfig.Fields.Intro != 0) {
                finish();
                main();
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            dbConfig.fechaBanco();
        }
    }

    void main() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onFinish() {
        main();
        super.onFinish();
    }
}
