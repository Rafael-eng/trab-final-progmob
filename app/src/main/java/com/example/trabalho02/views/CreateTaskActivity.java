package com.example.trabalho02.views;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho02.NotificationReceiver;
import com.example.trabalho02.R;
import com.example.trabalho02.SessionManager;
import com.example.trabalho02.database.AppDatabase;
import com.example.trabalho02.databinding.ActivityCreateTaskBinding;
import com.example.trabalho02.entity.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {
    private AppDatabase db;
    private ActivityCreateTaskBinding binding;
    private int dbTaskID;
    private int idUserLogado;
    private Task dbTask;
    private TextInputLayout textInputLayoutDate, textInputLayoutTime;
    private SessionManager sessionManager;

    private EditText editTextDate, editTextTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);


        textInputLayoutDate = findViewById(R.id.textInputLayoutDate);
        editTextDate = findViewById(R.id.editTextDate);
        textInputLayoutTime = findViewById(R.id.textInputLayoutTime);
        editTextTime = findViewById(R.id.editTextTime);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        db = AppDatabase.getDatabase(getApplicationContext());
        dbTaskID = getIntent().getIntExtra("TASK_SELECIONADA_ID", -1);
        idUserLogado = sessionManager.getUserId();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbTaskID >= 0) {
            new LoadTaskAsyncTask().execute(dbTaskID);
        } else {
            binding.btnExcluirTask.setVisibility(View.GONE);
        }
    }

    private void getDBTask(Task task) {
        dbTask = task;
        binding.edtTask.setText(dbTask.getTaskName());
        binding.editTextDate.setText(dbTask.getDueDate().toLocalDate().toString());
        binding.editTextTime.setText(dbTask.getDueDate().toLocalTime().toString());
    }

    public void salvarTask(View view) {
        String nomeTask = binding.edtTask.getText().toString();
        String dataString = binding.editTextDate.getText().toString();
        String horaString = binding.editTextTime.getText().toString();
        if (nomeTask.equals("") || dataString.equals("") || horaString.equals("")) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        String dataHoraCompletaString = dataString + " " + horaString;
        LocalDateTime dataHoraCompleta = null;

        try {
            // Tente analisar no formato "dd/MM/yyyy HH:mm"
            dataHoraCompleta = LocalDateTime.parse(dataHoraCompletaString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (DateTimeParseException e1) {
            try {
                // Tente analisar no formato "yyyy-MM-dd HH:mm"
                dataHoraCompleta = LocalDateTime.parse(dataHoraCompletaString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if(dataHoraCompleta == null)
                dataHoraCompleta = LocalDateTime.parse(dataHoraCompletaString, DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm"));
            } catch (DateTimeParseException e2) {
                e2.printStackTrace();
            }
        }

        Task task = new Task(nomeTask, idUserLogado, dataHoraCompleta);



        new SaveTaskAsyncTask().execute(task);
    }

    private class LoadTaskAsyncTask extends AsyncTask<Integer, Void, Task> {

        @Override
        protected Task doInBackground(Integer... params) {
            return db.taskDao().getTaskById(params[0]);
        }

        @Override
        protected void onPostExecute(Task task) {
            super.onPostExecute(task);
            if (task != null) {
                getDBTask(task);
            }
        }
    }

    private class SaveTaskAsyncTask extends AsyncTask<Task, Void, Void> {

        @Override
        protected Void doInBackground(Task... tasks) {
            if (dbTask != null) {
                tasks[0].setId(dbTaskID);
                db.taskDao().updateTask(tasks[0]);
            } else {
                db.taskDao().insertTask(tasks[0]);
            }
            agendarNotificacao(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(CreateTaskActivity.this,
                    dbTask != null ? "Task atualizado com sucesso." : "Task criado com sucesso.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void excluirTask(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Exclusão de Task")
                .setMessage("Deseja excluir esse Task?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluir();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluir() {
        if (dbTask != null) {
            new DeleteTaskAsyncTask().execute(dbTask);
        }
    }

    private class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {

        @Override
        protected Void doInBackground(Task... tasks) {
            db.taskDao().deleteTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(CreateTaskActivity.this, "Task excluído com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void voltar(View view) {
        finish();
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formattedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        editTextDate.setText(formattedDate);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        editTextTime.setText(formattedTime);
                    }
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void agendarNotificacao(Task task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("task_channel_id2", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("mensagem", task.getTaskName());
        PendingIntent pendingIntent = createPendingIntent(this, 123, intent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeInMillis = task.getDueDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    private PendingIntent createPendingIntent(Context context, int notificationId, Intent intent) {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }

        return PendingIntent.getBroadcast(context, notificationId, intent, flags);
    }
}

