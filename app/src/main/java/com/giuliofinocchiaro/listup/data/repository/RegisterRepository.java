package com.giuliofinocchiaro.listup.data.repository;

import com.giuliofinocchiaro.mygiancarlobarmenuapplicationvera.data.source.RegisterDataSource;

public class RegisterRepository {
    private final RegisterDataSource dataSource;

    public RegisterRepository(RegisterDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void register(String username, String password, String email, String name, String surname, RegisterDataSource.RegisterCallback callback) {
        dataSource.register(username, password, email, name, surname, callback);
    }
}