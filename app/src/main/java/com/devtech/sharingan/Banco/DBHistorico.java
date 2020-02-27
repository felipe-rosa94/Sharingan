package com.devtech.sharingan.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.devtech.sharingan.Model.Historico;

import java.util.ArrayList;

public class DBHistorico {

    private SQLiteDatabase database;
    private ItemDAO dbHelper;

    public DBHistorico(Context context) {
        dbHelper = new ItemDAO(context);
    }

    public void abrirBanco() {
        database = dbHelper.getWritableDatabase();
    }

    public void fecharBanco() {
        dbHelper.close();
    }

    public long inserir(Historico h) {
        ContentValues cv = new ContentValues();
        cv.put(ItemDAO._ID, h.getId());
        cv.put(ItemDAO._TEXTO, h.getTexto());
        cv.put(ItemDAO._DATA, h.getData());
        return database.insert(ItemDAO.TBL_HISTORICO, null, cv);
    }

    public long alterar(Historico h) {
        long id = h.getId();
        ContentValues cv = new ContentValues();
        cv.put(ItemDAO._TEXTO, h.getTexto());
        cv.put(ItemDAO._DATA, h.getData());
        return database.update(ItemDAO.TBL_HISTORICO,
                cv,
                ItemDAO._ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public int excluir(long id) {
        return database.delete(ItemDAO.TBL_HISTORICO,
                ItemDAO._ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public boolean deletar() {
        return database.delete(ItemDAO.TBL_HISTORICO, null, null) > 0;
    }

    public ArrayList<Historico> consultar() {

        ArrayList<Historico> prodAux = new ArrayList<>();

        Cursor cursor = database.query(
                ItemDAO.TBL_HISTORICO,
                ItemDAO.TBL_COMANDA_COLUNAS,
                null,
                null,
                null,
                null,
                ItemDAO._ID); //order by

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Historico h = new Historico();
            h.setId(cursor.getInt(0));
            h.setTexto(cursor.getString(1));
            h.setData(cursor.getString(2));
            cursor.moveToNext();
            prodAux.add(h);
        }

        cursor.close();
        return prodAux;
    }

    public ArrayList<Historico> filtrar(String query) {

        ArrayList<Historico> cliAux = new ArrayList<>();

        Cursor cursor = database.query(
                ItemDAO.TBL_HISTORICO,
                ItemDAO.TBL_COMANDA_COLUNAS,
                query,
                null,
                null,
                null,
                ItemDAO._ID
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Historico h = new Historico();
            h.setId(cursor.getInt(0));
            h.setTexto(cursor.getString(1));
            h.setData(cursor.getString(2));
            cursor.moveToNext();
            cliAux.add(h);
        }
        cursor.close();
        return cliAux;
    }

    private static class ItemDAO extends SQLiteOpenHelper {

        public static final String TBL_HISTORICO = "historico";
        public static final String _ID = "id";
        public static final String _TEXTO = "hora";
        public static final String _DATA = "pedido";

        public static final String[] TBL_COMANDA_COLUNAS = {
                ItemDAO._ID,
                ItemDAO._TEXTO,
                ItemDAO._DATA};

        public static final String DATABASE_NAME = "historico.sqlite";
        public static final int DATABASE_VERSION = 1;

        public static final String CREATE_COMADA =
                "create table " + TBL_HISTORICO + "(" +
                        _ID + " integer primary key , " +
                        _TEXTO + " text," +
                        _DATA + " text );";

        public static final String DROP_PRODUTO =
                "drop table if exists " + TBL_HISTORICO;

        public ItemDAO(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_COMADA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database", "Atualizando da versão " + oldVersion
                    + " para "
                    + newVersion + ". Isto destruirá todos os dados.");
            db.execSQL("DROP TABLE IF EXISTS " + TBL_HISTORICO + ";");
            onCreate(db);
        }
    }
}
