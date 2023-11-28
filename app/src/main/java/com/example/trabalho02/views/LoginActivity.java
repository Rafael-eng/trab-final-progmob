package com.example.trabalho02.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho02.R;
import com.example.trabalho02.SessionManager;
import com.example.trabalho02.database.AppDatabase;
import com.example.trabalho02.entity.User;
import com.example.trabalho02.repository.UserRepository;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    private SessionManager sessionManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        sessionManager = new SessionManager(this);

        userRepository = new UserRepository(getAppDatabase().userDao());
        new LoadUsersTask().execute();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                userRepository.performLogin(username, password, (isSuccess, user) -> {
                    runOnUiThread(() -> {
                        if (isSuccess && user != null) {
                            String role = user.getRole();
                            redirectToNextActivity(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login falhou. Tente Novamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }

    private void redirectToNextActivity(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("ID", (int) user.getId());  // Converte para int
        sessionManager.saveUserId((int)user.getId());
        startActivity(intent);
    }

    private AppDatabase getAppDatabase() {
        return AppDatabase.getInstance(getApplicationContext());
    }

    private class LoadUsersTask extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... voids) {
            // Execute a lógica para carregar os usuários em segundo plano
            User adminUser = new User();
            adminUser.setUsername("admin1");
            adminUser.setPassword("6d4525c2a21f9be1cca9e41f3aa402e0765ee5fcc3e7fea34a169b1730ae386e"); // Senha: admin_password
            adminUser.setRole("admin");
            getAppDatabase().userDao().insertUser(adminUser);


            return getAppDatabase().userDao().getAllUsers();
        }

        @Override
        protected void onPostExecute(List<User> users) {
        }
    }

}