package com.example.sistema_condominio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Veiculo extends AppCompatActivity {
    private EditText editPlaca, editAno, editMensalidade;
    private Spinner spinnerProprietarios;
    private Button btnSalvarVeiculo, btnAtualizarVeiculo, btnApagarVeiculo;
    private ListView listViewVeiculos; // ListView para exibir os veículos
    private ArrayAdapter<String> adapter;
    private ArrayList<String> veiculosList;
    private ArrayList<Integer> veiculoIds;
    private ArrayList<Integer> proprietarioIds; // Para armazenar os IDs dos proprietários
    private DB dbHelper;
    private int selectedVeiculoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_veiculo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editPlaca = findViewById(R.id.editPlaca);
        editAno = findViewById(R.id.editAno);
        editMensalidade = findViewById(R.id.editMensalidade);
        spinnerProprietarios = findViewById(R.id.spinnerProprietarios);
        btnSalvarVeiculo = findViewById(R.id.btnSalvarVeiculo);
        btnAtualizarVeiculo = findViewById(R.id.btnAtualizarVeiculo);
        btnApagarVeiculo = findViewById(R.id.btnApagarVeiculo);
        listViewVeiculos = findViewById(R.id.listViewVeiculos); // Inicializa o ListView

        dbHelper = new DB(this);
        veiculosList = new ArrayList<>();
        veiculoIds = new ArrayList<>();
        proprietarioIds = new ArrayList<>();

        carregarProprietarios();
        carregarVeiculos();

        btnSalvarVeiculo.setOnClickListener(v -> {
            String placa = editPlaca.getText().toString();
            String ano = editAno.getText().toString();
            String mensalidade = editMensalidade.getText().toString();
            int proprietarioId = proprietarioIds.get(spinnerProprietarios.getSelectedItemPosition());

            adicionarVeiculo(placa, ano, mensalidade, proprietarioId);
        });

        btnAtualizarVeiculo.setOnClickListener(v -> {
            if (selectedVeiculoId != -1) {
                String placa = editPlaca.getText().toString();
                String ano = editAno.getText().toString();
                String mensalidade = editMensalidade.getText().toString();
                int proprietarioId = proprietarioIds.get(spinnerProprietarios.getSelectedItemPosition());

                atualizarVeiculo(selectedVeiculoId, placa, ano, mensalidade, proprietarioId);
            } else {
                Toast.makeText(this, "Selecione um veículo para atualizar", Toast.LENGTH_SHORT).show();
            }
        });

        btnApagarVeiculo.setOnClickListener(v -> {
            if (selectedVeiculoId != -1) {
                excluirVeiculo(selectedVeiculoId);
            } else {
                Toast.makeText(this, "Selecione um veículo para excluir", Toast.LENGTH_SHORT).show();
            }
        });

        listViewVeiculos.setOnItemClickListener((parent, view, position, id) -> {
            String itemSelecionado = veiculosList.get(position);
            String[] partes = itemSelecionado.split(": ");
            selectedVeiculoId = Integer.parseInt(partes[0]); // Extrai o ID do veículo

            // Carregar os dados do veículo selecionado nos campos
            carregarDadosVeiculo(selectedVeiculoId);
        });
    }

    private void carregarProprietarios() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_proprietario, nome FROM Proprietario", null);

        proprietarioIds.clear();
        ArrayList<String> proprietariosList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_proprietario"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));

            proprietarioIds.add(id);
            proprietariosList.add(nome);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, proprietariosList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProprietarios.setAdapter(adapter);
    }

    private void adicionarVeiculo(String placa, String ano, String mensalidade, int proprietarioId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placa", placa);
        values.put("ano", ano);
        values.put("mensalidade", mensalidade);
        values.put("fk_proprietario", proprietarioId);

        db.insert("Veiculo", null, values);
        Toast.makeText(this, "Veículo cadastrado com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVeiculos(); // Atualiza a lista no ListView
    }

    private void atualizarVeiculo(int id, String placa, String ano, String mensalidade, int proprietarioId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placa", placa);
        values.put("ano", ano);
        values.put("mensalidade", mensalidade);
        values.put("fk_proprietario", proprietarioId);

        db.update("Veiculo", values, "id_veiculo=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Veículo atualizado com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVeiculos(); // Atualiza a lista no ListView
    }

    private void excluirVeiculo(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Veiculo", "id_veiculo=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Veículo excluído com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarVeiculos(); // Atualiza a lista no ListView
    }

    private void carregarVeiculos() {
        veiculosList.clear(); // Limpa a lista para evitar duplicados
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_veiculo, placa FROM Veiculo", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_veiculo"));
            String placa = cursor.getString(cursor.getColumnIndexOrThrow("placa"));
            veiculosList.add(id + ": " + placa); // Adiciona "ID: Placa" na lista
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, veiculosList);
        listViewVeiculos.setAdapter(adapter); // Atualiza o ListView
    }

    private void carregarDadosVeiculo(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Veiculo WHERE id_veiculo = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            editPlaca.setText(cursor.getString(cursor.getColumnIndexOrThrow("placa")));
            editAno.setText(cursor.getString(cursor.getColumnIndexOrThrow("ano")));
            editMensalidade.setText(cursor.getString(cursor.getColumnIndexOrThrow("mensalidade")));

            // Selecionar o proprietário no spinner
            int proprietarioId = cursor.getInt(cursor.getColumnIndexOrThrow("fk_proprietario"));
            int index = proprietarioIds.indexOf(proprietarioId);
            if (index >= 0) {
                spinnerProprietarios.setSelection(index);
            }
        }
        cursor.close();
    }


    private void limparCampos() {
        editPlaca.setText("");
        editAno.setText("");
        editMensalidade.setText("");
        selectedVeiculoId = -1; // Resetar o ID selecionado
    }
}
