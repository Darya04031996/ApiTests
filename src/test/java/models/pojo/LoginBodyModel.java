package models.pojo;

import lombok.Setter;

public class LoginBodyModel {
//    String authData = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\"}";
@Setter
String email, password, job, name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void getJob(String job) {
        this.job= job;
    }

    public void getName() {this.name= name;
    }
}
