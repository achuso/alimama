package com.alimama.alimamaspringboot;

public class RegisterRequest {
    private String fullName;
    private String brand_name;
    private String email;
    private String tckn;
    private String password;
    private String role;

    public String getFullName() {return fullName;}

    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getBrandName() {return brand_name;}

    public void setBrandName(String brand_name) {this.brand_name = brand_name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getTckn() {return tckn;}

    public void setTckn(String tckn) {this.tckn = tckn;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}
}
