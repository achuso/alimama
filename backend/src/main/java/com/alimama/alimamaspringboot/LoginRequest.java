package com.alimama.alimamaspringboot;

public class LoginRequest {
    private String email;
    private String pwd_hash;

    public LoginRequest() {}

    public LoginRequest(String email, String pwd_hash) {
        this.email = email;
        this.pwd_hash = pwd_hash;
    }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return pwd_hash;}
    public void setPassword(String password) {this.pwd_hash = pwd_hash;}
}
