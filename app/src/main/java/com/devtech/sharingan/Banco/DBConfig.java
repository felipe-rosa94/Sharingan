package com.devtech.sharingan.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBConfig {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "config.sqb";

    private static final String DATABASE_TABLE = "config";

    private static final String _ID = "Id";
    private static final String _INTRO = "Intro";

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + _ID + " integer primary key autoincrement, "
                    + _INTRO + " interger "
                    + ");";

    public class Struc {
        public int Id = 1;
        public int Intro = 0;
    }

    public final Struc Fields = new Struc();

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBConfig(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public boolean deletar() {
        return db.delete(DATABASE_TABLE, null, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            db.execSQL("insert into config (Id, Intro) values (1, 0);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database", "Atualizando da versão " + oldVersion
                    + " para "
                    + newVersion + ". Isto destruirá todos os dados.");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
            onCreate(db);
        }
    }

    public void abrirBanco() throws SQLException {
        db = DBHelper.getWritableDatabase();
        getId(1);
    }

    public void fechaBanco() {
        DBHelper.close();
    }

    private ContentValues getArgs() {
        ContentValues args = new ContentValues();
        args.put(_ID, Fields.Id);
        args.put(_INTRO, Fields.Intro);
        return args;
    }

    public void getFields(Cursor c) {
        if (!c.isAfterLast() && !c.isBeforeFirst()) {
            int i = 0;
            Fields.Id = c.getInt(i++);
            Fields.Intro = c.getInt(i++);
        }
    }

    public long inserir() {
        return db.insert(DATABASE_TABLE, null, getArgs());
    }

    public boolean alterar(long Id) {
        return db.update(DATABASE_TABLE, getArgs(), _ID + "=" + Id, null) > 0;
    }

    public boolean excluir(long Id) {
        return db.delete(DATABASE_TABLE, _ID + "=" + Id, null) > 0;
    }

    public Cursor getAll() {
        return getWhere(null);
    }

    public Cursor getId(long Id) throws SQLException {
        Cursor c = getWhere(_ID + "=" + Id);
        return c;
    }

    public Cursor getWhere(String Where) throws SQLException {
        Cursor c =
                db.query(DATABASE_TABLE, new String[]{
                                _ID,
                                _INTRO},
                        Where,
                        null,
                        null,
                        null,
                        _ID);

        if (c != null) {
            c.moveToFirst();
            getFields(c);
        }
        return c;
    }
}
