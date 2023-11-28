package com.example.trabalho02.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trabalho02.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    User loginUser(String username, String password);

    @Insert
    void insertUser(User user);

    @Query("SELECT * from user where id = :id ")
    User findUserById(int id);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE username = :username")
    User getUserByUsername(String username);

}
