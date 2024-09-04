package com.alimama.alimamaspringboot.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {
    @JsonProperty("legal_name")
    private String legalName;
    private String email;
    private String tckn;
    private String pwd_hash;
    private String role;

    public String getLegalName() {return legalName;}
    public void setLegalName(String legalName) {this.legalName = legalName;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getTckn() {return tckn;}
    public void setTckn(String tckn) {this.tckn = tckn;}

    public String getPassword() {return pwd_hash;}
    public void setPassword(String pwd_hash) {this.pwd_hash = pwd_hash;}

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}