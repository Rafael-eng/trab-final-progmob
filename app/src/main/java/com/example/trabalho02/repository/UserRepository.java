package com.example.trabalho02.repository;

import com.example.trabalho02.Utils.PasswordHashUtils;
import com.example.trabalho02.dao.UserDao;
import com.example.trabalho02.entity.User;

public class UserRepository {
    private UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public interface LoginCallback {
        void onLoginResult(boolean isSuccess, User user);
    }

    public void performLogin(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            User user = userDao.getUserByUsername(username);

            if (user != null && PasswordHashUtils.verifyPassword(password, user.getPassword())) {
                // As credenciais são válidas
                callback.onLoginResult(true, user);
            } else {
                // As credenciais são inválidas
                callback.onLoginResult(false, null);
            }
        }).start();
    }
}
