package com.example.trabalho02.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trabalho02.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM Task WHERE userId = :userId")
    List<Task> getUserTasks(long userId);

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task tipo);

    @Delete
    void deleteTask(Task dbTask);

    @Query("SELECT * FROM Task WHERE id = :dbTaskID")
    Task getTaskById(int dbTaskID);
}