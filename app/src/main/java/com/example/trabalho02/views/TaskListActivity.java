package com.example.trabalho02.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho02.R;
import com.example.trabalho02.database.AppDatabase;
import com.example.trabalho02.databinding.ActivityTaskListBinding;
import com.example.trabalho02.entity.Task;

import java.util.List;

public class TaskListActivity extends AppCompatActivity {


    private ActivityTaskListBinding binding;
    private AppDatabase db;
    private List<Task> tasks;
    private ListView listViewTipos;
    private Intent edtIntent;
    private int idUserLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Thread(() -> {
            db = AppDatabase.getDatabase(getApplicationContext());
            // Atualizar a UI (ListView, etc.) na thread principal usando runOnUiThread
            runOnUiThread(() -> {
                listViewTipos = binding.listTasks;
                Intent intent = getIntent();
                int id = intent.getIntExtra("ID", -1);
                idUserLogado = id;
                new Thread(() -> preencheTasks(idUserLogado)).start();
                Button btnLogout = findViewById(R.id.btnLogout);
                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                binding.btnHomeTask.setOnClickListener(v -> finish());
                binding.btnAdd.setOnClickListener(v -> {
                    Intent createTaskIntent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
                    createTaskIntent.putExtra("ID_USER", (int) id);
                    startActivity(createTaskIntent);
                });
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtIntent = new Intent(this, CreateTaskActivity.class);
        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", -1);
        idUserLogado = id;
        new Thread(() -> preencheTasks(idUserLogado)).start();
    }

    private void preencheTasks(int idUser) {
        tasks = db.taskDao().getUserTasks(idUser);

        runOnUiThread(() -> {
            ArrayAdapter<Task> tiposAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, tasks);
            listViewTipos.setAdapter(tiposAdapter);

            listViewTipos.setOnItemClickListener((parent, view, position, id) -> {
                Task taskSelecionada = tasks.get(position);
                edtIntent.putExtra("TASK_SELECIONADA_ID", (int)taskSelecionada.getId());
                startActivity(edtIntent);
            });
        });
    }
}