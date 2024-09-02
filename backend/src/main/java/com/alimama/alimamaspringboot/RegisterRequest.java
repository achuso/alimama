package com.alimama.alimamaspringboot;

public class RegisterRequest {
    private String legal_name;
    private String email;
    private String tckn;
    private String password;
    private String role;

    public String getLegal_name() {return legal_name;}

    public void setLegal_name(String legal_name) {this.legal_name = legal_name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getTckn() {return tckn;}

    public void setTckn(String tckn) {this.tckn = tckn;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}