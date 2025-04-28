package com.example.soundaware;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundaware.Adapter.Alert;
import com.example.soundaware.Adapter.AlertAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView alertsRecycler;
    AlertAdapter alertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "Corriendo en segundo plano", Toast.LENGTH_LONG).show();

        initializeCards();
    }

    private void initializeCards(){
        alertsRecycler = findViewById(R.id.recycler_view);
        alertsRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<Alert> alertList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            alertList.add(new Alert(i, "22 de abril 2025, 12:44pm", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ipsum dolor sit amet, consectetur adipiscing elit. "));
        }
        alertAdapter = new AlertAdapter(alertList, this);
        alertsRecycler.setAdapter((alertAdapter));
    }
}