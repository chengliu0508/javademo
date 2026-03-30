package com.example.demo.vo;

public class LogoutResponseVO {
    private boolean ok;

    public LogoutResponseVO() {}

    public LogoutResponseVO(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}

