package com.alimama.alimamaspringboot.auth;

public class RegisterRequest {
    private String legal_name;
    private String email;
    private String tckn;
    private String pwd_hash;
    private String role;

    public String getLegalName() {return legal_name;}
    public void setLegalName(String legal_name) {this.legal_name = legal_name;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getTckn() {return tckn;}
    public void setTckn(String tckn) {this.tckn = tckn;}

    public String getPassword() {return pwd_hash;}
    public void setPassword(String pwd_hash) {this.pwd_hash = pwd_hash;}

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}