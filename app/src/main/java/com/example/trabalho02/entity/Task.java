package com.example.trabalho02.entity;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Task implements Serializable {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");


    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "taskName")
    private String taskName;


    @ColumnInfo(name = "dueDate")
    private LocalDateTime dueDate;


    public Task(String taskName, LocalDateTime dueDate) {
    this.taskName = taskName;
    this.dueDate = dueDate;
    }

    public Task(String nomeTask, int idUserLogado, LocalDateTime dataHoraCompleta) {
        this.taskName = nomeTask;
        this.userId = idUserLogado;
        this.dueDate = dataHoraCompleta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTaskName() {
        return taskName;
    }


    public LocalDateTime getDueDate() {
        return dueDate;
    }


    @Override
    public String toString() {
        return getTaskName() + " - " +
                (dueDate != null ? dueDate.format(formatter) : "null");
    }
}