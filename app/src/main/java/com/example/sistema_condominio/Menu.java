package com.example.sistema_condominio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Menu extends AppCompatActivity {
    Button btvaga,btprop,btveiculo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        btvaga = findViewById(R.id.button2);
        btprop = findViewById(R.id.button);
        btveiculo = findViewById(R.id.button3);
        btvaga.setOnClickListener(view -> {
                Intent intent = new Intent(Menu.this,
                        Vaga.class);
                startActivity(intent);
            }
);
        btprop.setOnClickListener(view -> {
                    Intent intent = new Intent(Menu.this,
                            Proprietario.class);
                    startActivity(intent);
                }
        );
        btveiculo.setOnClickListener(view ->{
            Intent intent = new Intent(Menu.this,
                    Veiculo.class);
            startActivity(intent);
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}