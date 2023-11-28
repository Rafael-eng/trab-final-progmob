package com.example.trabalho02.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.trabalho02.R;
import com.example.trabalho02.database.AppDatabase;
import com.example.trabalho02.entity.User;

public class MainActivity extends AppCompatActivity {

    private Button btnCriarUsuario;
    private Button btnVisualizarTarefas;
    private AppDatabase appDatabase;
    private int usuarioLogadoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCriarUsuario = findViewById(R.id.btnCriarUsuario);
        btnVisualizarTarefas = findViewById(R.id.btnVisualizarTarefas);
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        checkUserAdminRole();

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

        btnCriarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateUserActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnVisualizarTarefas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                intent.putExtra("ID", usuarioLogadoId);
                startActivity(intent);
            }
        });
    }

    private void checkUserAdminRole() {
        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                Intent intent = getIntent();
                int id = intent.getIntExtra("ID", -1);
                User loggedInUser = appDatabase.userDao().findUserById(id);
                usuarioLogadoId = (int) loggedInUser.getId();
                return loggedInUser != null && loggedInUser.getRole().equals("admin");
            }

            @Override
            protected void onPostExecute(Boolean isAdmin) {
                if (isAdmin) {
                    btnCriarUsuario.setVisibility(View.VISIBLE);
                } else {
                    btnCriarUsuario.setVisibility(View.GONE);
                }
            }
        }.execute();
    }
}