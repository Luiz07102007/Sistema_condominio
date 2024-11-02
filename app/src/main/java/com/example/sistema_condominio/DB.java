package com.example.sistema_condominio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "estacionamento.db";
    private static final int DATABASE_VERSION = 1;

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableProprietario = "CREATE TABLE Proprietario (" +
                "id_proprietario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, " +
                "CPF TEXT UNIQUE, " +
                "email TEXT, " +
                "senha TEXT, " +
                "fk_vaga INTEGER, " +
                "FOREIGN KEY (fk_vaga) REFERENCES Vaga(id_vaga)" +
                ");";


        String createTableVeiculo = "CREATE TABLE Veiculo (" +
                "id_veiculo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "placa TEXT UNIQUE, " +
                "mensalidade REAL, " +
                "ano INTEGER, " +
                "fk_proprietario INTEGER, " +
                "FOREIGN KEY (fk_proprietario) REFERENCES Proprietario(id_proprietario)" +
                ");";

        String createTableVaga = "CREATE TABLE Vaga (" +
                "id_vaga INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero_vaga INTEGER UNIQUE, " +
                "mensalidade REAL" +
                ");";


        db.execSQL(createTableProprietario);
        db.execSQL(createTableVeiculo);
        db.execSQL(createTableVaga);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Proprietario");
        db.execSQL("DROP TABLE IF EXISTS Veiculo");
        db.execSQL("DROP TABLE IF EXISTS Vaga");
        onCreate(db);
    }

}
