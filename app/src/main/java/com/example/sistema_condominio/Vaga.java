package com.example.sistema_condominio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Vaga extends AppCompatActivity {
    private EditText editNumeroVaga, editMensalidade;
    private Button btnSalvarVaga, btnAtualizarVaga, btnApagarVaga;
    private ListView listViewVagas;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> vagasList;
    private ArrayList<Integer> vagaIds; // Para armazenar os IDs das vagas
    private DB dbHelper;
    private int selectedVagaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vaga);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editNumeroVaga = findViewById(R.id.editNumeroVaga);
        editMensalidade = findViewById(R.id.editMensalidade);
        btnSalvarVaga = findViewById(R.id.btnSalvarVaga);
        btnAtualizarVaga = findViewById(R.id.btnAtualizarVaga);
        btnApagarVaga = findViewById(R.id.btnApagarVaga);
        listViewVagas = findViewById(R.id.listViewVagas); // Inicializa o ListView

        dbHelper = new DB(this);
        vagasList = new ArrayList<>();
        vagaIds = new ArrayList<>();

        carregarVagas();

        btnSalvarVaga.setOnClickListener(v -> {
            String numeroVaga = editNumeroVaga.getText().toString();
            String mensalidade = editMensalidade.getText().toString();

            adicionarVaga(numeroVaga, mensalidade);
        });

        btnAtualizarVaga.setOnClickListener(v -> {
            if (selectedVagaId != -1) {
                String numeroVaga = editNumeroVaga.getText().toString();
                String mensalidade = editMensalidade.getText().toString();

                atualizarVaga(selectedVagaId, numeroVaga, mensalidade);
            } else {
                Toast.makeText(this, "Selecione uma vaga para atualizar", Toast.LENGTH_SHORT).show();
            }
        });

        btnApagarVaga.setOnClickListener(v -> {
            if (selectedVagaId != -1) {
                excluirVaga(selectedVagaId);
            } else {
                Toast.makeText(this, "Selecione uma vaga para excluir", Toast.LENGTH_SHORT).show();
            }
        });

        listViewVagas.setOnItemClickListener((parent, view, position, id) -> {
            String itemSelecionado = vagasList.get(position);
            String[] partes = itemSelecionado.split(": ");
            selectedVagaId = Integer.parseInt(partes[0]); // Extrai o ID da vaga

            // Carregar os dados da vaga selecionada nos campos
            carregarDadosVaga(selectedVagaId);
        });
    }

    private void adicionarVaga(String numeroVaga, String mensalidade) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("numero_vaga", Integer.parseInt(numeroVaga));
        values.put("mensalidade", Double.parseDouble(mensalidade));

        db.insert("Vaga", null, values);
        Toast.makeText(this, "Vaga cadastrada com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVagas(); // Atualiza a lista no ListView
    }

    private void atualizarVaga(int id, String numeroVaga, String mensalidade) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("numero_vaga", Integer.parseInt(numeroVaga));
        values.put("mensalidade", Double.parseDouble(mensalidade));

        db.update("Vaga", values, "id_vaga=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Vaga atualizada com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVagas(); // Atualiza a lista no ListView
    }

    private void excluirVaga(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Vaga", "id_vaga=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Vaga excluída com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVagas(); // Atualiza a lista no ListView
    }

    private void carregarVagas() {
        vagasList.clear(); // Limpa a lista para evitar duplicados
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_vaga, numero_vaga FROM Vaga", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vaga"));
            int numeroVaga = cursor.getInt(cursor.getColumnIndexOrThrow("numero_vaga"));
            vagasList.add(id + ": " + numeroVaga); // Adiciona "ID: Número da Vaga" na lista
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vagasList);
        listViewVagas.setAdapter(adapter); // Atualiza o ListView
    }

    private void carregarDadosVaga(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Vaga WHERE id_vaga = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            editNumeroVaga.setText(cursor.getString(cursor.getColumnIndexOrThrow("numero_vaga")));
            editMensalidade.setText(cursor.getString(cursor.getColumnIndexOrThrow("mensalidade")));
        }
        cursor.close();
    }

    private void limparCampos() {
        editNumeroVaga.setText("");
        editMensalidade.setText("");
        selectedVagaId = -1; // Reseta o ID da vaga selecionada
    }
}
