package models.lombok;

import lombok.Data;

@Data
public class LoginBodyLombokModel {
    private String email;      // Email пользователя
    private String password;   // Пароль пользователя
}