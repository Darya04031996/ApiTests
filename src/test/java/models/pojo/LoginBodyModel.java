package models.pojo;

import lombok.Data;

@Data
public class LoginBodyModel {
    private String email;
    private String password;
    private String job;
    private String name;
}

