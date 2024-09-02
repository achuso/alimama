package com.alimama.alimamaspringboot;

public class RegisterRequest {
    private String legalName;
    private String email;
    private String tckn;
    private String password;
    private String role;

    public String getLegalName() {return legalName;}

    public void setLegalName(String legalName) {this.legalName = legalName;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getTckn() {return tckn;}

    public void setTckn(String tckn) {this.tckn = tckn;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}
}
