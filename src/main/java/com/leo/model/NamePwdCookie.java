package com.leo.model;

/**
 * Created by liang on 2017/6/8.
 */
public class NamePwdCookie {

    private String name;
    private String pwd;
    private String code;
    private String cookie;
    private boolean loginError;

    public NamePwdCookie() {
    }

    public NamePwdCookie(String name, String pwd, String code, String cookie) {
        this.name = name;
        this.pwd = pwd;
        this.code = code;
        this.cookie = cookie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public boolean isLoginError() {
        return loginError;
    }

    public void setLoginError(boolean loginError) {
        this.loginError = loginError;
    }
}
