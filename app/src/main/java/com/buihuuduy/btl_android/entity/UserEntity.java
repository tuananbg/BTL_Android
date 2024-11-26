package com.buihuuduy.btl_android.entity;

public class UserEntity
{
    private Integer id;
    private String email;
    private String fullName;
    private String password;
    private int isAdmin;

    public UserEntity(String email, String fullName, String password, int isAdmin) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public UserEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
}
