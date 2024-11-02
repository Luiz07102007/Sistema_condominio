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

public class Proprietario extends AppCompatActivity {
    private EditText editNome, editCPF, editEmail, editSenha;
    private Button btnSalvar, btnApagar, btnAtualizar;
    private ListView listViewProprietarios; // ListView para exibir os proprietários
    private ArrayAdapter<String> adapter;
    private ArrayList<Integer> proprietarioIds; // Para armazenar os IDs dos proprietários

    private Spinner spinnerVagas;
    private ArrayList<String> proprietariosList;
    private ArrayList<Integer> vagaIds;
    private DB dbHelper;
    private int selectedProprietarioId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proprietario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar os componentes
        editNome = findViewById(R.id.editNome);
        editCPF = findViewById(R.id.editCPF);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnApagar = findViewById(R.id.btnApagar);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        listViewProprietarios = findViewById(R.id.listViewProprietarios);
        dbHelper = new DB(this);

        proprietariosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proprietariosList);
        listViewProprietarios.setAdapter(adapter);

        // Inicializar os componentes
        editNome = findViewById(R.id.editNome);
        editCPF = findViewById(R.id.editCPF);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        spinnerVagas = findViewById(R.id.spinnerVagas); // Inicializando o Spinner
        btnSalvar = findViewById(R.id.btnSalvar);
        btnApagar = findViewById(R.id.btnApagar);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        listViewProprietarios = findViewById(R.id.listViewProprietarios);
        dbHelper = new DB(this);

        proprietariosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proprietariosList);
        listViewProprietarios.setAdapter(adapter);
        proprietarioIds = new ArrayList<>();
        vagaIds = new ArrayList<>();

        carregarVagas(); // Carregar as vagas
        carregarProprietarios(); // Carregar os proprietários

        btnSalvar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String cpf = editCPF.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();
            int vagaId = vagaIds.get(spinnerVagas.getSelectedItemPosition());

            adicionarProprietario(nome, cpf, email, senha, vagaId);
        });

        btnAtualizar.setOnClickListener(v -> {
            if (selectedProprietarioId != -1) {
                String nome = editNome.getText().toString();
                String cpf = editCPF.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                int vagaId = vagaIds.get(spinnerVagas.getSelectedItemPosition());

                atualizarProprietario(selectedProprietarioId, nome, cpf, email, senha, vagaId);
            } else {
                Toast.makeText(this, "Selecione um proprietário para atualizar", Toast.LENGTH_SHORT).show();
            }
        });

        btnApagar.setOnClickListener(v -> {
            if (selectedProprietarioId != -1) {
                excluirProprietario(selectedProprietarioId);
            } else {
                Toast.makeText(this, "Selecione um proprietário para excluir", Toast.LENGTH_SHORT).show();
            }
        });

        listViewProprietarios.setOnItemClickListener((parent, view, position, id) -> {
            String itemSelecionado = proprietariosList.get(position);
            String[] partes = itemSelecionado.split(": ");
            selectedProprietarioId = Integer.parseInt(partes[0]); // Extrai o ID do proprietário

            // Carregar os dados do proprietário selecionado nos campos
            carregarDadosProprietario(selectedProprietarioId);
        });
    }

    private void carregarVagas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_vaga, numero_vaga FROM Vaga", null);

        vagaIds.clear();
        ArrayList<String> vagasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vaga"));
            String numero = cursor.getString(cursor.getColumnIndexOrThrow("numero_vaga"));

            vagaIds.add(id);
            vagasList.add(numero);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vagasList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVagas.setAdapter(adapter);
    }

    private void carregarProprietarios() {
        proprietariosList.clear(); // Limpa a lista para evitar duplicados
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_proprietario, nome FROM Proprietario", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_proprietario"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            proprietariosList.add(id + ": " + nome); // Adiciona "ID: Nome" na lista
            proprietarioIds.add(id); // Armazena o ID do proprietário
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proprietariosList);
        listViewProprietarios.setAdapter(adapter); // Atualiza o ListView
    }

    private void adicionarProprietario(String nome, String cpf, String email, String senha, int vagaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("CPF", cpf);
        values.put("email", email);
        values.put("senha", senha);
        values.put("fk_vaga", vagaId);

        db.insert("Proprietario", null, values);
        Toast.makeText(this, "Proprietário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarProprietarios(); // Atualiza a lista no ListView
    }

    private void atualizarProprietario(int id, String nome, String cpf, String email, String senha, int vagaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("CPF", cpf);
        values.put("email", email);
        values.put("senha", senha);
        values.put("fk_vaga", vagaId);

        db.update("Proprietario", values, "id_proprietario=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Proprietário atualizado com sucesso", Toast.LENGTH_SHORT).show();
        
        carregarProprietarios(); // Atualiza a lista no ListView
    }

    private void excluirProprietario(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Proprietario", "id_proprietario=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "Proprietário excluído com sucesso", Toast.LENGTH_SHORT).show();
        limparCampos();
        carregarProprietarios(); // Atualiza a lista no ListView
    }

    private void carregarDadosProprietario(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Proprietario WHERE id_proprietario = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            editNome.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
            editCPF.setText(cursor.getString(cursor.getColumnIndexOrThrow("CPF")));
            editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            editSenha.setText(cursor.getString(cursor.getColumnIndexOrThrow("senha")));

            // Selecionar a vaga no spinner
            int vagaId = cursor.getInt(cursor.getColumnIndexOrThrow("fk_vaga"));
            int index = vagaIds.indexOf(vagaId);
            if (index >= 0) {
                spinnerVagas.setSelection(index); // Seleciona a vaga no Spinner
            }
        }
        cursor.close();
    }

    private void limparCampos() {
        editNome.setText("");
        editCPF.setText("");
        editEmail.setText("");
        editSenha.setText("");
        selectedProprietarioId = -1; // Reseta o ID selecionado
    }
}