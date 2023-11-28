package com.example.trabalho02;

import com.example.trabalho02.entity.Task;

public interface TaskItemClickListener {
    void editTaskItem(Task taskItem);
    void completeTaskItem(Task taskItem);
}