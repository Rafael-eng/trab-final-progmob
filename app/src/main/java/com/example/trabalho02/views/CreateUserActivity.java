package com.example.trabalho02.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho02.R;
import com.example.trabalho02.Utils.PasswordHashUtils;
import com.example.trabalho02.database.AppDatabase;
import com.example.trabalho02.entity.User;

public class CreateUserActivity extends AppCompatActivity {

    private EditText editTextNewUsername, editTextNewPassword;
    private Spinner spinnerRole;
    private Button buttonCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_user);

        Spinner spinnerRole = findViewById(R.id.spinnerRole);

        String[] roleOptions = {"admin", "user"};

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleOptions);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRole.setAdapter(roleAdapter);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        buttonCreateUser = findViewById(R.id.buttonCreateUser);

        Spinner finalSpinnerRole = spinnerRole;

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

        buttonCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminUser()) {
                    String newUsername = editTextNewUsername.getText().toString();
                    String newPassword = editTextNewPassword.getText().toString();
                    String role = finalSpinnerRole.getSelectedItem().toString();

                    createUser(newUsername, newPassword);


                } else {
                    Toast.makeText(CreateUserActivity.this, "Sem autorização.", Toast.LENGTH_SHORT).show();                }
            }
        });
    }

    private boolean isAdminUser() {

        return true;
    }

    private void createUser(String username, String password) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            User existingUser = db.userDao().getUserByUsername(username);

            if (existingUser == null) {
                String hashedPassword = PasswordHashUtils.hashPassword(password);
                Spinner spinnerRole = findViewById(R.id.spinnerRole);
                String selectedRole = spinnerRole.getSelectedItem().toString();

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(hashedPassword);
                newUser.setRole(selectedRole);

                db.userDao().insertUser(newUser);

                runOnUiThread(() ->
                        Toast.makeText(this, "Usuario criado com sucesso", Toast.LENGTH_SHORT).show()
                );

                editTextNewPassword.setText(null);
                editTextNewUsername.setText(null);

            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "O usuário já existe. Escolha um nome de usuário diferente.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}